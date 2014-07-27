package net.bramp.dissector.pivot;

import com.google.common.base.Throwables;
import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.java.JavaClassDissector;
import net.bramp.dissector.png.PngDissector;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;

import java.io.IOException;

/**
 * @author bramp
 */
public class DissectorApplication implements Application {
    private DissectorWindow window = null;

    @Override
    public void startup(Display display, Map<String, String> properties) throws Exception {
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
	    window = (DissectorWindow) bxmlSerializer.readObject(getClass().getResource("application.xml"));
	    window.open(display);

	    //ExtendedRandomAccessFile in = open(getClass(), "JavaClassDissector.class" );
	    //JavaClassDissector dissector = new JavaClassDissector().read(in);

	    //String filename = "/home/bramp/personal/dissector/dissector-core/src/test/resources/net/bramp/dissector/png/z00n2c08.png";
	    //String filename = "/home/bramp/personal/dissector/dissector-core/target/classes/net/bramp/dissector/java/JavaClassDissector.class";
	    String filename = "/home/bramp/personal/dissector/dissector-core/target/classes/net/bramp/dissector/node/Node.class";
	    ExtendedRandomAccessFile file = new ExtendedRandomAccessFile(filename, "r");

	    //window.loadDissector(file, new PngDissector());
	    window.loadDissector(file, new JavaClassDissector());

    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {}

    @Override
    public void resume() {}

    public static void main(String[] args) {
        DesktopApplicationContext.main(DissectorApplication.class, args);
    }
}
