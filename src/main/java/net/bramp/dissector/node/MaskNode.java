package net.bramp.dissector.node;

import net.bramp.dissector.io.DataPositionInputStream;

import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class MaskNode extends Node {

    final Map<Integer, String> values;

    int value;

    public MaskNode(Map<Integer, String> values) {
        this.values = values;
    }

    public MaskNode read(DataPositionInputStream in, int length) throws IOException {
        super.setPos(in, length);

        value = in.readUnsignedIntOfLength(length);

        return this;
    }

    public int value() {
        return value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> bit : values.entrySet()) {
            int b = bit.getKey();
            if ((b & value) == b) {
                sb.append(bit.getValue()).append("|");
            }
        }

        // Chop the trailing |
        if (sb.length() > 0)
            sb.setLength( sb.length() - 1 );

        return sb.toString();
    }
}
