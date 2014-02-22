package net.bramp.dissector.disk;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.junit.Test;

import java.io.IOException;

import static net.bramp.dissector.Helper.open;

/**
 * @author bramp
 */
public class MBRDissectorTest {

    @Test
    public void test() throws IOException {
        ExtendedRandomAccessFile in = open(getClass(), "sda");
        MBRDissector dissector = new MBRDissector().read(in);

        new NodePrinter().print(dissector);
    }
}
