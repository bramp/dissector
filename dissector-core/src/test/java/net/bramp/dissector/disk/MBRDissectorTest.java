package net.bramp.dissector.disk;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.java.JavaClassDissector;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author bramp
 */
public class MBRDissectorTest {

    @Test
    public void test() throws IOException {
        DataPositionInputStream in = new DataPositionInputStream( getClass().getResourceAsStream("sda") );
        MBRDissector dissector = new MBRDissector().read(in);

        new NodePrinter().print(dissector);
    }
}
