package com.fileconv.converter.json;

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

class JsonParserTest {

    private JsonParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new JsonParser();
    }

    @Test
    void parseFlatObject() throws IOException {
        Path file = tempDir.resolve("flat.json");
        Files.writeString(file, """
                {"name": "Alice", "age": 30}
                """);

        DataNode result = parser.parse(file);

        assertTrue(result.isObject());
        ObjectNode obj = result.asObject();
        assertEquals("Alice", obj.get("name").asValue().asString());
        assertEquals("30", obj.get("age").asValue().asString());
    }

    @Test
    void parseNestedObject() throws IOException {
        Path file = tempDir.resolve("nested.json");
        Files.writeString(file, """
                {
                    "person": {
                        "name": "Bob",
                        "address": {
                            "city": "Vienna",
                            "zip": "AZ1000"
                        }
                    }
                }
                """);

        DataNode result = parser.parse(file);
        ObjectNode person = result.asObject().get("person").asObject();
        ObjectNode address = person.get("address").asObject();
        assertEquals("Vienna", address.get("city").asValue().asString());
        assertEquals("AZ1000", address.get("zip").asValue().asString());
    }

    @Test
    void parseArray() throws IOException {
        Path file = tempDir.resolve("array.json");
        Files.writeString(file, """
                [
                    {"id": 1, "name": "Alice"},
                    {"id": 2, "name": "Bob"}
                ]
                """);

        DataNode result = parser.parse(file);
        assertTrue(result.isArray());
        ArrayNode arr = result.asArray();
        assertEquals(2, arr.size());
        assertEquals("Alice", arr.get(0).asObject().get("name").asValue().asString());
        assertEquals("Bob", arr.get(1).asObject().get("name").asValue().asString());
    }

    @Test
    void parseMixedTypes() throws IOException {
        Path file = tempDir.resolve("mixed.json");
        Files.writeString(file, """
                {
                    "string": "hello",
                    "number": 42,
                    "bool": true,
                    "nullable": null,
                    "array": [1, 2, 3]
                }
                """);

        DataNode result = parser.parse(file);
        ObjectNode obj = result.asObject();
        assertEquals("hello", obj.get("string").asValue().asString());
        assertEquals("42", obj.get("number").asValue().asString());
        assertEquals("true", obj.get("bool").asValue().asString());
        assertTrue(obj.get("nullable").isNull());
        assertTrue(obj.get("array").isArray());
        assertEquals(3, obj.get("array").asArray().size());
    }

    @Test
    void parseEmptyObject() throws IOException {
        Path file = tempDir.resolve("empty.json");
        Files.writeString(file, "{}");

        DataNode result = parser.parse(file);
        assertTrue(result.isObject());
        assertEquals(0, result.asObject().size());
    }

    @Test
    void parseEmptyArray() throws IOException {
        Path file = tempDir.resolve("empty_arr.json");
        Files.writeString(file, "[]");

        DataNode result = parser.parse(file);
        assertTrue(result.isArray());
        assertEquals(0, result.asArray().size());
    }

    @Test
    void throwsOnInvalidJson() throws IOException {
        Path file = tempDir.resolve("invalid.json");
        Files.writeString(file, "{ invalid json }");

        assertThrows(ParseException.class, () -> parser.parse(file));
    }

    @Test
    void throwsOnNonexistentFile() {
        Path file = tempDir.resolve("missing.json");
        assertThrows(ParseException.class, () -> parser.parse(file));
    }
}
