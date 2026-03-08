package com.fileconv.converter.csv;

import com.fileconv.model.ArrayNode;
import com.fileconv.model.NullNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlatteningUtilsTest {

    @Test
    void flattenSimpleObject() {
        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Alice"));
        obj.put("age", new ValueNode("30"));

        Map<String, String> flat = FlatteningUtils.flatten(obj);

        assertEquals("Alice", flat.get("name"));
        assertEquals("30", flat.get("age"));
        assertEquals(2, flat.size());
    }

    @Test
    void flattenNestedObject() {
        ObjectNode address = new ObjectNode();
        address.put("city", new ValueNode("Vienna"));
        address.put("zip", new ValueNode("1010"));

        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Alice"));
        obj.put("address", address);

        Map<String, String> flat = FlatteningUtils.flatten(obj);

        assertEquals("Alice", flat.get("name"));
        assertEquals("Vienna", flat.get("address.city"));
        assertEquals("1010", flat.get("address.zip"));
    }

    @Test
    void flattenDeeplyNestedObject() {
        ObjectNode inner = new ObjectNode();
        inner.put("value", new ValueNode("deep"));

        ObjectNode mid = new ObjectNode();
        mid.put("inner", inner);

        ObjectNode outer = new ObjectNode();
        outer.put("mid", mid);

        ObjectNode root = new ObjectNode();
        root.put("outer", outer);

        Map<String, String> flat = FlatteningUtils.flatten(root);
        assertEquals("deep", flat.get("outer.mid.inner.value"));
    }

    @Test
    void flattenWithArray() {
        ArrayNode arr = new ArrayNode();
        arr.add(new ValueNode("a"));
        arr.add(new ValueNode("b"));

        ObjectNode obj = new ObjectNode();
        obj.put("items", arr);

        Map<String, String> flat = FlatteningUtils.flatten(obj);
        assertEquals("a", flat.get("items[0]"));
        assertEquals("b", flat.get("items[1]"));
    }

    @Test
    void flattenWithNullValues() {
        ObjectNode obj = new ObjectNode();
        obj.put("key", NullNode.INSTANCE);

        Map<String, String> flat = FlatteningUtils.flatten(obj);
        assertEquals("", flat.get("key"));
    }

    @Test
    void flattenEmptyObject() {
        ObjectNode obj = new ObjectNode();
        Map<String, String> flat = FlatteningUtils.flatten(obj);
        assertTrue(flat.isEmpty());
    }

    @Test
    void unflattenSimple() {
        Map<String, String> flat = new LinkedHashMap<>();
        flat.put("name", "Alice");
        flat.put("age", "30");

        ObjectNode result = FlatteningUtils.unflatten(flat);
        assertEquals("Alice", result.get("name").asValue().asString());
        assertEquals("30", result.get("age").asValue().asString());
    }

    @Test
    void unflattenNested() {
        Map<String, String> flat = new LinkedHashMap<>();
        flat.put("address.city", "Vienna");
        flat.put("address.zip", "1010");
        flat.put("name", "Alice");

        ObjectNode result = FlatteningUtils.unflatten(flat);
        assertEquals("Alice", result.get("name").asValue().asString());
        ObjectNode address = result.get("address").asObject();
        assertEquals("Vienna", address.get("city").asValue().asString());
        assertEquals("1010", address.get("zip").asValue().asString());
    }

    @Test
    void unflattenWithArrayIndex() {
        Map<String, String> flat = new LinkedHashMap<>();
        flat.put("items[0]", "a");
        flat.put("items[1]", "b");

        ObjectNode result = FlatteningUtils.unflatten(flat);
        assertTrue(result.get("items").isArray());
        assertEquals("a", result.get("items").asArray().get(0).asValue().asString());
        assertEquals("b", result.get("items").asArray().get(1).asValue().asString());
    }

    @Test
    void roundTripFlattenUnflatten() {
        ObjectNode address = new ObjectNode();
        address.put("city", new ValueNode("Vienna"));

        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Alice"));
        obj.put("address", address);

        Map<String, String> flat = FlatteningUtils.flatten(obj);
        ObjectNode restored = FlatteningUtils.unflatten(flat);

        assertEquals("Alice", restored.get("name").asValue().asString());
        assertEquals("Vienna", restored.get("address").asObject().get("city").asValue().asString());
    }
}
