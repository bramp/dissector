package net.bramp.dissector.java;

import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.node.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class ConstantPoolNode extends TreeNode {

    static final Map<Integer, String> constantTypes = ImmutableMap.<Integer, String>builder()
        .put(1, "Utf8")
        .put(3, "Integer")
        .put(4, "Float")
        .put(5, "Long")
        .put(6, "Double")
        .put(7, "Class")
        .put(8, "String")
        .put(9, "Fieldref")
        .put(10, "Methodref")
        .put(11, "InterfaceMethodref")
        .put(12, "NameAndType")
        .build();

    public ConstantPoolNode() {}

    public ConstantPoolNode read(DataPositionInputStream in) throws IOException {
        super.read(in);

        EnumNode type = addChild( "tag", new EnumNode(constantTypes).read(in, 1) );
        //setTitle( type.name() );

        switch (type.value()) {
            case 7:
                addChild( "name_index", new ShortNode().read(in) );
                break;

            case 9: case 10: case 11:
                addChild( "class_index", new ShortNode().read(in) );
                addChild( "name_and_type_index", new ShortNode().read(in) );
                break;

            case 8: // String
                addChild( "string_index", new ShortNode().read(in) );
                break;

            case 3: // int
                addChild( "bytes", new IntNode().read(in) );
                break;
            case 4: // float
                addChild( "bytes", new FloatNode().read(in) );
                break;

            case 5: // long
            case 6: // double
                // TODO
                addChild( "TODO", new SkipNode().read(in, 8) );
                break;

            case 12: // NameAndType
                addChild( "name_index", new ShortNode().read(in) );
                addChild( "descriptor_index", new ShortNode().read(in) );
                break;

            case 1: // String
                ShortNode len = addChild( "length", new ShortNode().read(in) );
                addChild( "bytes", new FixedStringNode().read(in, len.value()) );
                break;

            default:
                // We can't continue parsing as constant types are variable length
                throw new IOException("Unknown Constant Pool Type");
        }

        return this;
    }
}
