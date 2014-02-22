package net.bramp.hex;

import com.google.common.base.Throwables;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Window;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * @author bramp
 */
public class HexEditorWindow extends Window implements Bindable {
    @BXML
    private HexEditor editor;

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        String filename = "/home/bramp/personal/dissector/dissector-core/src/test/resources/net/bramp/dissector/png/z00n2c08.png";
        try {
            editor.setFile( new RandomAccessFile(filename, "r") );
        } catch (FileNotFoundException e) {
            Throwables.propagate(e);
        }
    }

    @Override
    public void load(Object context) {
        super.load(context);
    }
}
