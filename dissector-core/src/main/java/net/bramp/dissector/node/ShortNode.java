package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * @author bramp
 */
public class ShortNode extends Node {

    int value;

    public ShortNode() {}

    public ShortNode read(ExtendedRandomAccessFile in) throws IOException {
        return read(in, false);
    }

    public ShortNode read(ExtendedRandomAccessFile in, boolean signed) throws IOException {
        super.setPos(in, 2);
        value = signed ? in.readShort() : in.readUnsignedShort();
        return this;
    }

    public int value() {
        return value;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
