package net.bramp.dissector.node;

import com.google.common.base.Preconditions;
import net.bramp.dissector.OrderPreservingMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author bramp
 */
public class TreeNode extends Node {

    Map<String, Node> children = new OrderPreservingMap<>();

    public TreeNode() {
        super();
    }

    public <T extends Node> T addChild(String title, T node) {
        Preconditions.checkArgument(node.getStart() >= 0);
        Preconditions.checkArgument(node.getEnd() >= node.getStart());

        children.put(title, node);
        this.start = Math.min(this.start, node.start);
        this.end   = Math.max(this.end,   node.end);
        return node;
    }

    public TreeNode read(ExtendedRandomAccessFile in) throws IOException {
        super.setPos(in, 0);
        return this;
    }

    public Set<Map.Entry<String, Node>> getChildren() {
        return children.entrySet();
    }

}
