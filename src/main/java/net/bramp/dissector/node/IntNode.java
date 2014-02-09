package net.bramp.dissector.node;

import net.bramp.dissector.io.DataPositionInputStream;

import java.io.IOException;

/**
 * @author bramp
 */
public class IntNode extends Node {

    long value;
    byte radix = 10;

    public IntNode() {}

    public IntNode read(DataPositionInputStream in) throws IOException {
        return read(in, false);
    }

    public IntNode read(DataPositionInputStream in, boolean signed) throws IOException {
        super.setPos(in, 4);
        value = signed ? in.readInt() : in.readUnsignedInt();
        return this;
    }

    public IntNode base(int radix) {
        this.radix = (byte)radix;
        return this;
    }

    public long value() {
        return value;
    }

    public String toString() {
        if (radix == 16)
            return "0x" + Long.toString(value, radix).toUpperCase();
        return Long.toString(value, radix);
    }
}
