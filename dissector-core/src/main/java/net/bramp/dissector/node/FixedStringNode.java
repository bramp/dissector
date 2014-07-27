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
public class FixedStringNode extends Node {

	final static int MAX_DISPLAY_WIDTH = 50;

    Charset charset = Charsets.UTF_8;
    String value;

    public FixedStringNode() {}

    public FixedStringNode read(ExtendedRandomAccessFile in, long length) throws IOException {
        return this.read(in, length, charset);
    }

    public String value() {
        return value;
    }

    /**
     * @param in
     * @param length in bytes
     * @param charset
     * @throws IOException
     */
    public FixedStringNode read(ExtendedRandomAccessFile in, long length, Charset charset) throws IOException {
        this.charset = Preconditions.checkNotNull(charset);
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
