package com.fileconv.integration;

import com.fileconv.cli.CliArgs;
import com.fileconv.pipeline.ConversionPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonToXmlTest {

    @TempDir
    Path tempDir;

    @Test
    void convertJsonObjectToXml() throws IOException {
        Path input = tempDir.resolve("data.json");
        Files.writeString(input, """
                {"name": "Alice", "age": 30}
                """);
        Path output = tempDir.resolve("data.xml");

        new ConversionPipeline().execute(new CliArgs(input, output));

        String content = Files.readString(output);
        assertTrue(content.contains("<name>Alice</name>"));
        assertTrue(content.contains("<age>30</age>"));
    }

    @Test
    void convertJsonWithArrayToXml() throws IOException {
        Path input = tempDir.resolve("data.json");
        Files.writeString(input, """
                {"items": ["apple", "banana"]}
                """);
        Path output = tempDir.resolve("data.xml");

        new ConversionPipeline().execute(new CliArgs(input, output));

        String content = Files.readString(output);
        assertTrue(content.contains("apple"));
        assertTrue(content.contains("banana"));
    }
}
