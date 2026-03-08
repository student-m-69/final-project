package com.fileconv.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.NullNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    private JsonWriter writer;
    private ObjectMapper mapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        writer = new JsonWriter();
        mapper = new ObjectMapper();
    }

    @Test
    void writeFlatObject() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Alice"));
        obj.put("age", new ValueNode("30"));

        Path output = tempDir.resolve("output.json");
        writer.write(obj, output);

        JsonNode result = mapper.readTree(output.toFile());
        assertEquals("Alice", result.get("name").asText());
        assertEquals(30, result.get("age").asInt());
    }

    @Test
    void writeNestedObject() throws IOException {
        ObjectNode address = new ObjectNode();
        address.put("city", new ValueNode("Vienna"));

        ObjectNode person = new ObjectNode();
        person.put("name", new ValueNode("Bob"));
        person.put("address", address);

        ObjectNode root = new ObjectNode();
        root.put("person", person);

        Path output = tempDir.resolve("output.json");
        writer.write(root, output);

        JsonNode result = mapper.readTree(output.toFile());
        assertEquals("Vienna", result.get("person").get("address").get("city").asText());
    }

    @Test
    void writeArray() throws IOException {
        ArrayNode arr = new ArrayNode();
        ObjectNode item1 = new ObjectNode();
        item1.put("id", new ValueNode("1"));
        ObjectNode item2 = new ObjectNode();
        item2.put("id", new ValueNode("2"));
        arr.add(item1);
        arr.add(item2);

        Path output = tempDir.resolve("output.json");
        writer.write(arr, output);

        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.isArray());
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("id").asInt());
    }

    @Test
    void writeNullValues() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("key", NullNode.INSTANCE);

        Path output = tempDir.resolve("output.json");
        writer.write(obj, output);

        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.get("key").isNull());
    }

    @Test
    void writeBooleanValues() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("active", new ValueNode("true"));
        obj.put("deleted", new ValueNode("false"));

        Path output = tempDir.resolve("output.json");
        writer.write(obj, output);

        JsonNode result = mapper.readTree(output.toFile());
        assertTrue(result.get("active").asBoolean());
        assertFalse(result.get("deleted").asBoolean());
    }

    @Test
    void writeNumericValues() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("integer", new ValueNode("42"));
        obj.put("decimal", new ValueNode("3.14"));

        Path output = tempDir.resolve("output.json");
        writer.write(obj, output);

        JsonNode result = mapper.readTree(output.toFile());
        assertEquals(42, result.get("integer").asInt());
        assertEquals(3.14, result.get("decimal").asDouble(), 0.001);
    }
}
