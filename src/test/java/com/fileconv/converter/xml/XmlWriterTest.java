package com.fileconv.converter.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XmlWriterTest {

    private XmlWriter writer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        writer = new XmlWriter();
    }

    @Test
    void writeSimpleObject() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Alice"));
        obj.put("age", new ValueNode("30"));

        Path output = tempDir.resolve("output.xml");
        writer.write(obj, output);

        String content = Files.readString(output);
        assertTrue(content.contains("<name>Alice</name>"));
        assertTrue(content.contains("<age>30</age>"));
    }

    @Test
    void writeNestedObject() throws IOException {
        ObjectNode address = new ObjectNode();
        address.put("city", new ValueNode("Vienna"));

        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Bob"));
        obj.put("address", address);

        Path output = tempDir.resolve("output.xml");
        writer.write(obj, output);

        String content = Files.readString(output);
        assertTrue(content.contains("<city>Vienna</city>"));
    }

    @Test
    void writeArray() throws IOException {
        ArrayNode arr = new ArrayNode();
        arr.add(new ValueNode("Alice"));
        arr.add(new ValueNode("Bob"));

        ObjectNode root = new ObjectNode();
        root.put("person", arr);

        Path output = tempDir.resolve("output.xml");
        writer.write(root, output);

        String content = Files.readString(output);
        assertTrue(content.contains("Alice"));
        assertTrue(content.contains("Bob"));
    }

    @Test
    void outputContainsXmlDeclaration() throws IOException {
        ObjectNode obj = new ObjectNode();
        obj.put("key", new ValueNode("value"));

        Path output = tempDir.resolve("output.xml");
        writer.write(obj, output);

        String content = Files.readString(output);
        assertTrue(content.startsWith("<?xml"));
    }
}
