package net.bramp.dissector.png;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.ByteNode;
import net.bramp.dissector.node.FixedStringNode;
import net.bramp.dissector.node.TreeNode;

import java.io.IOException;

/**
 * @author bramp
 */
public class PngHeaderNode extends TreeNode {

    public PngHeaderNode() {}

    public PngHeaderNode read(ExtendedRandomAccessFile in) throws IOException {

        addChild("0x89", new ByteNode().read(in) );
        addChild("Magic", new FixedStringNode(3).read(in) );
        addChild("DOS line ending", new FixedStringNode(2).read(in) );
        addChild("0x1A", new ByteNode().read(in) );
        addChild("0x0A", new ByteNode().read(in) );

        return this;
    }
}
