package com.fileconv.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataNodeTest {

    @Test
    void objectNodePutAndGet() {
        ObjectNode obj = new ObjectNode();
        obj.put("name", new ValueNode("Alice"));
        obj.put("age", new ValueNode("30"));

        assertEquals("Alice", obj.get("name").asValue().asString());
        assertEquals("30", obj.get("age").asValue().asString());
        assertEquals(2, obj.size());
    }

    @Test
    void objectNodePreservesInsertionOrder() {
        ObjectNode obj = new ObjectNode();
        obj.put("z", new ValueNode("1"));
        obj.put("a", new ValueNode("2"));
        obj.put("m", new ValueNode("3"));

        List<String> keys = List.copyOf(obj.keys());
        assertEquals(List.of("z", "a", "m"), keys);
    }

    @Test
    void objectNodeHas() {
        ObjectNode obj = new ObjectNode();
        obj.put("key", new ValueNode("value"));

        assertTrue(obj.has("key"));
        assertFalse(obj.has("missing"));
    }

    @Test
    void arrayNodeAddAndGet() {
        ArrayNode arr = new ArrayNode();
        arr.add(new ValueNode("a"));
        arr.add(new ValueNode("b"));

        assertEquals("a", arr.get(0).asValue().asString());
        assertEquals("b", arr.get(1).asValue().asString());
        assertEquals(2, arr.size());
    }

    @Test
    void arrayNodeIterable() {
        ArrayNode arr = new ArrayNode();
        arr.add(new ValueNode("x"));
        arr.add(new ValueNode("y"));

        int count = 0;
        for (DataNode node : arr) {
            assertTrue(node.isValue());
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void valueNodeConversions() {
        ValueNode intNode = new ValueNode("42");
        assertEquals(42, intNode.asInt());
        assertEquals(42L, intNode.asLong());
        assertEquals(42.0, intNode.asDouble());

        ValueNode boolNode = new ValueNode("true");
        assertTrue(boolNode.asBoolean());

        ValueNode strNode = new ValueNode("hello");
        assertEquals("hello", strNode.asString());
    }

    @Test
    void valueNodeInvalidConversion() {
        ValueNode node = new ValueNode("not_a_number");
        assertThrows(IllegalStateException.class, node::asInt);
        assertThrows(IllegalStateException.class, node::asLong);
        assertThrows(IllegalStateException.class, node::asDouble);
    }

    @Test
    void nullNodeSingleton() {
        assertSame(NullNode.INSTANCE, NullNode.INSTANCE);
        assertTrue(NullNode.INSTANCE.isNull());
    }

    @Test
    void typeChecks() {
        DataNode obj = new ObjectNode();
        DataNode arr = new ArrayNode();
        DataNode val = new ValueNode("v");
        DataNode nul = NullNode.INSTANCE;

        assertTrue(obj.isObject());
        assertFalse(obj.isArray());

        assertTrue(arr.isArray());
        assertFalse(arr.isValue());

        assertTrue(val.isValue());
        assertFalse(val.isNull());

        assertTrue(nul.isNull());
        assertFalse(nul.isObject());
    }

    @Test
    void safeCastingThrowsOnWrongType() {
        DataNode val = new ValueNode("v");
        assertThrows(IllegalStateException.class, val::asObject);
        assertThrows(IllegalStateException.class, val::asArray);

        DataNode obj = new ObjectNode();
        assertThrows(IllegalStateException.class, obj::asValue);
    }

    @Test
    void safeCastingReturnsCorrectType() {
        ObjectNode obj = new ObjectNode();
        assertSame(obj, ((DataNode) obj).asObject());

        ArrayNode arr = new ArrayNode();
        assertSame(arr, ((DataNode) arr).asArray());

        ValueNode val = new ValueNode("v");
        assertSame(val, ((DataNode) val).asValue());
    }
}
