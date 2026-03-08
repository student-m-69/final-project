package com.fileconv.model;

public sealed abstract class DataNode permits ObjectNode, ArrayNode, ValueNode, NullNode {

    public boolean isObject() {
        return this instanceof ObjectNode;
    }

    public boolean isArray() {
        return this instanceof ArrayNode;
    }

    public boolean isValue() {
        return this instanceof ValueNode;
    }

    public boolean isNull() {
        return this instanceof NullNode;
    }

    public ObjectNode asObject() {
        if (this instanceof ObjectNode obj) {
            return obj;
        }
        throw new IllegalStateException("Node is not an ObjectNode: " + getClass().getSimpleName());
    }

    public ArrayNode asArray() {
        if (this instanceof ArrayNode arr) {
            return arr;
        }
        throw new IllegalStateException("Node is not an ArrayNode: " + getClass().getSimpleName());
    }

    public ValueNode asValue() {
        if (this instanceof ValueNode val) {
            return val;
        }
        throw new IllegalStateException("Node is not a ValueNode: " + getClass().getSimpleName());
    }
}
