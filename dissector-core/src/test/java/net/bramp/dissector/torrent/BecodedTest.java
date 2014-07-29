package net.bramp.dissector.torrent;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.Dissector;
import net.bramp.dissector.png.PngDissector;
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
public class BecodedTest {

    @Test
    public void testOne() throws IOException {
	    ExtendedRandomAccessFile in = open(getClass(), "KNOPPIX 7.2.0 DVD.torrent");
        Dissector dissector = new TorrentDissector().read(in);

        new NodePrinter().print(dissector);
    }

}
