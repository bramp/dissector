package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * @author bramp
 */
public class ByteNode extends Node {

    short value;
    byte radix = 10;

    public ByteNode() {}

    public ByteNode read(ExtendedRandomAccessFile in) throws IOException {
        return this.read(in, false);
    }

    public ByteNode read(ExtendedRandomAccessFile in, boolean signed) throws IOException {
        super.setPos(in, 1);
        value = signed ? in.readByte() : (short)in.readUnsignedByte();
        return this;
    }

    public ByteNode base(int radix) {
        this.radix = (byte)radix;
        return this;
    }

    public String toString() {
        if (radix == 16)
            return "0x" + Long.toString(value, radix).toUpperCase();
        return Long.toString(value, radix);
    }

}
