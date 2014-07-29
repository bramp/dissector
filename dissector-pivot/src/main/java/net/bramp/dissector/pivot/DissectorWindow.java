package net.bramp.dissector.pivot;

import com.google.common.collect.ImmutableList;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.Dissector;
import net.bramp.dissector.node.Node;
import net.bramp.hex.HexEditor;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.*;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Filter;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.content.TreeBranch;
import org.apache.pivot.wtk.content.TreeNode;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author bramp
 */
public class DissectorWindow extends Window implements Bindable {

	@BXML
	private SplitPane splitPane = null;

	@BXML
	private ScrollPane editorScrollPane = null;

    @BXML
    private HexEditor editor = null;

	@BXML
	private TreeView treeView = null;

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {

	    // Ensure the Hex Editor pane has a min width
	    splitPane.getTopLeft().setMinimumWidth( editorScrollPane.getPreferredWidth() );
	    treeView.getTreeViewSelectionListeners().add(new TreeViewSelectionListener.Adapter() {

		    /**
		     * Called when a tree view's selected node has changed.
		     *
		     * @param treeView
		     * @param previousSelectedNode
		     */
		    public void selectedNodeChanged(TreeView treeView, Object previousSelectedNode) {
			    TreeNode treeNode = (TreeNode) treeView.getSelectedNode();
			    java.util.Map.Entry<String, Node> selected = (java.util.Map.Entry<String, Node>) treeNode.getUserData();
			    Node node = selected.getValue();

			    //System.out.println( node.getStart() + " " + (node.getEnd() - node.getStart()) + " " + node );
			    editor.setSelection( node.getStart(), node.getEnd() - node.getStart() );
		    }

	    });
    }

    @Override
    public void load(Object context) {
        super.load(context);
    }

	/**
	 * Uses the file and dissector for display
	 * The dissector is assumed to have not started yet
	 * @param file
	 * @param dissector
	 * @throws IOException
	 */
	public void loadDissector(ExtendedRandomAccessFile file, Dissector dissector) throws IOException {
		editor.setFile( file );
		//editor.setSelection(5, 100); // For testing

		// TODO Show loading window
		try {
			dissector.read(file);
		} catch (Exception e) {
			// TODO Make a popup
			System.out.println("Caught Exception reading file " + e);
		}

		//treeView.setNodeRenderer( new DissectorTreeViewNodeRenderer() );
		treeView.setTreeData( new MyTreeBranch(dissector) );
	}

}
