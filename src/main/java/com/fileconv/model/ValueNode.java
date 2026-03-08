package com.fileconv.model;

public final class ValueNode extends DataNode {

    private final String value;

    public ValueNode(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    public int asInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Cannot convert '" + value + "' to int", e);
        }
    }

    public long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Cannot convert '" + value + "' to long", e);
        }
    }

    public double asDouble() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Cannot convert '" + value + "' to double", e);
        }
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }
}
