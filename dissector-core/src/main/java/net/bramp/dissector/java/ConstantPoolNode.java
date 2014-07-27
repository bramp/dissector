package net.bramp.dissector.java;

import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class ConstantPoolNode extends TreeNode {

    static final Map<Short, String> constantTypes = ImmutableMap.<Short, String>builder()
        .put((short) 1, "Utf8")
        .put((short) 3, "Integer")
        .put((short) 4, "Float")
        .put((short) 5, "Long")
        .put((short) 6, "Double")
        .put((short) 7, "Class")
        .put((short) 8, "String")
        .put((short) 9, "Fieldref")
        .put((short) 10, "Methodref")
        .put((short) 11, "InterfaceMethodref")
        .put((short) 12, "NameAndType")
		//
		.put((short) 15, "MethodHandle")
		.put((short) 16, "MethodType")
		.put((short) 18, "InvokeDynamic")
        .build();

	private short type;

	public ConstantPoolNode() {}

    public ConstantPoolNode read(ExtendedRandomAccessFile in) throws IOException {
        super.read(in);

        EnumNode<Short> type = addChild( "tag", new EnumNode<Short>(constantTypes, new ByteNode().read(in) ) );
        setTitle( type.name() );

	    this.type = type.value();
        switch (this.type) {
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

            case 1: // String (UTF-8)
                ShortNode len = addChild( "length", new ShortNode().read(in) );
                addChild( "bytes", new FixedStringNode().read(in, len.value()) );
                break;

            default:
                // If we don't know it we can't continue parsing as constant types are variable length
                throw new IOException("Unknown Constant Pool Type: " + type.value());
        }

        return this;
    }

	public short getType() {
		return type;
	}
}
