package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 *
 * @author bramp
 */
public class NullNode extends Node<Void> {

    public NullNode() {}

	public NullNode read(ExtendedRandomAccessFile in) throws IOException {
        super.setPos(in, 0);
        return this;
    }

	@Override
	public Void value() {
		return null;
	}

    public String toString() {
        return "{null}";
    }
}
