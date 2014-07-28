package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * Generic number of specific length
 * @author bramp
 */
public class NumberNode extends Node<Long> {

    long value;
    byte radix = 10;

    public NumberNode() {}

    public NumberNode read(ExtendedRandomAccessFile in, int length) throws IOException {
        return read(in, length, false);
    }

    public NumberNode read(ExtendedRandomAccessFile in, int length, boolean signed) throws IOException {
        super.setPos(in, length);
        value = signed ? in.readIntOfLength(length) : in.readUnsignedIntOfLength(length);
        return this;
    }

    public NumberNode base(int radix) {
        this.radix = (byte)radix;
        return this;
    }

    public Long value() {
        return value;
    }

    public String toString() {
        if (radix == 16)
            return "0x" + Long.toString(value, radix).toUpperCase();
        return Long.toString(value, radix);
    }
}
