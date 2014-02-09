package net.bramp.dissector.node;

import net.bramp.dissector.io.DataPositionInputStream;

import java.io.IOException;

/**
 * @author bramp
 */
public class FloatNode extends Node {

    float value;

    public FloatNode() {}

    public FloatNode read(DataPositionInputStream in) throws IOException {
        super.setPos(in, 4);
        value = in.readFloat();
        return this;
    }

    public float value() {
        return value;
    }

    public String toString() {
        return Float.toString(value);
    }
}
