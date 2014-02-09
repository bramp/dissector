package net.bramp.dissector.node;

import com.google.common.base.Preconditions;
import net.bramp.dissector.io.DataPositionInputStream;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class EnumNode extends Node {

    final Map<Integer, String> values;

    int value;

    public EnumNode(Map<Integer, String> values) {
        this.values = values;
    }

    public EnumNode read(DataPositionInputStream in, int length) throws IOException {
        super.setPos(in, length);

        value = in.readUnsignedIntOfLength(length);

        return this;
    }

    public int value() {
        return value;
    }

    public String name() {
        return values.containsKey(value) ? values.get(value) : "unknown";
    }

    public String toString() {
        return Long.toString(value) + " - " + name();
    }
}
