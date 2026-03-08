package com.fileconv.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class ArrayNode extends DataNode implements Iterable<DataNode> {

    private final List<DataNode> elements = new ArrayList<>();

    public void add(DataNode element) {
        elements.add(element);
    }

    public DataNode get(int index) {
        return elements.get(index);
    }

    public List<DataNode> elements() {
        return Collections.unmodifiableList(elements);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<DataNode> iterator() {
        return elements.iterator();
    }
}
