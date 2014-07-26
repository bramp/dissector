package net.bramp.hex;

import java.io.IOException;

/**
* @author bramp
*/
public interface HexEditorContentListener {
    public void fileChanged(HexEditor editor) throws IOException;
}
