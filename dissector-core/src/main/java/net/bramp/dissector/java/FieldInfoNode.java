package net.bramp.dissector.java;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;

import java.io.IOException;

/**
 * @author bramp
 */
public class FieldInfoNode extends TreeNode {

    public FieldInfoNode read(ExtendedRandomAccessFile in) throws IOException {

        addChild( "access_flags", new MaskNode(JavaClassDissector.accessFlags, new ShortNode().read(in)) );
        addChild( "name_index", new ShortNode().read(in) );
        addChild( "descriptor_index", new ShortNode().read(in) );

        ShortNode len = addChild( "attributes_count", new ShortNode().read(in) );
        ArrayNode attributes = addChild( "attributes", new ArrayNode().read(in) );
        for (int i = 0; i < len.value(); i++) {
            attributes.addChild( new AttributeInfoNode().read(in) );
        }

        return this;
    }
}
