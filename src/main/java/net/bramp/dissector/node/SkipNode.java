package net.bramp.dissector.node;

import net.bramp.dissector.io.DataPositionInputStream;

import java.io.IOException;

/**
 * Skips some data
 * @author bramp
 */
public class SkipNode extends Node {

    long value;

    public SkipNode() {}

    public SkipNode read(DataPositionInputStream in, long length) throws IOException {
        super.setPos(in, length);
        value = length;
        in.skip(length);
        return this;
    }

    public String toString() {
        return "{" + value + " bytes}";
    }
}
