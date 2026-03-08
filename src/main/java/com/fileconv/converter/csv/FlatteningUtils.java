package com.fileconv.converter.csv;

import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.NullNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FlatteningUtils {

    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("^(.+?)\\[(\\d+)]$");

    private FlatteningUtils() {
    }

    public static Map<String, String> flatten(ObjectNode node) {
        Map<String, String> result = new LinkedHashMap<>();
        flattenRecursive("", node, result);
        return result;
    }

    private static void flattenRecursive(String prefix, DataNode node, Map<String, String> result) {
        if (node instanceof ObjectNode obj) {
            for (var entry : obj.entries().entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenRecursive(key, entry.getValue(), result);
            }
        } else if (node instanceof ArrayNode arr) {
            for (int i = 0; i < arr.size(); i++) {
                String key = prefix + "[" + i + "]";
                flattenRecursive(key, arr.get(i), result);
            }
        } else if (node instanceof ValueNode val) {
            result.put(prefix, val.asString());
        } else if (node instanceof NullNode) {
            result.put(prefix, "");
        }
    }

    public static ObjectNode unflatten(Map<String, String> flat) {
        ObjectNode root = new ObjectNode();
        for (var entry : flat.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            setNestedValue(root, key, value);
        }
        return root;
    }

    private static void setNestedValue(ObjectNode root, String path, String value) {
        String[] segments = splitPath(path);
        DataNode current = root;

        for (int i = 0; i < segments.length - 1; i++) {
            String segment = segments[i];
            String nextSegment = segments[i + 1];
            boolean nextIsIndex = isArrayIndex(nextSegment);

            Matcher matcher = ARRAY_INDEX_PATTERN.matcher(segment);
            if (matcher.matches()) {
                String fieldName = matcher.group(1);
                int index = Integer.parseInt(matcher.group(2));

                ObjectNode currentObj = (ObjectNode) current;
                if (!currentObj.has(fieldName)) {
                    currentObj.put(fieldName, new ArrayNode());
                }
                ArrayNode arr = currentObj.get(fieldName).asArray();
                while (arr.size() <= index) {
                    arr.add(nextIsIndex ? new ObjectNode() : new ObjectNode());
                }
                current = arr.get(index);
            } else {
                ObjectNode currentObj = (ObjectNode) current;
                if (!currentObj.has(segment)) {
                    currentObj.put(segment, nextIsIndex ? new ObjectNode() : new ObjectNode());
                }
                DataNode child = currentObj.get(segment);
                if (!child.isObject() && !child.isArray()) {
                    ObjectNode newObj = new ObjectNode();
                    currentObj.put(segment, newObj);
                    child = newObj;
                }
                current = child;
            }
        }

        String lastSegment = segments[segments.length - 1];
        Matcher matcher = ARRAY_INDEX_PATTERN.matcher(lastSegment);
        if (matcher.matches()) {
            String fieldName = matcher.group(1);
            int index = Integer.parseInt(matcher.group(2));
            ObjectNode currentObj = (ObjectNode) current;
            if (!currentObj.has(fieldName)) {
                currentObj.put(fieldName, new ArrayNode());
            }
            ArrayNode arr = currentObj.get(fieldName).asArray();
            DataNode valueNode = (value == null || value.isEmpty()) ? NullNode.INSTANCE : new ValueNode(value);
            while (arr.size() <= index) {
                arr.add(NullNode.INSTANCE);
            }
            // Replace the placeholder at index
            ArrayNode newArr = new ArrayNode();
            for (int i = 0; i < arr.size(); i++) {
                if (i == index) {
                    newArr.add(valueNode);
                } else {
                    newArr.add(arr.get(i));
                }
            }
            currentObj.put(fieldName, newArr);
        } else {
            ObjectNode currentObj = (ObjectNode) current;
            DataNode valueNode = (value == null || value.isEmpty()) ? NullNode.INSTANCE : new ValueNode(value);
            currentObj.put(lastSegment, valueNode);
        }
    }

    private static String[] splitPath(String path) {
        return path.split("\\.");
    }

    private static boolean isArrayIndex(String segment) {
        return ARRAY_INDEX_PATTERN.matcher(segment).matches();
    }
}
