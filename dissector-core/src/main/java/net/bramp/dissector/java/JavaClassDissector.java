package net.bramp.dissector.java;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;

import java.io.IOException;
import java.util.Map;

/**
 * Parses Java Class file
 * http://docs.oracle.com/javase/specs/jvms/se5.0/html/ClassFile.doc.html#2877
 * @author bramp
 */
public class JavaClassDissector extends Dissector {

    static final Map<Integer, String> majorVersions = ImmutableMap.<Integer, String>builder()
        .put(45, "JDK 1.1")
        .put(46, "JDK 1.2")
        .put(47, "JDK 1.3")
        .put(48, "JDK 1.4")
        .put(49, "J2SE 5.0")
        .put(50, "J2SE 6.0")
        .put(51, "J2SE 7")
		.put(52, "J2SE 8")
        .build();

    static final Map<Integer, String> accessFlags = ImmutableMap.<Integer, String>builder()
        .put(0x0001, "public")
        .put(0x0002, "private")
        .put(0x0004, "protected")
        .put(0x0008, "static")
        .put(0x0010, "final")
        .put(0x0020, "super")
        .put(0x0040, "volatile")
        .put(0x0080, "transient")
        .put(0x0200, "interface")
        .put(0x0400, "abstract")
        .build();


    public JavaClassDissector() {}

    public JavaClassDissector read(ExtendedRandomAccessFile in) throws IOException {
        Preconditions.checkNotNull(in);

        ShortNode len;

        addChild( "Magic", new IntNode().read(in).base(16) );
        addChild( "Minor", new ShortNode().read(in) );
        addChild( "Major", new EnumNode(majorVersions, new ShortNode().read(in)) );

        len = addChild( "Constant pool count", new ShortNode().read(in, false) );

        ArrayNode constantPool = addChild( "Constant pool", new ArrayNode(1).read(in) );
        for (int i = 1; i < len.value(); i++) {
	        ConstantPoolNode poolNode = new ConstantPoolNode().read(in);
            constantPool.addChild( poolNode );

	        // If the last type was a Long or a Double, we have to skip one tag
	        if (poolNode.getType() == 5 || poolNode.getType() == 6) {
		        constantPool.addChild(new NullNode().read(in));
		        i++;
	        }
        }

        addChild( "Access flags", new MaskNode(accessFlags, new ShortNode().read(in)) );
        addChild( "This index",   new ShortNode().read(in, false) );
        addChild( "Super index",  new ShortNode().read(in, false) );

        len = addChild( "Interface count", new ShortNode().read(in, false) );

	    if (len.value() > 0) {
		    ArrayNode interfaces = addChild("Interfaces", new ArrayNode().read(in));
		    for (int i = 0; i < len.value(); i++) {
			    interfaces.addChild(new ShortNode().read(in));
		    }
	    }

        len = addChild( "Field count", new ShortNode().read(in, false) );
	    if (len.value() > 0) {
		    ArrayNode fields = addChild("Fields", new ArrayNode().read(in));
		    for (int i = 0; i < len.value(); i++) {
			    fields.addChild(new FieldInfoNode().read(in));
		    }
	    }

        len = addChild( "Method count", new ShortNode().read(in, false) );
	    if (len.value() > 0) {
		    ArrayNode methods = addChild("Methods", new ArrayNode().read(in));
		    for (int i = 0; i < len.value(); i++) {
			    methods.addChild(new MethodInfoNode().read(in));
		    }
	    }

        len = addChild( "Attribute count", new ShortNode().read(in, false) );
	    if (len.value() > 0) {
		    ArrayNode attributes = addChild("Attributes", new ArrayNode().read(in));
		    for (int i = 0; i < len.value(); i++) {
			    attributes.addChild(new AttributeInfoNode().read(in));
		    }
	    }

        return this;
    }
}