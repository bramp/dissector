package net.bramp.dissector.png;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.junit.Test;

import java.io.IOException;

import static net.bramp.dissector.Helper.open;

/**
 * @author bramp
 */
public class PngTest {

    @Test
    public void test() throws IOException {
        ExtendedRandomAccessFile in = open(getClass(), "z09n2c08.png");
        PngDissector dissector = new PngDissector().read(in);

       new NodePrinter().print(dissector);
    }


}
