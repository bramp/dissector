package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * Skips some data
 * @author bramp
 */
public class SkipNode extends Node {

    long value;

    public SkipNode() {}

    public SkipNode read(ExtendedRandomAccessFile in, long length) throws IOException {
        super.setPos(in, length);
        value = length;
        in.skipBytes(length);
        return this;
    }

    public String toString() {
        return "{" + value + " bytes}";
    }
}
