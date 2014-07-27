package net.bramp.dissector;

import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bramp
 */
public final class Helper {

    private Helper() {
        // Nothing
    }

    public static ExtendedRandomAccessFile open(Class clazz, String filename) throws IOException {
	    checkNotNull(clazz);
	    checkNotNull(filename);

        try {
	        URL url = clazz.getResource(filename);
	        if (url == null)
		        throw new IllegalArgumentException("Failed to open resource '" + filename + "'");

            return new ExtendedRandomAccessFile( new File( url.toURI() ), "r" );

        } catch (URISyntaxException e) {
            throw new IOException("Failed to open " + filename, e);
        }
    }

	public static ExtendedRandomAccessFile open(String filename) throws IOException {
		checkNotNull(filename);

		try {
			URI path = Helper.class.getClassLoader().getResource(filename).toURI();

			return new ExtendedRandomAccessFile(new File( path ), "r");

		} catch (URISyntaxException e) {
			throw new IOException("Failed to open " + filename, e);
		}
	}
}
