package com.fileconv.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileconv.cli.CliArgs;
import com.fileconv.pipeline.ConversionPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XmlToJsonTest {

    @TempDir
    Path tempDir;

    @Test
    void convertSimpleXmlToJson() throws IOException {
        Path input = tempDir.resolve("data.xml");
        Files.writeString(input, """
                <person>
                    <name>Alice</name>
                    <age>30</age>
                </person>
                """);
        Path output = tempDir.resolve("data.json");

        new ConversionPipeline().execute(new CliArgs(input, output));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.isObject());
        assertEquals("Alice", result.get("root").get("name").asText());
    }

    @Test
    void convertXmlWithRepeatedElementsToJson() throws IOException {
        Path input = tempDir.resolve("data.xml");
        Files.writeString(input, """
                <people>
                    <person>Alice</person>
                    <person>Bob</person>
                </people>
                """);
        Path output = tempDir.resolve("data.json");

        new ConversionPipeline().execute(new CliArgs(input, output));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.get("root").get("person").isArray());
    }
}
