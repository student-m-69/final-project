package com.fileconv.integration;

import com.fileconv.cli.CliArgs;
import com.fileconv.pipeline.ConversionPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvToXmlTest {

    @TempDir
    Path tempDir;

    @Test
    void convertCsvToXml() throws IOException {
        Path input = tempDir.resolve("data.csv");
        Files.writeString(input, """
                name,age
                Alice,30
                Bob,25
                """);
        Path output = tempDir.resolve("data.xml");

        new ConversionPipeline().execute(new CliArgs(input, output));

        String content = Files.readString(output);
        assertTrue(content.contains("Alice"));
        assertTrue(content.contains("Bob"));
        assertTrue(content.startsWith("<?xml"));
    }
}
