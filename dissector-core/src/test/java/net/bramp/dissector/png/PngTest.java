package net.bramp.dissector.png;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

import static net.bramp.dissector.Helper.open;

/**
 * @author bramp
 */
public class PngTest {

    @Test
    public void testOne() throws IOException {
	    ExtendedRandomAccessFile in = open(getClass(), "z09n2c08.png");
        PngDissector dissector = new PngDissector().read(in);

        new NodePrinter().print(dissector);
    }

	@Test
	public void testAll() throws IOException {

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("net.bramp.dissector.png"))
				.setScanners(new ResourcesScanner())
		);

		for (String path : reflections.getResources(Pattern.compile(".*\\.png"))) {
			System.out.print(path);
			ExtendedRandomAccessFile in = open( path );
			new PngDissector().read(in); // Read but don't do anything with it
			System.out.println(" [OK]");
		}
	}
}
