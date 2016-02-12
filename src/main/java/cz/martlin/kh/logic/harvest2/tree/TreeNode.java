package cz.martlin.kh.logic.harvest2.tree;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Node of Lazy (or some other tree) Tree. Nothing more special.
 * 
 * @author martin
 * 
 */
public class TreeNode implements Serializable {
	private static final long serialVersionUID = 9011773824162874741L;

	public static final TreeNode EMPTY_NODE = new TreeNode(null,
			new LinkedList<String>());

	private final String label;
	private final LinkedHashMap<String, TreeNode> children;

	public TreeNode(String label, Iterable<String> children) {
		this.label = label;
		this.children = new LinkedHashMap<>();

		for (String child : children) {
			this.children.put(child, EMPTY_NODE);
		}
	}

	public String getLabel() {
		return label;
	}

	public LinkedHashMap<String, TreeNode> getChildren() {
		return children;
	}

	public boolean isEmptyNode() {
		return this == EMPTY_NODE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeNode other = (TreeNode) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (isEmptyNode()) {
			return "TreeNode[]";
		} else {
			return "TreeNode [label=" + label + ", children=" + children + "]";
		}
	}

}
