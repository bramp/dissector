package net.bramp.dissector.java;

import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.node.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class FieldInfoNode extends TreeNode {

    public FieldInfoNode read(DataPositionInputStream in) throws IOException {

        addChild( "access_flags", new MaskNode(JavaClassDissector.accessFlags).read(in, 2) );
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
