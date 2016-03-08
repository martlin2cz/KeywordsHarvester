package cz.martlin.kh.logic.harvest2.tree;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import cz.martlin.kh.logic.utils.Interruptable;

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

	/**
	 * Loads next count items, or less if have been interrupt interrupted
	 * 
	 * @param count
	 * @param interrupt
	 * @return
	 */
	public Set<String> next(int count, Interruptable interrupt) {
		Set<String> result = new LinkedHashSet<>(count);

		for (int i = 0; i < count; i++) {
			if (interrupt != null && interrupt.isInterrupted()) {
				return result;
			}

			String next = next();
			result.add(next);
		}

		return result;
	}
}
