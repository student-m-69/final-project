package com.fileconv.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileconv.converter.DataParser;
import com.fileconv.exception.ParseException;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.NullNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class JsonParser implements DataParser {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public DataNode parse(Path inputFile) throws ParseException {
        try {
            JsonNode root = mapper.readTree(inputFile.toFile());
            return convert(root);
        } catch (IOException e) {
            throw new ParseException("Failed to parse JSON file '" + inputFile + "': " + e.getMessage(), e);
        }
    }

    private DataNode convert(JsonNode node) {
        if (node == null || node.isNull()) {
            return NullNode.INSTANCE;
        }
        if (node.isObject()) {
            ObjectNode obj = new ObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                obj.put(entry.getKey(), convert(entry.getValue()));
            }
            return obj;
        }
        if (node.isArray()) {
            ArrayNode arr = new ArrayNode();
            for (JsonNode element : node) {
                arr.add(convert(element));
            }
            return arr;
        }
        return new ValueNode(node.asText());
    }
}
