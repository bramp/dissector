package net.bramp.dissector.java;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.ArrayNode;
import net.bramp.dissector.node.MaskNode;
import net.bramp.dissector.node.ShortNode;
import net.bramp.dissector.node.TreeNode;

import java.io.IOException;

/**
 * @author bramp
 */
public class MethodInfoNode extends TreeNode {

    public MethodInfoNode read(ExtendedRandomAccessFile in) throws IOException {

        addChild( "access_flags", new MaskNode(JavaClassDissector.accessFlags).read(in, 2) );
        addChild( "name_index", new ShortNode().read(in) );
        addChild( "descriptor_index", new ShortNode().read(in) );

        ShortNode len = addChild( "attributes_count", new ShortNode().read(in) );
        ArrayNode attributes = addChild( "attributes", new ArrayNode().read(in) );
        for (int i = 0; i < len.value(); i++) {
            attributes.addChild(new AttributeInfoNode().read(in));
        }

        return this;
    }
}
