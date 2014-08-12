package net.bramp.dissector.png;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class PngChunkNode extends TreeNode {

	static final Map<String, String> chunkTypes = ImmutableMap.<String, String>builder()
			.put("IHDR", "Image header")
			.put("IDAT", "Image data")
			.put("PLTE", "Palette")
			.put("IEND", "Image trailer")
			.put("bKGD", "Background color")
			.put("cHRM", "Primary chromaticities and white point")
			.put("gAMA", "Image gamma")
			.put("hIST", "Image histogram")
			.put("pHYs", "Physical pixel dimensions")
			.put("sBIT", "Significant bits")
			.put("tEXt", "Textual data")
			.put("tIME", "Image last-modification time")
			.put("tRNS", "Transparency")
			.put("zTXt", "Compressed textual data Transparency")

			.build();

    static final Map<Short, String> colourTypes = ImmutableMap.<Short, String>builder()
            .put((short) 0, "Greyscale")
            .put((short) 2, "Truecolour")
            .put((short) 3, "Indexed-colour")
            .put((short) 4, "Greyscale with alpha")
            .put((short) 6, "Truecolour with alpha")
            .build();

    static final Map<Short, String> compressionTypes = ImmutableMap.<Short, String>builder()
            .put((short) 0, "deflate/inflate") // compression with a sliding window of at most 32768 bytes)
            .build();

    static final Map<Short, String> filterTypes = ImmutableMap.<Short, String>builder()
            .put((short) 0, "adaptive filtering") // with five basic filter types
            .build();

    static final Map<Short, String> interlaceTypes = ImmutableMap.<Short, String>builder()
            .put((short) 0, "none")
            .put((short) 1, "Adam7")
            .build();

    public PngChunkNode() {}

    public PngChunkNode read(ExtendedRandomAccessFile in) throws IOException {
        IntNode length = new IntNode().read(in);
        EnumNode<String> type  = new EnumNode<String>(chunkTypes, new FixedStringNode(4, Charsets.US_ASCII).read(in));

        addChild("Length", length);
        addChild("Type",   type);

        PngChunkNode node;
        switch (type.value()) {
            case "IHDR" : readIHDR(in); break;
            default     : readUnknown(in, length.value());
        }

        addChild("CRC", new IntNode().base(16).read(in)); // TODO Validate

	    setTitle( type.name() + " (" + length.value() + " bytes)" );

        return this;
    }

    protected void readUnknown(ExtendedRandomAccessFile in, long length) throws IOException {
        addChild("Data", new SkipNode().read(in, length) );
    }

    protected void readIHDR(ExtendedRandomAccessFile in) throws IOException {
        addChild("Width", new IntNode().read(in) );
        addChild("Height", new IntNode().read(in) );
        addChild("Bit depth", new ByteNode().read(in) );
        addChild("Colour type", new EnumNode(colourTypes, new ByteNode().read(in, false) ));
        addChild("Compression", new EnumNode(compressionTypes, new ByteNode().read(in, false) ));
        addChild("Filter method", new EnumNode(filterTypes, new ByteNode().read(in, false) ));
        addChild("Interlace method", new EnumNode(interlaceTypes, new ByteNode().read(in, false) ));
    }

}
