package net.bramp.dissector.node;

import com.google.common.base.Preconditions;
import net.bramp.dissector.io.DataPositionInputStream;
import net.bramp.dissector.io.PositionInputStream;

/**
 * @author bramp
 */
public class Node implements Comparable<Node> {
    public long start;
    public long end;

    public Node() {}

    protected Node setPos(DataPositionInputStream in) {
        this.start = in.getPosition();
        this.end   = -1;
        return this;
    }

    protected Node setPos(DataPositionInputStream in, long length) {
        this.start = in.getPosition();
        this.end   = start + length;
        return this;
    }

    protected Node setPos(long start, long end) {
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
}
