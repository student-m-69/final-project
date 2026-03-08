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

class CsvToJsonTest {

    @TempDir
    Path tempDir;

    @Test
    void convertSimpleCsvToJson() throws IOException {
        Path input = tempDir.resolve("users.csv");
        Files.writeString(input, """
                name,age,city
                Alice,30,Vienna
                Bob,25,Berlin
                """);
        Path output = tempDir.resolve("users.json");

        new ConversionPipeline().execute(new CliArgs(input, output));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.isArray());
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).get("name").asText());
        assertEquals("30", result.get(0).get("age").asText());
    }

    @Test
    void convertNestedCsvToJson() throws IOException {
        Path input = tempDir.resolve("nested.csv");
        Files.writeString(input, """
                name,address.city,address.zip
                Alice,Vienna,1010
                """);
        Path output = tempDir.resolve("nested.json");

        new ConversionPipeline().execute(new CliArgs(input, output));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.isArray());
        assertEquals("Vienna", result.get(0).get("address").get("city").asText());
    }
}
