package net.bramp.hex;

import com.google.common.base.Throwables;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Insets;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.skin.ComponentSkin;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author bramp
 */
public class HexEditorSkin extends ComponentSkin implements HexEditor.Skin, HexEditorContentListener, HexEditorSelectionListener {

    /*
    private class BlinkCaretCallback implements Runnable {
        @Override
        public void run() {
            caretOn = !caretOn;

            if (selection == null) {
                TextArea textArea = (TextArea) getComponent();
                textArea.repaint(caret.x, caret.y, caret.width, caret.height);
            }
        }
    }

    private class ScrollSelectionCallback implements Runnable {
        @Override
        public void run() {
            TextArea textArea = (TextArea)getComponent();
            int selectionStart = textArea.getSelectionStart();
            int selectionLength = textArea.getSelectionLength();
            int selectionEnd = selectionStart + selectionLength - 1;

            switch (scrollDirection) {
                case UP: {
                    // Get previous offset
                    int index = getNextInsertionPoint(mouseX, selectionStart, scrollDirection);

                    if (index != -1) {
                        textArea.setSelection(index, selectionEnd - index + 1);
                        scrollCharacterToVisible(index + 1);
                    }

                    break;
                }

                case DOWN: {
                    // Get next offset
                    int index = getNextInsertionPoint(mouseX, selectionEnd, scrollDirection);

                    if (index != -1) {
                        // If the next character is a paragraph terminator, increment
                        // the selection
                        if (index < textArea.getCharacterCount()
                                && textArea.getCharacterAt(index) == '\n') {
                            index++;
                        }

                        textArea.setSelection(selectionStart, index - selectionStart);
                        scrollCharacterToVisible(index - 1);
                    }

                    break;
                }

                default: {
                    break;
                }
            }
        }
    }
    */

    private int caretX = 0;
    private Rectangle caret = new Rectangle();
    private Area selection = null;

    private boolean caretOn = false;

    private int anchor = -1;
    private TextArea.ScrollDirection scrollDirection = null;
    private int mouseX = -1;

    //private BlinkCaretCallback blinkCaretCallback = new BlinkCaretCallback();
    //private ApplicationContext.ScheduledCallback scheduledBlinkCaretCallback = null;

    //private ScrollSelectionCallback scrollSelectionCallback = new ScrollSelectionCallback();
    //private ApplicationContext.ScheduledCallback scheduledScrollSelectionCallback = null;

    private Font font;
    private Color color = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private Color inactiveColor = Color.GRAY;
    private Color selectionColor = Color.LIGHT_GRAY;
    private Color selectionBackgroundColor = Color.BLUE;
    private Color inactiveSelectionColor = Color.LIGHT_GRAY;
    private Color inactiveSelectionBackgroundColor = Color.BLACK;
    private Insets margin = new Insets(4);

    private Dimensions averageCharacterSize;
    private int lineHeight;
    private int marginTop;
    private int fontAscent;


    /** TODO Create show booleans
    private boolean showAddress = true;
    private boolean showHex = true;
    private boolean showRaw = true;
    */

    private static final int addressWidth = 9; // In characters 8 + 1 space
    private static final int SCROLL_RATE = 30;

    private ExtendedRandomAccessFile file;

    /**
     * Total number of lines
     */
    private int lines = 1;

    /**
     * How many bytes to display per line
     */
    private final int bytesPerLine = 16;

    private long selectionStart  = 0;
    private long selectionLength = 0;


    public HexEditorSkin() {

        TableView t;

        Theme theme = Theme.getTheme();
        font = theme.getFont(); // TODO default to monospace font
	    calculateFontBounds();
    }

    @Override
    public void install(org.apache.pivot.wtk.Component component) {
        super.install(component);

        HexEditor editor = (HexEditor)component;
        editor.getContentListeners().add(this);
        editor.getSelectionListeners().add(this);

        //textArea.setCursor(org.apache.pivot.wtk.Cursor.TEXT);
    }

    protected HexEditor getHexEditor() {
        return (HexEditor)getComponent();
    }

    @Override
    public int getPreferredWidth(int height) {
        int charsPerLine = bytesPerLine * 4 + addressWidth;
        int preferredWidth = averageCharacterSize.width * charsPerLine;
        preferredWidth += margin.left + margin.right;
        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        int preferredHeight = (int) (averageCharacterSize.height * numberOfLines);
        preferredHeight += margin.top + margin.bottom;
        return preferredHeight;
    }

    @Override
    public Dimensions getPreferredSize() {
        int preferredWidth  = getPreferredWidth(-1);
        int preferredHeight = getPreferredHeight(-1);

        return new Dimensions(preferredWidth, preferredHeight);
    }

