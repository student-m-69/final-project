package com.fileconv.converter.csv;

import com.fileconv.model.ArrayNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvWriterTest {

    private CsvWriter writer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        writer = new CsvWriter();
    }

    @Test
    void writeFlatRecords() throws IOException {
        ArrayNode data = new ArrayNode();

        ObjectNode row1 = new ObjectNode();
        row1.put("name", new ValueNode("Alice"));
        row1.put("age", new ValueNode("30"));
        data.add(row1);

        ObjectNode row2 = new ObjectNode();
        row2.put("name", new ValueNode("Bob"));
        row2.put("age", new ValueNode("25"));
        data.add(row2);

        Path output = tempDir.resolve("output.csv");
        writer.write(data, output);

        List<String> lines = Files.readAllLines(output);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("name"));
        assertTrue(lines.get(0).contains("age"));
        assertTrue(lines.get(1).contains("Alice"));
        assertTrue(lines.get(2).contains("Bob"));
    }

    @Test
    void writeNestedRecords() throws IOException {
        ArrayNode data = new ArrayNode();

        ObjectNode address = new ObjectNode();
        address.put("city", new ValueNode("Vienna"));
        address.put("zip", new ValueNode("1010"));

        ObjectNode row = new ObjectNode();
        row.put("name", new ValueNode("Alice"));
        row.put("address", address);
        data.add(row);

        Path output = tempDir.resolve("output.csv");
        writer.write(data, output);

        List<String> lines = Files.readAllLines(output);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("address.city"));
        assertTrue(lines.get(0).contains("address.zip"));
    }

    @Test
    void writeSingleObject() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("key", new ValueNode("value"));

        Path output = tempDir.resolve("output.csv");
        writer.write(obj, output);

        List<String> lines = Files.readAllLines(output);
        assertEquals(2, lines.size());
    }
}
