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

	Node<T> in;

    public EnumNode(Map<T, String> values, Node<T> in) {
        this.values = values;
	    this.in = in;
    }

    public T value() {
        return in.value();
    }

    public String name() {
        return values.containsKey(in.value()) ? values.get(in.value()) : "unknown";
    }

    public String toString() {
        return in.value() + " - " + name();
    }

    public Map<T, String> getPossibleValues() {
        return ImmutableMap.copyOf(values);
    }

	@Override
	public long getEnd() {
		return in.getEnd();
	}

	@Override
	public long getStart() {
		return in.getStart();
	}
}
