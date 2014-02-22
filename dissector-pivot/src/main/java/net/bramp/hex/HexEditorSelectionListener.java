package net.bramp.hex;

import org.apache.pivot.wtk.TextArea;

/**
 * @author bramp
 */
public interface HexEditorSelectionListener {
    /**
     * Called when a text area's selection state has changed.
     *
     * @param editor
     * @param previousSelectionStart
     * @param previousSelectionLength
     */
    public void selectionChanged(HexEditor editor, long previousSelectionStart, long previousSelectionLength);
}
