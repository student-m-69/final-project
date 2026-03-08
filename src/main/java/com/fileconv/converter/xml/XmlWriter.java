package com.fileconv.converter.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fileconv.converter.DataWriter;
import com.fileconv.exception.WriteException;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.NullNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;

import java.io.IOException;
import java.nio.file.Path;

public class XmlWriter implements DataWriter {

    private final XmlMapper xmlMapper;

    public XmlWriter() {
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    @Override
    public void write(DataNode data, Path outputFile) throws WriteException {
        try {
            DataNode toWrite = data;
            if (data instanceof ArrayNode arr) {
                ObjectNode wrapper = new ObjectNode();
                wrapper.put("item", data);
                toWrite = wrapper;
            }
            com.fasterxml.jackson.databind.JsonNode jsonNode = convert(toWrite);
            xmlMapper.writeValue(outputFile.toFile(), jsonNode);
        } catch (IOException e) {
            throw new WriteException("Cannot write to '" + outputFile + "': " + e.getMessage(), e);
        }
    }

    private com.fasterxml.jackson.databind.JsonNode convert(DataNode node) {
        if (node instanceof ObjectNode obj) {
            com.fasterxml.jackson.databind.node.ObjectNode jsonObj = JsonNodeFactory.instance.objectNode();
            for (var entry : obj.entries().entrySet()) {
                jsonObj.set(entry.getKey(), convert(entry.getValue()));
            }
            return jsonObj;
        }
        if (node instanceof ArrayNode arr) {
            com.fasterxml.jackson.databind.node.ArrayNode jsonArr = JsonNodeFactory.instance.arrayNode();
            for (DataNode element : arr) {
                jsonArr.add(convert(element));
            }
            return jsonArr;
        }
        if (node instanceof ValueNode val) {
            return JsonNodeFactory.instance.textNode(val.asString());
        }
        if (node instanceof NullNode) {
            return JsonNodeFactory.instance.nullNode();
        }
        return JsonNodeFactory.instance.nullNode();
    }
}
