package net.bramp.dissector.iso;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.junit.Test;

import java.io.IOException;

/**
 * @author bramp
 */
public class IsoTest {

    @Test
    public void testOne() throws IOException {
	    ExtendedRandomAccessFile in = new ExtendedRandomAccessFile("/home/bramp/Downloads/6th_and_Lane_480_RK2 RF22 F24 A32 H240.mp4", "r");
	    IsoDissector dissector = new IsoDissector();

	    try {
		    dissector.read(in);
	    } catch (Exception e) {
		    e.printStackTrace();
	    }

        new NodePrinter().print(dissector);
    }

}
