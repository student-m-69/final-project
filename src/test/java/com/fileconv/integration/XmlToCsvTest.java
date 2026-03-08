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

class XmlToCsvTest {

    @TempDir
    Path tempDir;

    @Test
    void convertSimpleXmlToCsv() throws IOException {
        Path input = tempDir.resolve("data.xml");
        Files.writeString(input, """
                <person>
                    <name>Alice</name>
                    <age>30</age>
                </person>
                """);
        Path output = tempDir.resolve("data.csv");

        new ConversionPipeline().execute(new CliArgs(input, output));

        List<String> lines = Files.readAllLines(output);
        assertTrue(lines.size() >= 2);
        assertTrue(lines.get(0).contains("name"));
    }
}
