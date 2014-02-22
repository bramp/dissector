package net.bramp.dissector.java;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.java.JavaClassDissector;
import net.bramp.dissector.png.PngDissector;
import org.junit.Test;

import java.io.IOException;

/**
 * @author bramp
 */
public class JavaClassTest {

    @Test
    public void test() throws IOException {
        DataPositionInputStream in = new DataPositionInputStream( getClass().getResourceAsStream("JavaClassDissector.class") );
        JavaClassDissector dissector = new JavaClassDissector().read(in);

        new NodePrinter().print(dissector);
    }
}
