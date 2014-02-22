package net.bramp.dissector;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author bramp
 */
public final class Helper {

    private Helper() {
        // Nothing
    }

    public static ExtendedRandomAccessFile open(Class clazz, String filename) throws IOException {
        try {
            URI path = clazz.getResource(filename).toURI();
            return new ExtendedRandomAccessFile( new File( path ), "r" );

        } catch (URISyntaxException e) {
            throw new IOException("Failed to open " + filename, e);
        }
    }
}
