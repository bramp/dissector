package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * @author bramp
 */
public class IntNode extends Node<Long> {

    long value;
    byte radix = 10;
	boolean signed = false;
	boolean bigEndian = true;

    public IntNode() {}

    public IntNode read(ExtendedRandomAccessFile in) throws IOException {
	    super.setPos(in, 4);
	    value = signed ? in.readInt() : in.readUnsignedInt();
	    return this;
    }

    public IntNode base(int radix) {
        this.radix = (byte)radix;
        return this;
    }

	public IntNode signed() {
		signed = true;
		return this;
	}

	public IntNode unsigned() {
		signed = false;
		return this;
	}

	public IntNode big() {
		bigEndian = true;
		return this;
	}

	public IntNode little() {
		bigEndian = false;
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
