package com.fileconv.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ObjectNode extends DataNode {

    private final LinkedHashMap<String, DataNode> fields = new LinkedHashMap<>();

    public void put(String key, DataNode value) {
        fields.put(key, value);
    }

    public DataNode get(String key) {
        return fields.get(key);
    }

    public boolean has(String key) {
        return fields.containsKey(key);
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(fields.keySet());
    }

    public Map<String, DataNode> entries() {
        return Collections.unmodifiableMap(fields);
    }

    public int size() {
        return fields.size();
    }
}
