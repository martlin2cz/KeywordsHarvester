package cz.martlin.kh.logic.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Implements iterator of some {@link Collection}, but this one bothly
 * implements {@link Serializable} interface.
 * 
 * @author martin
 * 
 * @param <T>
 */
public class SerializableIterator<T> implements Serializable, Iterator<T> {
	private static final long serialVersionUID = -5919225348366946853L;

	private final Queue<T> items;

	public SerializableIterator(Collection<T> colection) {
		this.items = new LinkedList<>();
		this.items.addAll(colection);
	}

	@Override
	public boolean hasNext() {
		return !items.isEmpty();
	}

	@Override
	public T next() {
		return items.poll();
	}

	@Override
	public void remove() {
		next();
	}

	@Override
	public void forEachRemaining(Consumer<? super T> action) {
		items.forEach(action);
	}

}
