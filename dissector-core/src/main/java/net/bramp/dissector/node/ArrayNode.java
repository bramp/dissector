package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * @author bramp
 */
public class ArrayNode extends TreeNode {

    int startOffset = 0;

    public ArrayNode() {}

	/**
	 *
	 * @param startOffset Index this array starts at, defaults to zero
	 */
    public ArrayNode(int startOffset) {
        this.startOffset = startOffset;
    }

    public <T extends Node> T addChild(T node) {
        return this.addChild("[" + startOffset++ + "]", node);
    }

    public ArrayNode read(ExtendedRandomAccessFile in) throws IOException {
        super.read(in);
        return this;
    }
}
