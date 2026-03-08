package com.fileconv.converter.csv;

import com.fileconv.exception.ParseException;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    private CsvParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new CsvParser();
    }

    @Test
    void parseSimpleCsv() throws IOException {
        Path file = tempDir.resolve("simple.csv");
        Files.writeString(file, """
                name,age,city
                Alice,30,Vienna
                Bob,25,Berlin
                """);

        DataNode result = parser.parse(file);
        assertTrue(result.isArray());
        ArrayNode arr = result.asArray();
        assertEquals(2, arr.size());

        ObjectNode first = arr.get(0).asObject();
        assertEquals("Alice", first.get("name").asValue().asString());
        assertEquals("30", first.get("age").asValue().asString());
        assertEquals("Vienna", first.get("city").asValue().asString());
    }

    @Test
    void parseQuotedFields() throws IOException {
        Path file = tempDir.resolve("quoted.csv");
        Files.writeString(file, "name,description\nAlice,\"Has a \"\"nickname\"\"\"\nBob,\"Likes, commas\"\n");

        DataNode result = parser.parse(file);
        ArrayNode arr = result.asArray();
        assertEquals(2, arr.size());
        assertEquals("Has a \"nickname\"", arr.get(0).asObject().get("description").asValue().asString());
        assertEquals("Likes, commas", arr.get(1).asObject().get("description").asValue().asString());
    }

    @Test
    void parseNestedHeaders() throws IOException {
        Path file = tempDir.resolve("nested.csv");
        Files.writeString(file, """
                name,address.city,address.zip
                Alice,Vienna,1010
                """);

        DataNode result = parser.parse(file);
        ArrayNode arr = result.asArray();
        ObjectNode row = arr.get(0).asObject();
        assertTrue(row.has("address"));
        ObjectNode address = row.get("address").asObject();
        assertEquals("Vienna", address.get("city").asValue().asString());
        assertEquals("1010", address.get("zip").asValue().asString());
    }

    @Test
    void parseEmptyCsv() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "");

        DataNode result = parser.parse(file);
        assertTrue(result.isArray());
        assertEquals(0, result.asArray().size());
    }

    @Test
    void parseHeaderOnly() throws IOException {
        Path file = tempDir.resolve("header_only.csv");
        Files.writeString(file, "name,age,city\n");

        DataNode result = parser.parse(file);
        assertTrue(result.isArray());
        assertEquals(0, result.asArray().size());
    }

    @Test
    void throwsOnNonexistentFile() {
        Path file = tempDir.resolve("missing.csv");
        assertThrows(ParseException.class, () -> parser.parse(file));
    }
}
