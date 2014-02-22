package net.bramp.dissector.java;

import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.node.FixedStringNode;
import net.bramp.dissector.node.IntNode;
import net.bramp.dissector.node.ShortNode;
import net.bramp.dissector.node.TreeNode;

import java.io.IOException;

/**
 * @author bramp
 */
public class AttributeInfoNode extends TreeNode {

    @Override
    public TreeNode read(DataPositionInputStream in) throws IOException {

        addChild( "name_index", new ShortNode().read(in) );

        IntNode len = addChild( "length", new IntNode().read(in) );
        addChild( "info", new FixedStringNode().read(in, len.value()) );

        return this;
    }
}
