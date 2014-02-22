package net.bramp.dissector.node;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.bramp.dissector.io.DataPositionInputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author bramp
 */
public class ArrayNode extends TreeNode {

    int startOffset = 0;

    public ArrayNode() {}

    public ArrayNode(int startOffset) {
        this.startOffset = startOffset;
    }

    public <T extends Node> T addChild(T node) {
        return this.addChild("[" + startOffset++ + "]", node);
    }

    public ArrayNode read(DataPositionInputStream in) throws IOException {
        super.read(in);
        return this;
    }
}
