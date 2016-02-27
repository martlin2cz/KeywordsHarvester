package cz.martlin.kh.logic.harvest2.tree;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Iterates over {@link BFSLazyTree} instance. The method next does
 * Breadth-first-search throught the tree. When one layer is completed, method
 * {@link #next()} returns null and on next call automatically invokes
 * {@link BFSLazyTree#toNextLayer()} and starts next layer.
 * 
 * @author martin
 * 
 */
public class BFSLazyTreeIterator implements Serializable, Iterator<String> {
	private static final long serialVersionUID = 4865752668029120010L;

	private final BFSLazyTree tree;

	public BFSLazyTreeIterator(BFSLazyTree tree) {
		this.tree = tree;
	}

	@Override
	public boolean hasNext() {
		return tree.isHasSomeItems();
	}

	@Override
	public String next() {
		if (!tree.isHasSomeItemsToProcess()) {
			tree.calculateNextChildren();
		}

		String next = tree.nextToProcess();
		return next;
	}

	public Set<String> next(int count) {
		Set<String> result = new LinkedHashSet<>(count);

		for (int i = 0; i < count; i++) {
			String next = next();
			result.add(next);
		}

		return result;
	}
}
