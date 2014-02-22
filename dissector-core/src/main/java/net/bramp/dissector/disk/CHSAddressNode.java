package net.bramp.dissector.disk;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.ByteNode;
import net.bramp.dissector.node.TreeNode;

import java.io.IOException;

/**
 * @author bramp
 */
public class CHSAddressNode extends TreeNode {

    public CHSAddressNode read(ExtendedRandomAccessFile in) throws IOException {
        //addChild("skip", new FixedStringNode().read(in, 16) );
        addChild("head", new ByteNode().read(in).base(16) );

        // TODO Fix
        addChild("sector", new ByteNode().read(in) );
        addChild("cylinder", new ByteNode().read(in) );
        return this;
    }

}
