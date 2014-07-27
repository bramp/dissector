package net.bramp.dissector.pivot;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import net.bramp.dissector.node.Node;
import net.bramp.dissector.node.TreeNode;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.content.TreeBranch;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps the Dissector Nodes in Pivot TreeBranch/TreeNodes
 *
 * @author bramp
 */
public class MyTreeBranch extends TreeBranch {

	final net.bramp.dissector.node.TreeNode root;
	final ImmutableList<Map.Entry<String, Node>> children;

	final static Function<Map.Entry<String, Node>, org.apache.pivot.wtk.content.TreeNode> toTreeNode = new Function<Map.Entry<String, Node>, org.apache.pivot.wtk.content.TreeNode>() {
		@Nullable
		@Override
		public org.apache.pivot.wtk.content.TreeNode apply(@Nullable Map.Entry<String, Node> input) {
			Node value = input.getValue();
			org.apache.pivot.wtk.content.TreeNode node;

			if (value instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) value;
				node = new MyTreeBranch(treeNode, input.getKey() + " " + treeNode.toString());
			} else {
				String text = input.getKey() + ": " + value.toString();
				node = new org.apache.pivot.wtk.content.TreeNode(text);
			}

			node.setUserData( input );

			return node;
		}
	};

	public MyTreeBranch(net.bramp.dissector.node.TreeNode root) {
		this(root, null);
	}

	public MyTreeBranch(net.bramp.dissector.node.TreeNode root, String text) {
		super(text);
		this.root = root;
		children = ImmutableList.copyOf( root.getChildren().entrySet() ); // TODO Make this lazy
	}


	@Override
	public int add(org.apache.pivot.wtk.content.TreeNode item) {
		throw new RuntimeException("Not implement");
	}

	@Override
	public void insert(org.apache.pivot.wtk.content.TreeNode item, int index) {
		throw new RuntimeException("Not implement");
	}

	@Override
	public org.apache.pivot.wtk.content.TreeNode update(int index, org.apache.pivot.wtk.content.TreeNode item) {
		throw new RuntimeException("Not implement");
	}

	@Override
	public int remove(org.apache.pivot.wtk.content.TreeNode item) {
		throw new RuntimeException("Not implement");
	}

	@Override
	public Sequence<org.apache.pivot.wtk.content.TreeNode> remove(int index, int count) {
		throw new RuntimeException("Not implement");
	}

	@Override
	public org.apache.pivot.wtk.content.TreeNode get(int index) {
		return toTreeNode.apply( children.get(index) );
	}

	@Override
	public int indexOf(org.apache.pivot.wtk.content.TreeNode item) {
		return children.indexOf(checkNotNull(item).getUserData());
	}

	@Override
	public void clear() {
		throw new RuntimeException("Not implement");
	}

	@Override
	public boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	public Comparator<org.apache.pivot.wtk.content.TreeNode> getComparator() {
		throw new RuntimeException("Not implement");
	}

	@Override
	public void setComparator(Comparator<org.apache.pivot.wtk.content.TreeNode> comparator) {
		throw new RuntimeException("Not implement");
	}

	@Override
	public int getLength() {
		return children.size();
	}

	@Override
	public Iterator<org.apache.pivot.wtk.content.TreeNode> iterator() {
		return Iterators.transform(children.iterator(), toTreeNode);
	}

}
