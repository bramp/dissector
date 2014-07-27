package net.bramp.dissector.node;

import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;
import java.util.Map;

/**
 * Note only works up to 63 bits
 * @author bramp
 */
public class MaskNode<T extends Number> extends Node<Long> {

    final Map<T, String> values;

    long value;

	public MaskNode(Map<T, String> values, Node<T> in) {
		this.values = values;
		super.setPos(in.getStart(), in.getEnd());
		this.value = in.value().longValue();
	}

    public Long value() {
        return value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<T, String> bit : values.entrySet()) {
	        long b = bit.getKey().longValue();
            if ((b & value) == b) {
                sb.append(bit.getValue()).append("|");
            }
        }

        // Chop the trailing |
        if (sb.length() > 0)
            sb.setLength( sb.length() - 1 );

        return sb.toString();
    }

    public Map<T, String> getPossibleValues() {
        return ImmutableMap.copyOf(values);
    }
}
