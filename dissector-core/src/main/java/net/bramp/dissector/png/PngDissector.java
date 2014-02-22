package net.bramp.dissector.png;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.Dissector;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author bramp
 */
public class PngDissector extends Dissector {

    public PngDissector() {}

    public PngDissector read(ExtendedRandomAccessFile in) throws IOException {
        addChild( "Header", new PngHeaderNode().read(in) );

        try {

            while(true) {
                addChild( "Chunk", new PngChunkNode().read(in) );
            }

        } catch (EOFException eof) {
            // Ignore
            // TODO FIX
        }

        return this;
    }
}
