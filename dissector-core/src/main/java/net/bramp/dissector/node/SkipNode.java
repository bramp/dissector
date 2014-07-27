package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * Skips some data
 * @author bramp
 */
public class SkipNode extends Node<Void> {

    long skipped;

    public SkipNode() {}

	public SkipNode read(ExtendedRandomAccessFile in, long length) throws IOException {
        super.setPos(in, length);
        skipped = length;
        in.skipBytes(length);
        return this;
    }

	@Override
	public Void value() {
		return null;
	}

    public String toString() {
        return "{" + skipped + " bytes}";
    }
}