    private long calcFileLength() {
        try {
            return file != null ? file.length() : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void layout() {
        HexEditor editor = getHexEditor();

        this.fileLength = calcFileLength();
        this.numberOfLines = (int) ((fileLength / this.bytesPerLine) + 1); // TODO 2GB

        FontRenderContext fontRenderContext = Platform.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("", fontRenderContext);

        fontAscent = (int) lm.getAscent();
        marginTop  = (int) (margin.top + lm.getAscent());

        /*
        int width = getWidth();
        int breakWidth = Integer.MAX_VALUE;

        int y = margin.top;
        int lastY = 0;
        int lastHeight = 0;

        int rowOffset = 0;
        int index = 0;
        for (TextAreaSkinParagraphView paragraphView : paragraphViews) {
            paragraphView.setBreakWidth(breakWidth);
            paragraphView.setX(margin.left);
            paragraphView.setY(y);
            lastY = y;
            y += paragraphView.getHeight();
            lastHeight = paragraphView.getHeight();

            paragraphView.setRowOffset(rowOffset);
            rowOffset += paragraphView.getRowCount();
            index++;
        }

        updateSelection();
        caretX = caret.x;

        if (textArea.isFocused()) {
            scrollCharacterToVisible(textArea.getSelectionStart());
            showCaret(textArea.getSelectionLength() == 0);
        } else {
            showCaret(false);
        }
        */
    }

    @Override
    public int getBaseline(int width, int height) {
        FontRenderContext fontRenderContext = Platform.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("", fontRenderContext);

        return Math.round(margin.top + lm.getAscent());
    }


    private StringBuilder expand(StringBuilder sb, int newLength, char c) {
        int length = sb.length();
        for (; length < newLength; length++)
            sb.append( c );
        return sb;
    }

    private static char printable(byte b) {
        if (b >= 32)
            return (char)(b & 0xff);
        return '.';
    }

    /* Some temp objects to aid in face rendering */
    final StringBuilder hexSB = new StringBuilder();
    final StringBuilder rawSB = new StringBuilder();
    byte[] lineBuffer = new byte[bytesPerLine];

    /**
     * Builds a single line of text
     * @return
     * @throws IOException
     */
    private String buildLine(long offset) throws IOException {
        if (file == null)
            return "00000000: ";

        hexSB.setLength(0);
        rawSB.setLength(0);
        file.seek(offset);

        int len = file.read(lineBuffer);
        for (int i = 0; i < len; i++) {
            byte b = lineBuffer[i];

            hexSB.append( String.format("%02X ", b) );
            rawSB.append( printable(b) );
        }

        expand(hexSB, bytesPerLine * 3, ' ');
        expand(rawSB, bytesPerLine, ' ');

        StringBuilder totalSB = new StringBuilder(bytesPerLine * 4 + addressWidth);
        totalSB
            .append(String.format("%08X ", offset))
            .append(hexSB)
            .append(rawSB).append('\n');

        return totalSB.toString();
    }

    private boolean inSelectedRange(long offset, int length) {
        long offsetEnd = offset + length;
        long selectionEnd = selectionStart + selectionLength;

        return selectionLength > 0 &&
                (selectionStart < offset + length && selectionEnd >= offset);
    }

    /**
     * Returns byte offset
     * @param x x coord
     * @param y y xoord
     * @return
     */
    @Override
    public long getByteAt(int x, int y) {
        int row = getRowAt(y);
        if (row == -1)
            return -1;

        int col = getColAt(x);
        if (col == -1)
            return -1;

        return row * bytesPerLine + col;
    }

    /**
     *
     * @param y screen coord
     * @return
     */
    public int getRowAt(int y) {
        y -= marginTop;
        if (y < 0)
            return -1;

        int row = y / lineHeight;
        if (row >= this.numberOfLines)
            return -1;

        return row;
    }

    /**
     *
     * @param x screen coord
     * @return The column this byte is in, OR, -1 if outside range
     */
    public int getColAt(int x) {
        int charWidth  = averageCharacterSize.width;
        int marginLeft = margin.left + charWidth * addressWidth;
        x -= marginLeft;

        // Are we in the hex or raw?
        if (x < 0) {
            return -1;

        } else if (x > charWidth * bytesPerLine * 3) {
            // Raw
            x -= charWidth * bytesPerLine * 3;
        } else {
            // Hex (each byte is 3 chars wide)
            charWidth *= 3;
        }

        int col = x / charWidth;
        if (col >= bytesPerLine)
            return -1;
        return col;
    }

    @Override
    public void paint(Graphics2D graphics) {

        HexEditor editor = getHexEditor();
        int width  = getWidth();
        int height = getHeight();

        hexSB.ensureCapacity(bytesPerLine * 3);
        rawSB.ensureCapacity(bytesPerLine);

        // Draw the background
        if (backgroundColor != null) {
            graphics.setPaint(backgroundColor);
            graphics.fillRect(0, 0, width, height);
        }

        // Ensure that we only paint items that are visible
        int rowStart = 0;
        int rowEnd   = numberOfLines - 1;

        Rectangle clipBounds = graphics.getClipBounds();
        if (clipBounds != null) {
	        // TODO Do we need to translate the clipBound to our Component? if so the -1 check below wouldn't be needed
            rowStart = Math.max(rowStart, getRowAt(clipBounds.y));

	        int row = getRowAt(clipBounds.y + clipBounds.height);
	        if (row != -1)
                rowEnd   = Math.min(rowEnd, row + 1);
        }

        checkState(rowStart >= 0);
        checkState(rowStart <= rowEnd);
        checkState(rowEnd >= 0);
        checkState(rowEnd < numberOfLines);


        /*
        // Draw the caret/selection
        if (selection == null) {
            if (caretOn
                    && editor.isFocused()) {
                graphics.setColor(editor.isEditable() ? color : inactiveColor);
                graphics.fill(caret);
            }
        } else {
            graphics.setColor(editor.isFocused()
                    && editor.isEditable() ? selectionBackgroundColor : inactiveSelectionBackgroundColor);
            graphics.fill(selection);
        }
        */

        graphics.setFont(font);
        graphics.translate(margin.left, marginTop + rowStart * lineHeight);

        int charWidth  = averageCharacterSize.width;

        try {
            long offset = rowStart * bytesPerLine;
            for (;rowStart <= rowEnd; rowStart++) {

                String line = buildLine(offset);

                paintLine(graphics, line, false);


                // Selection is on this line
                if (inSelectedRange(offset, bytesPerLine)) {

                    paintLine(graphics, line, false);

                    int startCol = 0, endCol = 16;
                    long selectionEnd = selectionStart + selectionLength;
                    if (selectionStart <= offset && selectionEnd >= offset + bytesPerLine) {
                        // Full
                        startCol = 0;
                        endCol   = 16;
                    } else {
                        if (selectionStart >= offset) {
                            // Starts on this line
                            startCol = (int) (selectionStart % bytesPerLine);
                        }
                        if (selectionEnd < offset + bytesPerLine) {
                            // Ends on this line
                            endCol = (int) (selectionEnd % bytesPerLine);
                        }
                    }

                    Shape oldClip = graphics.getClip();
                    int clipX = addressWidth * averageCharacterSize.width +
                            startCol * 3 * averageCharacterSize.width;

                    int clipWidth = (endCol - startCol) * 3 * averageCharacterSize.width;

                    int pad = 3;
                    graphics.clipRect(clipX - pad,
                            -fontAscent - pad,
                            clipWidth - averageCharacterSize.width + 2 * pad,
                            lineHeight + 2 * pad);

                    paintLine(graphics, line, true);
                    graphics.setClip(oldClip);
                }

                graphics.translate(0, lineHeight);
                offset += bytesPerLine;
            }

        } catch (Throwable t) {
            Throwables.propagate(t);
        }
    }

    long fileLength = 0;
    int numberOfLines = 0;

    private void paintLine(Graphics2D graphics, String line, boolean selected) {

        /*
        Font font = textAreaSkin.getFont();
        FontRenderContext fontRenderContext = Platform.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("", fontRenderContext);
        float ascent = lm.getAscent();
        float rowHeight = ascent + lm.getDescent();
        */

        //Rectangle clipBounds = graphics.getClipBounds();
        //Rectangle2D textBounds = row.glyphVector.getLogicalBounds();

        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(line, graphics);

        // TODO set clip here

        graphics.setColor(selected ? getSelectionBackgroundColor() : getBackgroundColor());
        //graphics.fillRect(0, 0 - fm.getAscent(), (int) rect.getWidth(), (int) rect.getHeight());
        graphics.fillRect(0, -fontAscent, (int) rect.getWidth(), (int) rect.getHeight());

        graphics.setPaint(selected ? getSelectionColor() : getColor());
        graphics.drawString(line, 0, 0);

    }


    @Override
    public boolean isOpaque() {
        return (backgroundColor != null && backgroundColor.getTransparency() == Transparency.OPAQUE);
    }


    /*
    public Area getSelection() {
        return selection;
    }

    private void scrollCharacterToVisible(int index) {
        Bounds characterBounds = getCharacterBounds(index);

        if (characterBounds != null) {
            TextArea textArea = (TextArea)getComponent();
            textArea.scrollAreaToVisible(characterBounds.x, characterBounds.y,
                    characterBounds.width, characterBounds.height);
        }
    }
    */

    /**
     * Returns the font of the text
     */
    public Font getFont() {
        return font;
    }

	protected void calculateFontBounds() {
		int missingGlyphCode = font.getMissingGlyphCode();
		FontRenderContext fontRenderContext = Platform.getFontRenderContext();

		GlyphVector missingGlyphVector = font.createGlyphVector(fontRenderContext,
				new int[] {missingGlyphCode});
		Rectangle2D textBounds = missingGlyphVector.getLogicalBounds();

		Rectangle2D maxCharBounds = font.getMaxCharBounds(fontRenderContext);
		averageCharacterSize = new Dimensions(
				(int)Math.ceil(textBounds.getWidth()),
				(int)Math.ceil(maxCharBounds.getHeight())
		);

		lineHeight = averageCharacterSize.height;
	}

    /**
     * Sets the font of the text
     */
    public void setFont(Font font) {
        this.font = checkNotNull(font);
	    calculateFontBounds();

        invalidateComponent();
    }

    /**
     * Sets the font of the text
     * @param font A {@link ComponentSkin#decodeFont(String) font specification}
     */
    public final void setFont(String font) {
        checkNotNull(font);
        setFont(decodeFont(font));
    }

    /**
     * Sets the font of the text
     * @param font A dictionary {@link Theme#deriveFont describing a font}
     */
    public final void setFont(Dictionary<String, ?> font) {
        checkNotNull(font);
        setFont(Theme.deriveFont(font));
    }

    /**
     * Returns the foreground color of the text
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the foreground color of the text
     */
    public void setColor(Color color) {
        this.color = checkNotNull(color);
        repaintComponent();
    }

    /**
     * Sets the foreground color of the text
     * @param color Any of the {@linkplain GraphicsUtilities#decodeColor color values recognized by Pivot}.
     */
    public final void setColor(String color) {
        checkNotNull(color);
        setColor(GraphicsUtilities.decodeColor(color));
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = checkNotNull(backgroundColor);
        repaintComponent();
    }

    public final void setBackgroundColor(String backgroundColor) {
        checkNotNull(backgroundColor);
        setBackgroundColor(GraphicsUtilities.decodeColor(backgroundColor));
    }

    public Color getInactiveColor() {
        return inactiveColor;
    }

    public void setInactiveColor(Color inactiveColor) {
        this.inactiveColor = checkNotNull(inactiveColor);
        repaintComponent();
    }

    public final void setInactiveColor(String inactiveColor) {
        checkNotNull(inactiveColor);
        setColor(GraphicsUtilities.decodeColor(inactiveColor));
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = checkNotNull(selectionColor);
        repaintComponent();
    }

    public final void setSelectionColor(String selectionColor) {
        checkNotNull(selectionColor);
        setSelectionColor(GraphicsUtilities.decodeColor(selectionColor));
    }

    public Color getSelectionBackgroundColor() {
        return selectionBackgroundColor;
    }

    public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
        this.selectionBackgroundColor = checkNotNull(selectionBackgroundColor);
        repaintComponent();
    }

    public final void setSelectionBackgroundColor(String selectionBackgroundColor) {
        checkNotNull(selectionBackgroundColor);
        setSelectionBackgroundColor(GraphicsUtilities.decodeColor(selectionBackgroundColor));
    }

    public Color getInactiveSelectionColor() {
        return inactiveSelectionColor;
    }

    public void setInactiveSelectionColor(Color inactiveSelectionColor) {
        this.inactiveSelectionColor = checkNotNull(inactiveSelectionColor);;
        repaintComponent();
    }

    public final void setInactiveSelectionColor(String inactiveSelectionColor) {
        checkNotNull(inactiveSelectionColor);
        setInactiveSelectionColor(GraphicsUtilities.decodeColor(inactiveSelectionColor));
    }

    public Color getInactiveSelectionBackgroundColor() {
        return inactiveSelectionBackgroundColor;
    }

    public void setInactiveSelectionBackgroundColor(Color inactiveSelectionBackgroundColor) {
        this.inactiveSelectionBackgroundColor = checkNotNull(inactiveSelectionBackgroundColor);
        repaintComponent();
    }

    public final void setInactiveSelectionBackgroundColor(String inactiveSelectionBackgroundColor) {
        checkNotNull(inactiveSelectionBackgroundColor);
        setInactiveSelectionBackgroundColor(GraphicsUtilities.decodeColor(inactiveSelectionBackgroundColor));
    }

    /**
     * Returns the amount of space between the edge of the TextArea and its text
     */
    public org.apache.pivot.wtk.Insets getMargin() {
        return margin;
    }

    /**
     * Sets the amount of space between the edge of the TextArea and its text
     */
    public void setMargin(org.apache.pivot.wtk.Insets margin) {
        this.margin = checkNotNull(margin);;
        invalidateComponent();
    }

    /**
     * Sets the amount of space between the edge of the TextArea and its text
     * @param margin A dictionary with keys in the set {left, top, bottom, right}.
     */
    public final void setMargin(Dictionary<String, ?> margin) {
        checkNotNull(margin);
        setMargin(new org.apache.pivot.wtk.Insets(margin));
    }

    /**
     * Sets the amount of space between the edge of the TextArea and its text
     */
    public final void setMargin(int margin) {
        setMargin(new org.apache.pivot.wtk.Insets(margin));
    }

    /**
     * Sets the amount of space between the edge of the TextArea and its text
     */
    public final void setMargin(Number margin) {
        checkNotNull(margin);
        setMargin(margin.intValue());
    }

    /**
     * Sets the amount of space between the edge of the TextArea and its text
     * @param margin A string containing an integer or a JSON dictionary with keys
     * left, top, bottom, and/or right.
     */
    public final void setMargin(String margin) {
        checkNotNull(margin);
        setMargin(org.apache.pivot.wtk.Insets.decode(margin));
    }

    @Override
    public void fileChanged(HexEditor editor) {
        checkState(editor == getComponent());

        file = editor.getFile();
        //offset = 0;

        invalidateComponent();
    }

    public void invalidateComponent() {
	    super.invalidateComponent();

	    HexEditor editor = getHexEditor();
	    editor.setMinimumWidth( getPreferredWidth(-1) );
    }

    @Override
    public void selectionChanged(HexEditor editor, long previousSelectionStart, long previousSelectionLength) {
        checkState(editor == getComponent());

        LongSpan span = editor.getSelection();
        if (span == null) {
            selectionLength = 0;
        } else {
            selectionStart  = span.start;
            selectionLength = span.getLength();
        }

        // TODO repaint the effected lines only
        repaintComponent();
    }


/*
    @Override
    public boolean mouseMove(org.apache.pivot.wtk.Component component, int x, int y) {
        boolean consumed = super.mouseMove(component, x, y);

        if (Mouse.getCapturer() == component) {
            TextArea textArea = (TextArea)getComponent();

            Bounds visibleArea = textArea.getVisibleArea();
            visibleArea = new Bounds(visibleArea.x, visibleArea.y, visibleArea.width,
                    visibleArea.height);

            // if it's inside the visible area, stop the scroll timer
            if (y >= visibleArea.y
                    && y < visibleArea.y + visibleArea.height) {
                // Stop the scroll selection timer
                if (scheduledScrollSelectionCallback != null) {
                    scheduledScrollSelectionCallback.cancel();
                    scheduledScrollSelectionCallback = null;
                }

                scrollDirection = null;
            } else {
                // if it's outside the visible area, start the scroll timer
                if (scheduledScrollSelectionCallback == null) {
                    scrollDirection = (y < visibleArea.y) ? TextArea.ScrollDirection.UP
                            : TextArea.ScrollDirection.DOWN;

                    scheduledScrollSelectionCallback = ApplicationContext.scheduleRecurringCallback(
                            scrollSelectionCallback, SCROLL_RATE);

                    // Run the callback once now to scroll the selection immediately
                    scrollSelectionCallback.run();
                }
            }

            int index = getInsertionPoint(x, y);

            if (index != -1) {
                // Select the range
                if (index > anchor) {
                    textArea.setSelection(anchor, index - anchor);
                } else {
                    textArea.setSelection(index, anchor - index);
                }
            }

            mouseX = x;
        } else {
            if (Mouse.isPressed(Mouse.Button.LEFT)
                    && Mouse.getCapturer() == null
                    && anchor != -1) {
                // Capture the mouse so we can select text
                Mouse.capture(component);
            }
        }

        return consumed;
    }

    @Override
    public boolean mouseDown(org.apache.pivot.wtk.Component component, Mouse.Button button, int x, int y) {
        boolean consumed = super.mouseDown(component, button, x, y);

        TextArea textArea = (TextArea)component;

        if (button == Mouse.Button.LEFT) {
            anchor = getInsertionPoint(x, y);

            if (anchor != -1) {
                if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)) {
                    // Select the range
                    int selectionStart = textArea.getSelectionStart();

                    if (anchor > selectionStart) {
                        textArea.setSelection(selectionStart, anchor - selectionStart);
                    } else {
                        textArea.setSelection(anchor, selectionStart - anchor);
                    }
                } else {
                    // Move the caret to the insertion point
                    textArea.setSelection(anchor, 0);
                    consumed = true;
                }
            }

            caretX = caret.x;

            // Set focus to the text input
            textArea.requestFocus();
        }

        return consumed;
    }

    @Override
    public boolean mouseUp(org.apache.pivot.wtk.Component component, Mouse.Button button, int x, int y) {
        boolean consumed = super.mouseUp(component, button, x, y);

        if (Mouse.getCapturer() == component) {
            // Stop the scroll selection timer
            if (scheduledScrollSelectionCallback != null) {
                scheduledScrollSelectionCallback.cancel();
                scheduledScrollSelectionCallback = null;
            }

            Mouse.release();
        }

        scrollDirection = null;
        mouseX = -1;

        return consumed;
    }

    @Override
    public boolean mouseClick(org.apache.pivot.wtk.Component component, Mouse.Button button,
                              int x, int y, int count) {
        boolean consumed = super.mouseClick(component, button, x, y, count);

        TextArea textArea = (TextArea)component;

        if (button == Mouse.Button.LEFT) {
            int index = getInsertionPoint(x, y);
            if (index != -1) {
                if (count == 2) {
                    selectSpan(textArea, index);
                } else if (count == 3) {
                    textArea.setSelection(textArea.getRowOffset(index), textArea.getRowLength(index));
                }
            }
        }
        return consumed;
    }

    private void selectSpan(TextArea textArea, int start) {
        int rowStart = textArea.getRowOffset(start);
        int rowLength = textArea.getRowLength(start);
        if (start - rowStart >= rowLength) {
            start = rowStart + rowLength - 1;
            char ch = textArea.getCharacterAt(start);
            if (ch == '\r' || ch == '\n') {
                start--;
            }
        }
        char ch = textArea.getCharacterAt(start);
        int selectionStart = start;
        int selectionLength = 1;
        if (Character.isWhitespace(ch)) {
            // Move backward to beginning of whitespace block
            // but not before the beginning of the line.
            do {
                selectionStart--;
            } while (selectionStart >= rowStart &&
                    Character.isWhitespace(textArea.getCharacterAt(selectionStart)));
            selectionStart++;
            selectionLength = start - selectionStart;
            // Move forward to end of whitespace block
            // but not past the end of the text or the end of line
            do {
                selectionLength++;
            } while (selectionStart + selectionLength - rowStart < rowLength &&
                    Character.isWhitespace(textArea.getCharacterAt(selectionStart + selectionLength)));
        } else if (Character.isJavaIdentifierPart(ch)) {
            // Move backward to beginning of identifier block
            do {
                selectionStart--;
            } while (selectionStart >= rowStart &&
                    Character.isJavaIdentifierPart(textArea.getCharacterAt(selectionStart)));
            selectionStart++;
            selectionLength = start - selectionStart;
            // Move forward to end of identifier block
            // but not past end of text
            do {
                selectionLength++;
            } while (selectionStart + selectionLength - rowStart < rowLength &&
                    Character.isJavaIdentifierPart(textArea.getCharacterAt(selectionStart + selectionLength)));
        } else {
            return;
        }
        textArea.setSelection(selectionStart, selectionLength);
    }

    @Override
    public boolean keyTyped(org.apache.pivot.wtk.Component component, char character) {
        boolean consumed = super.keyTyped(component, character);

        if (paragraphViews.getLength() > 0) {
            TextArea textArea = (TextArea)getComponent();

            if (textArea.isEditable()) {
                // Ignore characters in the control range and the ASCII delete
                // character as well as meta key presses
                if (character > 0x1F
                        && character != 0x7F
                        && !Keyboard.isPressed(Keyboard.Modifier.META)) {
                    int selectionLength = textArea.getSelectionLength();

                    if (textArea.getCharacterCount() - selectionLength + 1 > textArea.getMaximumLength()) {
                        Toolkit.getDefaultToolkit().beep();
                    } else {
                        int selectionStart = textArea.getSelectionStart();
                        textArea.removeText(selectionStart, selectionLength);
                        textArea.insertText(Character.toString(character), selectionStart);
                    }

                    showCaret(true);
                }
            }
        }

        return consumed;
    }

    @Override
    public boolean keyPressed(org.apache.pivot.wtk.Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = false;

        if (paragraphViews.getLength() > 0) {
            TextArea textArea = (TextArea)getComponent();
            boolean commandPressed = Keyboard.isPressed(Platform.getCommandModifier());
            boolean wordNavPressed = Keyboard.isPressed(Platform.getWordNavigationModifier());
            boolean shiftPressed = Keyboard.isPressed(Keyboard.Modifier.SHIFT);
            boolean ctrlPressed = Keyboard.isPressed(Keyboard.Modifier.CTRL);
            boolean metaPressed = Keyboard.isPressed(Keyboard.Modifier.META);
            boolean isEditable = textArea.isEditable();

            if (keyCode == Keyboard.KeyCode.ENTER
                    && acceptsEnter && isEditable
                    && Keyboard.getModifiers() == 0) {
                int index = textArea.getSelectionStart();
                textArea.removeText(index, textArea.getSelectionLength());
                textArea.insertText("\n", index);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.DELETE
                    && isEditable) {
                int index = textArea.getSelectionStart();

                if (index < textArea.getCharacterCount()) {
                    int count = Math.max(textArea.getSelectionLength(), 1);
                    textArea.removeText(index, count);

                    consumed = true;
                }
            } else if (keyCode == Keyboard.KeyCode.BACKSPACE
                    && isEditable) {
                int index = textArea.getSelectionStart();
                int count = textArea.getSelectionLength();

                if (count == 0
                        && index > 0) {
                    textArea.removeText(index - 1, 1);
                    consumed = true;
                } else {
                    textArea.removeText(index, count);
                    consumed = true;
                }
            } else if (keyCode == Keyboard.KeyCode.TAB
                    && (acceptsTab != ctrlPressed)
                    && isEditable) {
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();

                int rowOffset = textArea.getRowOffset(selectionStart);
                int linePos = selectionStart - rowOffset;
                StringBuilder tabBuilder = new StringBuilder(tabWidth);
                for (int i = 0; i < tabWidth - (linePos % tabWidth); i++) {
                    tabBuilder.append(" ");
                }

                if (textArea.getCharacterCount() - selectionLength + tabWidth > textArea.getMaximumLength()) {
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    textArea.removeText(selectionStart, selectionLength);
                    textArea.insertText(tabBuilder, selectionStart);
                }

                showCaret(true);

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.HOME
                    || (keyCode == Keyboard.KeyCode.LEFT && metaPressed)) {
                int start;
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();
                if (ctrlPressed) {
                    // Move the caret to the beginning of the text
                    start = 0;
                } else {
                    // Move the caret to the beginning of the line
                    start = getRowOffset(selectionStart);
                }

                if (shiftPressed) {
                    selectionLength += selectionStart - start;
                } else {
                    selectionLength = 0;
                }

                if (selectionStart >= 0) {
                    textArea.setSelection(start, selectionLength);
                    scrollCharacterToVisible(start);

                    caretX = caret.x;

                    consumed = true;
                }
            } else if (keyCode == Keyboard.KeyCode.END
                    || (keyCode == Keyboard.KeyCode.RIGHT && metaPressed)) {
                int end;
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();
                int index = selectionStart + selectionLength;

                if (ctrlPressed) {
                    // Move the caret to end of the text
                    end = textArea.getCharacterCount();
                } else {
                    // Move the caret to the end of the line
                    int rowOffset = getRowOffset(index);
                    int rowLength = getRowLength(index);
                    end = rowOffset + rowLength;
                }

                if (shiftPressed) {
                    selectionLength += end - index;
                } else {
                    selectionStart = end;
                    if (selectionStart < textArea.getCharacterCount()
                            && textArea.getCharacterAt(selectionStart) != '\n') {
                        selectionStart--;
                    }

                    selectionLength = 0;
                }

                if (selectionStart + selectionLength <= textArea.getCharacterCount()) {
                    textArea.setSelection(selectionStart, selectionLength);
                    scrollCharacterToVisible(selectionStart + selectionLength);

                    caretX = caret.x;
                    if (selection != null) {
                        caretX += selection.getBounds2D().getWidth();
                    }

                    consumed = true;
                }
            } else if (keyCode == Keyboard.KeyCode.LEFT) {
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();

                if (wordNavPressed) {
                    // Move the caret to the start of the next word to the left
                    if (selectionStart > 0) {
                        // Skip over any space immediately to the left
                        int index = selectionStart;
                        while (index > 0
                                && Character.isWhitespace(textArea.getCharacterAt(index - 1))) {
                            index--;
                        }

                        // Skip over any word-letters to the left
                        while (index > 0
                                && !Character.isWhitespace(textArea.getCharacterAt(index - 1))) {
                            index--;
                        }

                        if (shiftPressed) {
                            selectionLength += selectionStart - index;
                        } else {
                            selectionLength = 0;
                        }

                        selectionStart = index;
                    }
                } else if (shiftPressed) {
                    if (anchor != -1) {
                        if (selectionStart < anchor) {
                            if (selectionStart > 0) {
                                selectionStart--;
                                selectionLength++;
                            }
                        } else {
                            if (selectionLength > 0) {
                                selectionLength--;
                            } else {
                                selectionStart--;
                                selectionLength++;
                            }
                        }
                    } else {
                        // Add the previous character to the selection
                        anchor = selectionStart;
                        if (selectionStart > 0) {
                            selectionStart--;
                            selectionLength++;
                        }
                    }
                } else {
                    // Move the caret back by one character
                    if (selectionLength == 0
                            && selectionStart > 0) {
                        selectionStart--;
                    }

                    // Clear the selection
                    anchor = -1;
                    selectionLength = 0;
                }

                if (selectionStart >= 0) {
                    textArea.setSelection(selectionStart, selectionLength);
                    scrollCharacterToVisible(selectionStart);

                    caretX = caret.x;

                    consumed = true;
                }
            } else if (keyCode == Keyboard.KeyCode.RIGHT) {
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();

                if (wordNavPressed) {
                    // Move the caret to the start of the next word to the right
                    if (selectionStart < textArea.getCharacterCount()) {
                        int index = selectionStart + selectionLength;

                        // Skip over any space immediately to the right
                        while (index < textArea.getCharacterCount()
                                && Character.isWhitespace(textArea.getCharacterAt(index))) {
                            index++;
                        }

                        // Skip over any word-letters to the right
                        while (index < textArea.getCharacterCount()
                                && !Character.isWhitespace(textArea.getCharacterAt(index))) {
                            index++;
                        }

                        if (shiftPressed) {
                            selectionLength = index - selectionStart;
                        } else {
                            selectionStart = index;
                            selectionLength = 0;
                        }
                    }
                } else if (shiftPressed) {
                    if (anchor != -1) {
                        if (selectionStart < anchor) {
                            selectionStart++;
                            selectionLength--;
                        } else {
                            selectionLength++;
                        }
                    } else {
                        // Add the next character to the selection
                        anchor = selectionStart;
                        selectionLength++;
                    }
                } else {
                    // Move the caret forward by one character
                    if (selectionLength == 0) {
                        selectionStart++;
                    } else {
                        selectionStart += selectionLength;
                    }

                    // Clear the selection
                    anchor = -1;
                    selectionLength = 0;
                }

                if (selectionStart + selectionLength <= textArea.getCharacterCount()) {
                    textArea.setSelection(selectionStart, selectionLength);
                    scrollCharacterToVisible(selectionStart + selectionLength);

                    caretX = caret.x;
                    if (selection != null) {
                        caretX += selection.getBounds2D().getWidth();
                    }

                    consumed = true;
                }
            } else if (keyCode == Keyboard.KeyCode.UP) {
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();

                int index = -1;
                if (shiftPressed) {
                    if (anchor == -1) {
                        anchor = selectionStart;
                        index = getNextInsertionPoint(caretX, selectionStart, TextArea.ScrollDirection.UP);
                        if (index != -1) {
                            selectionLength = selectionStart - index;
                        }
                    } else {
                        if (selectionStart < anchor) {
                            // continue upwards
                            index = getNextInsertionPoint(caretX, selectionStart, TextArea.ScrollDirection.UP);
                            if (index != -1) {
                                selectionLength = selectionStart + selectionLength - index;
                            }
                        } else {
                            // reduce downward size
                            Bounds trailingSelectionBounds = getCharacterBounds(selectionStart + selectionLength - 1);
                            int x = trailingSelectionBounds.x + trailingSelectionBounds.width;
                            index = getNextInsertionPoint(x, selectionStart + selectionLength - 1, TextArea.ScrollDirection.UP);
                            if (index != -1) {
                                if (index < anchor) {
                                    selectionLength = anchor - index;
                                } else {
                                    selectionLength = index - selectionStart;
                                    index = selectionStart;
                                }
                            }
                        }
                    }
                } else {
                    index = getNextInsertionPoint(caretX, selectionStart, TextArea.ScrollDirection.UP);
                    if (index != -1) {
                        selectionLength = 0;
                    }
                    anchor = -1;
                }

                if (index != -1) {
                    textArea.setSelection(index, selectionLength);
                    scrollCharacterToVisible(index);
                    caretX = caret.x;
                }

                consumed = true;
            } else if (keyCode == Keyboard.KeyCode.DOWN) {
                int selectionStart = textArea.getSelectionStart();
                int selectionLength = textArea.getSelectionLength();

                if (shiftPressed) {
                    int from;
                    int x;
                    int index;

                    if (anchor == -1) {
                        anchor = selectionStart;
                        index = getNextInsertionPoint(caretX, selectionStart, TextArea.ScrollDirection.DOWN);
                        if (index != -1) {
                            selectionLength = index - selectionStart;
                        }
                    } else {
                        if (selectionStart < anchor) {
                            // Reducing upward size
                            // Get next insertion point from leading selection character
                            from = selectionStart;
                            x = caretX;

                            index = getNextInsertionPoint(x, from, TextArea.ScrollDirection.DOWN);

                            if (index != -1) {
                                if (index < anchor) {
                                    selectionStart = index;
                                    selectionLength = anchor - index;
                                } else {
                                    selectionStart = anchor;
                                    selectionLength = index - anchor;
                                }

                                textArea.setSelection(selectionStart, selectionLength);
                                scrollCharacterToVisible(selectionStart);
                            }
                        } else {
                            // Increasing downward size
                            // Get next insertion point from right edge of trailing selection
                            // character
                            from = selectionStart + selectionLength - 1;

                            Bounds trailingSelectionBounds = getCharacterBounds(from);
                            x = trailingSelectionBounds.x + trailingSelectionBounds.width;

                            index = getNextInsertionPoint(x, from, TextArea.ScrollDirection.DOWN);

                            if (index != -1) {
                                // If the next character is a paragraph terminator and is
                                // not the final terminator character, increment the selection
                                if (index < textArea.getCharacterCount() - 1
                                        && textArea.getCharacterAt(index) == '\n') {
                                    index++;
                                }

                                textArea.setSelection(selectionStart, index - selectionStart);
                                scrollCharacterToVisible(index);
                            }
                        }
                    }
                } else {
                    int from;
                    if (selectionLength == 0) {
                        // Get next insertion point from leading selection character
                        from = selectionStart;
                    } else {
                        // Get next insertion point from trailing selection character
                        from = selectionStart + selectionLength - 1;
                    }

                    int index = getNextInsertionPoint(caretX, from, TextArea.ScrollDirection.DOWN);

                    if (index != -1) {
                        textArea.setSelection(index, 0);
                        scrollCharacterToVisible(index);
                        caretX = caret.x;
                    }
                    anchor = -1;
                }

                consumed = true;
            } else if (commandPressed) {
                if (keyCode == Keyboard.KeyCode.A) {
                    textArea.setSelection(0, textArea.getCharacterCount());
                    consumed = true;
                } else if (keyCode == Keyboard.KeyCode.X
                        && isEditable) {
                    textArea.cut();
                    consumed = true;
                } else if (keyCode == Keyboard.KeyCode.C) {
                    textArea.copy();
                    consumed = true;
                } else if (keyCode == Keyboard.KeyCode.V
                        && isEditable) {
                    textArea.paste();
                    consumed = true;
                } else if (keyCode == Keyboard.KeyCode.Z
                        && isEditable) {
                    if (!shiftPressed) {
                        textArea.undo();
                    }
                    consumed = true;
                } else if (keyCode == Keyboard.KeyCode.TAB) {
                    // Only here if acceptsTab is false
                    consumed = super.keyPressed(component, keyCode, keyLocation);
                }
            } else if (keyCode == Keyboard.KeyCode.INSERT) {
                if (shiftPressed && isEditable) {
                    textArea.paste();
                    consumed = true;
                }
            } else {
                consumed = super.keyPressed(component, keyCode, keyLocation);
            }
        }

        return consumed;
    }
*/

        /*
    @Override
    public void enabledChanged(org.apache.pivot.wtk.Component component) {
        super.enabledChanged(component);
        repaintComponent();
    }

    @Override
    public void focusedChanged(org.apache.pivot.wtk.Component component, org.apache.pivot.wtk.Component obverseComponent) {
        super.focusedChanged(component, obverseComponent);

        TextArea textArea = (TextArea)getComponent();
        if (textArea.isFocused()
                && textArea.getSelectionLength() == 0) {
            if (textArea.isValid()) {
                scrollCharacterToVisible(textArea.getSelectionStart());
            }

            showCaret(true);
        } else {
            showCaret(false);
        }

        repaintComponent();
    }

    @Override
    public void selectionChanged(TextArea textArea, int previousSelectionStart,
                                 int previousSelectionLength) {
        // If the text area is valid, repaint the selection state; otherwise,
        // the selection will be updated in layout()
        if (textArea.isValid()) {
            if (selection == null) {
                // Repaint previous caret bounds
                textArea.repaint(caret.x, caret.y, caret.width, caret.height);
            } else {
                // Repaint previous selection bounds
                Rectangle bounds = selection.getBounds();
                textArea.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            updateSelection();

            if (selection == null) {
                showCaret(textArea.isFocused());
            } else {
                showCaret(false);

                // Repaint current selection bounds
                Rectangle bounds = selection.getBounds();
                textArea.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }

    private void updateSelection() {
        TextArea textArea = (TextArea)getComponent();

        if (paragraphViews.getLength() > 0) {
            // Update the caret
            int selectionStart = textArea.getSelectionStart();

            Bounds leadingSelectionBounds = getCharacterBounds(selectionStart);
            caret = leadingSelectionBounds.toRectangle();
            caret.width = 1;

            // Update the selection
            int selectionLength = textArea.getSelectionLength();

            if (selectionLength > 0) {
                int selectionEnd = selectionStart + selectionLength - 1;
                Bounds trailingSelectionBounds = getCharacterBounds(selectionEnd);
                selection = new Area();

                int firstRowIndex = getRowAt(selectionStart);
                int lastRowIndex = getRowAt(selectionEnd);

                if (firstRowIndex == lastRowIndex) {
                    selection.add(new Area(new Rectangle(leadingSelectionBounds.x,
                            leadingSelectionBounds.y, trailingSelectionBounds.x
                            + trailingSelectionBounds.width - leadingSelectionBounds.x,
                            trailingSelectionBounds.y + trailingSelectionBounds.height
                                    - leadingSelectionBounds.y)));
                } else {
                    int width = getWidth();

                    selection.add(new Area(new Rectangle(leadingSelectionBounds.x,
                            leadingSelectionBounds.y, width - margin.right - leadingSelectionBounds.x,
                            leadingSelectionBounds.height)));

                    if (lastRowIndex - firstRowIndex > 0) {
                        selection.add(new Area(new Rectangle(margin.left, leadingSelectionBounds.y
                                + leadingSelectionBounds.height, width - (margin.left + margin.right),
                                trailingSelectionBounds.y
                                        - (leadingSelectionBounds.y + leadingSelectionBounds.height))));
                    }

                    selection.add(new Area(new Rectangle(margin.left, trailingSelectionBounds.y,
                            trailingSelectionBounds.x + trailingSelectionBounds.width - margin.left,
                            trailingSelectionBounds.height)));
                }
            } else {
                selection = null;
            }
        } else {
            // Clear the caret and the selection
            caret = new Rectangle();
            selection = null;
        }
    }

    private void showCaret(boolean show) {
        if (scheduledBlinkCaretCallback != null) {
            scheduledBlinkCaretCallback.cancel();
        }

        if (show) {
            caretOn = true;
            scheduledBlinkCaretCallback = ApplicationContext.scheduleRecurringCallback(
                    blinkCaretCallback, Platform.getCursorBlinkRate());

            // Run the callback once now to show the cursor immediately
            blinkCaretCallback.run();
        } else {
            scheduledBlinkCaretCallback = null;
        }
    }
    */
}
