package com.fileconv.converter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fileconv.converter.DataWriter;
import com.fileconv.exception.WriteException;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.NullNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;

import java.io.IOException;
import java.nio.file.Path;

public class JsonWriter implements DataWriter {

    private final ObjectMapper mapper;

    public JsonWriter() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void write(DataNode data, Path outputFile) throws WriteException {
        try {
            com.fasterxml.jackson.databind.JsonNode jsonNode = convert(data);
            mapper.writeValue(outputFile.toFile(), jsonNode);
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
            String value = val.asString();
            if (value == null) {
                return JsonNodeFactory.instance.nullNode();
            }
            if ("true".equals(value) || "false".equals(value)) {
                return JsonNodeFactory.instance.booleanNode(Boolean.parseBoolean(value));
            }
            try {
                long longVal = Long.parseLong(value);
                if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
                    return JsonNodeFactory.instance.numberNode((int) longVal);
                }
                return JsonNodeFactory.instance.numberNode(longVal);
            } catch (NumberFormatException ignored) {
            }
            try {
                double doubleVal = Double.parseDouble(value);
                return JsonNodeFactory.instance.numberNode(doubleVal);
            } catch (NumberFormatException ignored) {
            }
            return JsonNodeFactory.instance.textNode(value);
        }
        if (node instanceof NullNode) {
            return JsonNodeFactory.instance.nullNode();
        }
        return JsonNodeFactory.instance.nullNode();
    }
}
