package net.bramp.dissector.node;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author bramp
 */
public class FixedStringNode extends Node<String> {

	final static int MAX_DISPLAY_WIDTH = 50;

    final Charset charset;
	final long length;

	String value;

	public FixedStringNode(long length) {
		this(length, Charsets.UTF_8);
	}

	public FixedStringNode(Node<? extends Number> lengthNode) {
		this(lengthNode, Charsets.UTF_8);
	}

	/**
	 * @param length in bytes
	 * @param charset
	 * @throws IOException
	 */
	public FixedStringNode(long length, Charset charset) {
		this.length = length;
		this.charset = Preconditions.checkNotNull(charset);
	}

	public FixedStringNode(Node<? extends Number> lengthNode, Charset charset) {
		this.length = lengthNode.value().longValue();
		this.charset = Preconditions.checkNotNull(charset);
	}

	public String value() {
		return value;
	}

	public FixedStringNode read(ExtendedRandomAccessFile in) throws IOException {
		setPos(in, length);

		byte[] bytes = new byte[(int)length];
		in.readFully(bytes);

		value = new String(bytes, charset);
		return this;
    }

    public String toString() {
        // Replace some characters to make them printable
        return StringUtils.abbreviate( StringEscapeUtils.escapeJava(value), MAX_DISPLAY_WIDTH );
    }
}
