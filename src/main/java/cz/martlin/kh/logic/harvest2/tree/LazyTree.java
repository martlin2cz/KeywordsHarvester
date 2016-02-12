package cz.martlin.kh.logic.harvest2.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import cz.martlin.kh.logic.harvest2.RelatedKeywordsTree;

/**
 * Implements lazy-loaded and memory-safe tree. When layer of nodes is
 * processed, it can be poped out and replaced with their children.
 * 
 * @author martin
 * 
 */
public abstract class LazyTree implements Serializable, Iterable<String> {

	private static final long serialVersionUID = -5150024522240632714L;

	protected final LinkedHashMap<String, TreeNode> rootChildren;
	protected int level;

	public LazyTree(Collection<String> initials) {
		this.rootChildren = new LinkedHashMap<>();
		this.level = 0;

		addChildWith(initials);
	}

	protected void addChildWith(Collection<String> initials) {
		if (initials.isEmpty()) {
			return;
		}

		String initial = initials.iterator().next();

		Set<String> children = new LinkedHashSet<>(initials);
		children.remove(initial);

		TreeNode node = new TreeNode(initial, children);
		rootChildren.put(initial, node);

		// for (String initial : initials) {
		// rootChildren.put(initial, TreeNode.EMPTY_NODE);
		// }
	}

	public int getLevel() {
		return level;
	}

	public int getRootsCount() {
		return rootChildren.size();
	}

	@Override
	public Iterator<String> iterator() {
		return new LazyTreeIterator(this);
	}

	public LinkedHashSet<String> getRoots() {
		return new LinkedHashSet<>(rootChildren.keySet());
	}

	public LinkedHashSet<String> getChildren(String label) {
		TreeNode childNode = rootChildren.get(label);

		if (childNode == null || childNode.isEmptyNode()) {
			forceChildrenCalculation(label);
			childNode = rootChildren.get(label);
		}

		return new LinkedHashSet<>(childNode.getChildren().keySet());
	}

	private TreeNode calculateChildNode(String label) {
		Iterable<String> children = createChildrenOf(label);
		TreeNode node = new TreeNode(label, children);
		return node;
	}

	/**
	 * 
	 * @param label
	 * @return
	 */
	public abstract Iterable<String> createChildrenOf(String label);

	/**
	 * 
	 * @param label
	 */
	public void forceChildrenCalculation(String label) {
		TreeNode childNode;
		childNode = calculateChildNode(label);
		rootChildren.put(label, childNode);
	}

	/**
	 * 
	 */
	public void toNextLayer() {
		LinkedHashMap<String, TreeNode> newRoot = new LinkedHashMap<>(
				rootChildren.size());

		for (String label : getRoots()) {
			Set<String> children = getChildren(label);

			for (String child : children) {
				newRoot.put(child, TreeNode.EMPTY_NODE);
			}
		}

		rootChildren.clear();
		rootChildren.putAll(newRoot);

		level++;
	}

	public void add(Set<String> set) {
		addChildWith(set);
	}

	public void remove(String word) {
		rootChildren.remove(word);

		for (TreeNode node : rootChildren.values()) {
			if (!node.isEmptyNode()) {
				node.getChildren().remove(word);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rootChildren == null) ? 0 : rootChildren.hashCode());
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
		RelatedKeywordsTree other = (RelatedKeywordsTree) obj;
		if (rootChildren == null) {
			if (other.rootChildren != null)
				return false;
		} else if (!rootChildren.equals(other.rootChildren))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RelatedKeywordsTree [rootChildren=" + rootChildren + "]";
	}

}