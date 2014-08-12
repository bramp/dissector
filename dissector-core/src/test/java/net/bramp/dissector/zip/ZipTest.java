package net.bramp.dissector.zip;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.Dissector;
import net.bramp.dissector.png.PngDissector;
import net.bramp.dissector.torrent.TorrentDissector;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

import static net.bramp.dissector.Helper.open;

/**
 * @author bramp
 */
public class ZipTest {

	@Test
	public void testOne() throws IOException {
		ExtendedRandomAccessFile in = open(getClass(), "test.zip");
		ZipDissector dissector = new ZipDissector().read(in);

		new NodePrinter().print(dissector);
	}

	@Test
	public void testAll() throws IOException {

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("net.bramp.dissector.zip"))
				.setScanners(new ResourcesScanner())
		);

		for (String path : reflections.getResources(Pattern.compile(".*\\.zip"))) {
			System.out.print(path);
			ExtendedRandomAccessFile in = open( path );
			try {
				new ZipDissector().read(in); // Read but don't do anything with it
				System.out.println(" [OK]");
			} catch (Exception e) {
				System.out.println(" [BAD]");
			}

		}
	}

}
