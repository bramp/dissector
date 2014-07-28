package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * @author bramp
 */
public class ShortNode extends Node<Integer> {

    int value;
	byte radix = 10;

    public ShortNode() {}

    public ShortNode read(ExtendedRandomAccessFile in) throws IOException {
        return read(in, false);
    }

    public ShortNode read(ExtendedRandomAccessFile in, boolean signed) throws IOException {
        super.setPos(in, 2);
        value = signed ? in.readShort() : in.readUnsignedShort();
        return this;
    }

	public ShortNode base(int radix) {
		this.radix = (byte)radix;
		return this;
	}

    public Integer value() {
        return value;
    }

    public String toString() {
	    if (radix == 16)
		    return "0x" + Integer.toString(value, radix).toUpperCase();
        return Integer.toString(value, radix);
    }
}
