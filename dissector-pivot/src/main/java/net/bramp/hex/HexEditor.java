package net.bramp.hex;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.sun.istack.internal.Nullable;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.*;

import java.io.IOException;
import java.io.RandomAccessFile;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * HexEditor component
 * Note: Right now I think this will crash with files > 2GB
 *
 * @author bramp
 */
public class HexEditor extends Component {

    /**
     * The file
     */
    RandomAccessFile file;

    /**
     * Length of the file
     */
    private long fileLength = 0;

    private long selectionStart = 0;
    private long selectionLength = 0;


    final HexEditorContentListenerList contentListeners = new HexEditorContentListenerList();
    final HexEditorSelectionListenerList selectionListeners = new HexEditorSelectionListenerList();

    private static class HexEditorContentListenerList extends WTKListenerList<HexEditorContentListener>
            implements HexEditorContentListener {

        @Override
        public void fileChanged(HexEditor editor) throws IOException {
            for (HexEditorContentListener listener : this) {
                listener.fileChanged(editor);
            }
        }
    }

    private static class HexEditorSelectionListenerList extends WTKListenerList<HexEditorSelectionListener>
            implements HexEditorSelectionListener {
        @Override
        public void selectionChanged(HexEditor editor, long previousSelectionStart,
                                     long previousSelectionLength) {
            for (HexEditorSelectionListener listener : this) {
                listener.selectionChanged(editor, previousSelectionStart,
                        previousSelectionLength);
            }
        }
    }

    public interface Skin {
        /**
         * Get the byte being displayed at the x/y coord
         * -1 if no byte is at that location
         * @param x x coord
         * @param y y xoord
         * @return
         */
        long getByteAt(int x, int y);
    }

    public HexEditor() {
        // HACK Remove when I know how
        Theme.getTheme().set(HexEditor.class, HexEditorSkin.class);

        installSkin(HexEditor.class);
    }

    @Override
    protected void setSkin(org.apache.pivot.wtk.Skin skin) {
        checkArgument(skin instanceof HexEditor.Skin, "Skin class must implement {}", HexEditor.Skin.class.getName());
        super.setSkin(skin);
    }

	public void setFile(RandomAccessFile file) throws IOException {
        this.file = file;
        this.fileLength = file.length();

        contentListeners.fileChanged(this);
    }

    public @Nullable LongSpan getSelection() {
        if (selectionLength == 0)
            return null;

        return new LongSpan(selectionStart, selectionStart + selectionLength - 1);
    }

    /**
     *
     * @param selectionStart  Byte offset into file
     * @param selectionLength in bytes
     */
    public void setSelection(long selectionStart, long selectionLength) {
        Preconditions.checkArgument(selectionLength >= 0, "selectionLength is negative.");

        if (selectionStart < 0 || selectionStart + selectionLength > fileLength) {
            throw new IndexOutOfBoundsException();
        }

        long previousSelectionStart  = this.selectionStart;
        long previousSelectionLength = this.selectionLength;

        if (previousSelectionStart != selectionStart || previousSelectionLength != selectionLength) {
            this.selectionStart = selectionStart;
            this.selectionLength = selectionLength;

            selectionListeners.selectionChanged(this, previousSelectionStart, previousSelectionLength);
        }
    }

    /*
    public void invalidate() {
        super.invalidate();

        try {
            fileLength = file == null ? 0 : file.length();
            lines = (int) (fileLength / this.bytesPerLine) + 1;

        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
    */

    public RandomAccessFile getFile() {
        return file;
    }

    public ListenerList<HexEditorContentListener> getContentListeners() {
        return contentListeners;
    }

    public ListenerList<HexEditorSelectionListener> getSelectionListeners() {
        return selectionListeners;
    }

    /*
    @Override
    public void load(Object context) {
        if (textKey != null
                && JSON.containsKey(context, textKey)
                && textBi   ndType != BindType.STORE) {
            Object value = JSON.get(context, textKey);

            if (textBindMapping == null) {
                value = (value == null) ? "" : value.toString();
            } else {
                value = textBindMapping.toString(value);
            }

            setText((String)value);
        }
    }

    @Override
    public void store(Object context) {
        if (textKey != null
                && textBindType != BindType.LOAD) {
            String text = getText();
            JSON.put(context, textKey, (textBindMapping == null) ?
                    text : textBindMapping.valueOf(text));
        }
    }
    */
}
