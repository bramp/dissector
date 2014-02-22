package net.bramp.dissector.png;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.node.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class PngChunkNode extends TreeNode {

    static final Map<Integer, String> colourTypes = ImmutableMap.<Integer, String>builder()
            .put(0, "Greyscale")
            .put(2, "Truecolour")
            .put(3, "Indexed-colour")
            .put(4, "Greyscale with alpha")
            .put(6, "Truecolour with alpha")
            .build();

    static final Map<Integer, String> compressionTypes = ImmutableMap.<Integer, String>builder()
            .put(0, "deflate/inflate") // compression with a sliding window of at most 32768 bytes)
            .build();

    static final Map<Integer, String> filterTypes = ImmutableMap.<Integer, String>builder()
            .put(0, "adaptive filtering") // with five basic filter types
            .build();

    static final Map<Integer, String> interlaceTypes = ImmutableMap.<Integer, String>builder()
            .put(0, "none")
            .put(1, "Adam7")
            .build();


    public PngChunkNode() {}

    public PngChunkNode read(DataPositionInputStream in) throws IOException {
        IntNode length       = new IntNode().read(in, false);
        FixedStringNode type = new FixedStringNode().read(in, 4, Charsets.US_ASCII);

        addChild("Length", length);
        addChild("Type",   type);
        //setTitle(type + " Chunk");

        PngChunkNode node;
        switch (type.value()) {
            case "IHDR" : readIHDR(in); break;
            default     : readUnknown(in, length.value());
        }

        addChild("CRC", new IntNode().read(in, false));

        return this;
    }

    protected void readUnknown(DataPositionInputStream in, long length) throws IOException {
        addChild("Data", new SkipNode().read(in, length) );
    }

    protected void readIHDR(DataPositionInputStream in) throws IOException {
        addChild("Width", new IntNode().read(in, false) );
        addChild("Height", new IntNode().read(in, false) );
        addChild("Bit depth", new ByteNode().read(in, false) );
        addChild("Colour type", new EnumNode(colourTypes).read(in, 1) );
        addChild("Compression", new EnumNode(compressionTypes).read(in, 1) );
        addChild("Filter method", new EnumNode(filterTypes).read(in, 1) );
        addChild("Interlace method", new EnumNode(interlaceTypes).read(in, 1) );
    }
}
