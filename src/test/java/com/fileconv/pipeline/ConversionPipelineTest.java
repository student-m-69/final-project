package com.fileconv.pipeline;

import com.fileconv.cli.CliArgs;
import com.fileconv.exception.UnsupportedFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConversionPipelineTest {

    private ConversionPipeline pipeline;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pipeline = new ConversionPipeline();
    }

    @Test
    void executeJsonToCsv() throws IOException {
        Path input = tempDir.resolve("data.json");
        Files.writeString(input, """
                [{"name": "Alice", "age": 30}]
                """);
        Path output = tempDir.resolve("data.csv");

        pipeline.execute(new CliArgs(input, output));

        assertTrue(Files.exists(output));
        String content = Files.readString(output);
        assertTrue(content.contains("name"));
        assertTrue(content.contains("Alice"));
    }

    @Test
    void executeWithUnsupportedFormat() throws IOException {
        Path input = tempDir.resolve("data.yaml");
        Files.writeString(input, "key: value");
        Path output = tempDir.resolve("data.json");

        assertThrows(UnsupportedFormatException.class,
                () -> pipeline.execute(new CliArgs(input, output)));
    }

    @Test
    void executeSameFormat() throws IOException {
        Path input = tempDir.resolve("data.json");
        Files.writeString(input, """
                {"name": "Alice"}
                """);
        Path output = tempDir.resolve("copy.json");

        pipeline.execute(new CliArgs(input, output));

        assertTrue(Files.exists(output));
        String content = Files.readString(output);
        assertTrue(content.contains("Alice"));
    }
}
