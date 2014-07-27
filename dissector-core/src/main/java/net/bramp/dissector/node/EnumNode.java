package net.bramp.dissector.node;

import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class EnumNode<T> extends Node<T> {

    final Map<T, String> values;

    T value;

    public EnumNode(Map<T, String> values, Node<T> in) {
        this.values = values;
	    super.setPos(in.getStart(), in.getEnd());
	    this.value = in.value();
    }

    public T value() {
        return value;
    }

    public String name() {
        return values.containsKey(value) ? values.get(value) : "unknown";
    }

    public String toString() {
        return value + " - " + name();
    }

    public Map<T, String> getPossibleValues() {
        return ImmutableMap.copyOf(values);
    }
}
