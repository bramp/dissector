package net.bramp.dissector.node;

import com.google.common.base.Preconditions;
import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;

/**
 * @author bramp
 */
public abstract class Node<T> implements Comparable<Node> {
    public long start;
    public long end;

    public Node() {}

	public Node setPos(ExtendedRandomAccessFile in) throws IOException {
        this.start = in.getFilePointer();
        this.end   = -1;
        return this;
    }

	public Node setPos(ExtendedRandomAccessFile in, long length) throws IOException {
        Preconditions.checkArgument(length >= 0 && length <= Integer.MAX_VALUE, "Illegal length value " + length);
        this.start = in.getFilePointer();
        this.end   = start + length;
        return this;
    }

    public Node setPos(long start, long end) {
        this.start = start;
        this.end   = end;
        return this;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    /**
     * Returns the order the Nodes should appear in
     * @param o
     * @return
     */
    @Override
    public int compareTo(Node o) {
        Preconditions.checkNotNull(o);
        return Long.compare(start, o.start);
    }

	public abstract T value();

	public long length() {
		return getEnd() - getStart();
	}

}
