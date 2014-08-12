package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * Skips some data
 * @author bramp
 */
public class SkipNode extends Node<Void> {

    final long skipped;

    public SkipNode(long length) {
	    skipped = length;
    }

	public SkipNode(Node<? extends Number> lengthNode) {
		skipped = lengthNode.value().longValue();
	}

	public SkipNode read(ExtendedRandomAccessFile in) throws IOException {
        super.setPos(in, skipped);
        in.skipBytes(skipped);
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
