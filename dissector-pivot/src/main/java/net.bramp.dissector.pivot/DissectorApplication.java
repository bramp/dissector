package net.bramp.dissector.pivot;

import net.bramp.hex.HexEditorWindow;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;

/**
 * @author bramp
 */
public class DissectorApplication implements Application {
    private HexEditorWindow window = null;

    @Override
    public void startup(Display display, Map<String, String> properties) throws Exception {
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        //window = (Window)bxmlSerializer.readObject(DissectorApplication.class, "application.xml");
        window = (HexEditorWindow) bxmlSerializer.readObject(getClass().getResource("hexeditor.xml"));

        window.open(display);
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
