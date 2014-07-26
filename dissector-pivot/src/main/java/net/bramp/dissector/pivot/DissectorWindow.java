package net.bramp.dissector.pivot;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import net.bramp.hex.HexEditor;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author bramp
 */
public class DissectorWindow extends Window implements Bindable {

	@BXML
	private SplitPane splitPane;

	@BXML
	private ScrollPane editorScrollPane;

    @BXML
    private HexEditor editor;

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {

	    // Ensure the Hex Editor pane has a min width
	    //int minWidth = editor.getMinimumWidth()
	    System.out.println(editor.getPreferredWidth() + "," + editorScrollPane.getPreferredWidth());
	    splitPane.getTopLeft().setMinimumWidth( editorScrollPane.getPreferredWidth() );

        String filename = "/home/bramp/personal/dissector/dissector-core/src/test/resources/net/bramp/dissector/png/z00n2c08.png";
        try {
            editor.setFile( new RandomAccessFile(filename, "r") );
            //editor.setSelection(5, 100); // For testing
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void load(Object context) {
        super.load(context);
    }
}
