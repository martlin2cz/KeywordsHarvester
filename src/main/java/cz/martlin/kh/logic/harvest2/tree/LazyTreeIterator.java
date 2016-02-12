package cz.martlin.kh.logic.harvest2.tree;

import java.io.Serializable;
import java.util.Iterator;

import cz.martlin.kh.logic.utils.SerializableIterator;

/**
 * Iterates over {@link LazyTree} instance. The method next does
 * Breadth-first-search throught the tree. When one layer is completed, method
 * {@link #next()} returns null and on next call automatically invokes
 * {@link LazyTree#toNextLayer()} and starts next layer.
 * 
 * @author martin
 * 
 */
public class LazyTreeIterator implements Serializable, Iterator<String> {
	private static final long serialVersionUID = 4865752668029120010L;

	private final LazyTree tree;
	private SerializableIterator<String> roots;

	public LazyTreeIterator(LazyTree tree) {
		this.tree = tree;
		reset();
	}

	@Override
	public boolean hasNext() {
		return tree.getRootsCount() > 0;
	}

	@Override
	public String next() {
		if (roots == null) {
			tree.toNextLayer();
			roots = new SerializableIterator<>(tree.getRoots());
		}

		if (roots.hasNext()) {
			return toNextAndCalcItsChildren();
		} else {
			roots = null;
			return null;
		}
	}

	private String toNextAndCalcItsChildren() {
		String next = roots.next();
		tree.forceChildrenCalculation(next);
		return next;
	}

	public void reset() {
		roots = new SerializableIterator<>(tree.getRoots());
	}

}
