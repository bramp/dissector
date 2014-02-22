package net.bramp.dissector.disk;

import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.node.ByteNode;
import net.bramp.dissector.node.EnumNode;
import net.bramp.dissector.node.FixedStringNode;
import net.bramp.dissector.node.TreeNode;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class CHSAddressNode extends TreeNode {

    public CHSAddressNode read(DataPositionInputStream in) throws IOException {
        //addChild("skip", new FixedStringNode().read(in, 16) );
        addChild("head", new ByteNode().read(in).base(16) );

        // TODO Fix
        addChild("sector", new ByteNode().read(in) );
        addChild("cylinder", new ByteNode().read(in) );
        return this;
    }

}
