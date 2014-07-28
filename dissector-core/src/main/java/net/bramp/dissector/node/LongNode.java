package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;
import java.math.BigInteger;

/**
 * 64 bit number
 * @author bramp
 */
public class LongNode extends Node<BigInteger> {

	BigInteger value;
    byte radix = 10;

    public LongNode() {}

    public LongNode read(ExtendedRandomAccessFile in) throws IOException {
        return read(in, false);
    }

    public LongNode read(ExtendedRandomAccessFile in, boolean signed) throws IOException {
        super.setPos(in, 8);

	    byte[] num = new byte[8];
	    in.read(num);

	    value = new BigInteger( signed ? 1 : -1, num );

        return this;
    }

    public LongNode base(int radix) {
        this.radix = (byte)radix;
        return this;
    }

    public BigInteger value() {
        return value;
    }

    public String toString() {
        if (radix == 16)
            return "0x" + value.toString(radix).toUpperCase();
        return value.toString(radix);
    }
}
