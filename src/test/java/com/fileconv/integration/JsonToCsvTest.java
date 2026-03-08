package com.fileconv.integration;

import com.fileconv.cli.CliArgs;
import com.fileconv.pipeline.ConversionPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonToCsvTest {

    @TempDir
    Path tempDir;

    @Test
    void convertFlatJsonArrayToCsv() throws IOException {
        Path input = tempDir.resolve("users.json");
        Files.writeString(input, """
                [
                    {"name": "Alice", "age": 30, "city": "Vienna"},
                    {"name": "Bob", "age": 25, "city": "Berlin"}
                ]
                """);
        Path output = tempDir.resolve("users.csv");

        new ConversionPipeline().execute(new CliArgs(input, output));

        List<String> lines = Files.readAllLines(output);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("name"));
        assertTrue(lines.get(1).contains("Alice"));
        assertTrue(lines.get(2).contains("Bob"));
    }

    @Test
    void convertNestedJsonToCsv() throws IOException {
        Path input = tempDir.resolve("nested.json");
        Files.writeString(input, """
                [
                    {"name": "Alice", "address": {"city": "Vienna", "zip": "1010"}}
                ]
                """);
        Path output = tempDir.resolve("nested.csv");

        new ConversionPipeline().execute(new CliArgs(input, output));

        List<String> lines = Files.readAllLines(output);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("address.city"));
        assertTrue(lines.get(1).contains("Vienna"));
    }
}
