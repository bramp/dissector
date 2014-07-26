package net.bramp.dissector.pivot;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.png.PngDissector;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author bramp
 */
public class MainView extends Window implements Bindable {

    @BXML
    private TreeView treeView;

    /*
    public TreeBranch nodeToTreeView(net.bramp.dissector.node.TreeNode node) {
        TreeBranch root = new TreeBranch();
        for (java.util.Map.Entry<String, Node> e : node.getChildren()) {
            Node n = e.getValue();
            if (n instanceof net.bramp.dissector.node.TreeNode) {
                root.add( nodeToTreeView )
            } else {
                root.add(new TreeNode(n.toString()));
            }

        }
    }
    */

    public class DissectorTreeViewNodeRenderer extends BoxPane implements TreeView.NodeRenderer {

        @Override
        public void render(Object node, Tree.Path path, int rowIndex, TreeView treeView, boolean expanded, boolean selected, TreeView.NodeCheckState checkState, boolean highlighted, boolean disabled) {

        }

        @Override
        public String toString(Object node) {
            return null;
        }
    }

    public static ExtendedRandomAccessFile open(Class clazz, String filename) throws IOException {
        try {
            URI path = clazz.getResource(filename).toURI();
            return new ExtendedRandomAccessFile( new File( path ), "r" );

        } catch (URISyntaxException e) {
            throw new IOException("Failed to open " + filename, e);
        }
    }

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {

        try {
            ExtendedRandomAccessFile in = open(getClass(), "z09n2c08.png");
            PngDissector dissector = new PngDissector().read(in);

            //treeView.setNodeRenderer();
            //treeView.setTreeData( dissector );

        } catch (IOException e) {
            e.printStackTrace();
        }

        //selectionLabel = (Label)namespace.get("selectionLabel");
        //treeView = (TreeView)namespace.get("treeView");
        //treeView.setTreeData();
        /*
        listView.getListViewSelectionListeners().add(new ListViewSelectionListener() {
            @Override
            public void selectedRangeAdded(ListView listView, int rangeStart, int rangeEnd) {
                updateSelection(listView);
            }

            @Override
            public void selectedRangeRemoved(ListView listView, int rangeStart, int rangeEnd) {
                updateSelection(listView);
            }

            @Override
            public void selectedRangesChanged(ListView listView, Sequence<Span> previousSelectedRanges) {
                if (previousSelectedRanges != null
                        && previousSelectedRanges != listView.getSelectedRanges()) {
                    updateSelection(listView);
                }
            }

            @Override
            public void selectedItemChanged(ListView listView, Object previousSelectedItem) {
                // No-op
            }

            private void updateSelection(ListView listView) {
                String selectionText = "";

                Sequence<Span> selectedRanges = listView.getSelectedRanges();
                for (int i = 0, n = selectedRanges.getLength(); i < n; i++) {
                    Span selectedRange = selectedRanges.get(i);

                    for (int j = selectedRange.start;
                         j <= selectedRange.end;
                         j++) {
                        if (selectionText.length() > 0) {
                            selectionText += ", ";
                        }

                        Object item = listView.getListData().get(j);
                        String text;
                        if (item instanceof ListItem) {  // item is a listItem (for example because it has an image)
                            text = ((ListItem) item).getText();
                        } else {  // item is a standard item for listData
                            text = item.toString();
                        }
                        selectionText += text;
                    }
                }

                selectionLabel.setText(selectionText);
            }
        });
        */
    }
}
