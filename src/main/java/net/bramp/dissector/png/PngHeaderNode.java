package net.bramp.dissector.png;

import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.node.ByteNode;
import net.bramp.dissector.node.FixedStringNode;
import net.bramp.dissector.node.TreeNode;

import java.io.IOException;

/**
 * @author bramp
 */
public class PngHeaderNode extends TreeNode {

    public PngHeaderNode() {}

    public PngHeaderNode read(DataPositionInputStream in) throws IOException {

        addChild("0x89", new ByteNode().read(in) );
        addChild("Magic", new FixedStringNode().read(in, 3) );
        addChild("DOS line ending", new FixedStringNode().read(in, 2) );
        addChild("0x1A", new ByteNode().read(in) );
        addChild("0x0A", new ByteNode().read(in) );

        return this;
    }
}
