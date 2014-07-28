package net.bramp.dissector;

import com.google.common.base.Strings;
import net.bramp.dissector.node.ArrayNode;
import net.bramp.dissector.node.Node;
import net.bramp.dissector.node.TreeNode;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * @author bramp
 */
public class NodePrinter {

    StringBuilder indent = new StringBuilder();
    PrintStream out = System.out;

    public void print(Node node) {
        print(node, 0);
    }

    public void print(Node node, int depth) {
        if (node instanceof TreeNode) {
            for(Map.Entry<String, Node> m : ((TreeNode)node).getChildren().entrySet() ) {
                out.print(Strings.repeat("  ", depth) + m.getKey() + " : ");

                if (m.getValue() instanceof TreeNode)
                    out.println(((TreeNode) m.getValue()).getTitle());

                print(m.getValue(), depth + 1);

	            if (!(m.getValue() instanceof TreeNode))
                    out.println();
            }
        } else {
            out.print(node.toString());
        }
    }
}
