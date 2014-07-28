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

    Charset charset = Charsets.UTF_8;
    String value;

    public NullStringNode() {}

	public NullStringNode read(ExtendedRandomAccessFile in) throws IOException {
		return this.read(in, Long.MAX_VALUE, charset);
	}

    public NullStringNode read(ExtendedRandomAccessFile in, long maxLength) throws IOException {
        return this.read(in, maxLength, charset);
    }

    public String value() {
        return value;
    }

	private final int TERMINATOR = 0;

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
		    if (b == -1 || b == TERMINATOR)
			    break;

			buf.addByte( b );
		    maxLength--;
	    }

	    value = new String(buf.toBytes() , charset);
	    end = start + buf.len() + 1;

        return this;
    }

    public String toString() {
        // Replace some characters to make them printable
        return StringUtils.abbreviate( StringEscapeUtils.escapeJava(value), MAX_DISPLAY_WIDTH );
    }
}
