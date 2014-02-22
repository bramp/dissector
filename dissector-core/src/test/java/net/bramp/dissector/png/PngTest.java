package net.bramp.dissector.png;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.DataPositionInputStream;
import org.junit.Test;

import java.io.IOException;

/**
 * @author bramp
 */
public class PngTest {

    @Test
    public void test() throws IOException {
        DataPositionInputStream in = new DataPositionInputStream( getClass().getResourceAsStream("z09n2c08.png") );
        PngDissector dissector = new PngDissector().read(in);

       new NodePrinter().print(dissector);
    }
}
