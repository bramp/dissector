package net.bramp.dissector.node;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.boon.primitive.ByteBuf;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Null terminated String
 * @author bramp
 */
public class NullStringNode extends Node {

	final static int MAX_DISPLAY_WIDTH = 50;

	final int terminator;
	final boolean swallowTerminator;

	Charset charset = Charsets.UTF_8;
    String value;

    public NullStringNode() {
	    this(0);
    }

	public NullStringNode(int terminator) {
		this(terminator, true);
	}

	public NullStringNode(int terminator, boolean swallowTerminator) {
		this.terminator = terminator;
		this.swallowTerminator = swallowTerminator;
	}

	public NullStringNode read(ExtendedRandomAccessFile in) throws IOException {
		return this.read(in, Long.MAX_VALUE, charset);
	}

    public NullStringNode read(ExtendedRandomAccessFile in, long maxLength) throws IOException {
        return this.read(in, maxLength, charset);
    }

    public String value() {
        return value;
    }

    /**
     * @param in
     * @param maxLength in bytes
     * @param charset
     * @throws java.io.IOException
     */
    public NullStringNode read(ExtendedRandomAccessFile in, long maxLength, Charset charset) throws IOException {
        this.charset = Preconditions.checkNotNull(charset);
	    setPos(in);

	    final ByteBuf buf = ByteBuf.create(16);

	    int b = 0;
	    while (maxLength > 0) {
		    b = in.read();
		    if (b == -1)
		        break;

		    if (b == terminator) {
			    if (!swallowTerminator) {
				    in.rewind(1);
			    }
			    break;
		    }

			buf.addByte( b );
		    maxLength--;
	    }

	    value = new String(buf.toBytes() , charset);
	    end = in.getFilePointer();

        return this;
    }

    public String toString() {
        // Replace some characters to make them printable
        return StringUtils.abbreviate( StringEscapeUtils.escapeJava(value), MAX_DISPLAY_WIDTH );
    }
}
