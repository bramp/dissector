package net.bramp.dissector.java;

import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.junit.Test;

import java.io.IOException;

import static net.bramp.dissector.Helper.open;

/**
 * @author bramp
 */
public class JavaClassTest {

    @Test
    public void test() throws IOException {
        ExtendedRandomAccessFile in = open(getClass(), "JavaClassDissector.class" );
        JavaClassDissector dissector = new JavaClassDissector().read(in);

        new NodePrinter().print(dissector);
    }
}
