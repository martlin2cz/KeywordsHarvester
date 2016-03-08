package cz.martlin.kh.logic.harvest2.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Implements lazy-loaded and memory-safe tree. When layer of nodes is
 * processed, it can be poped out and replaced with their children.
 * 
 * TODO doc
 * 
 * @author martin
 * 
 */
public abstract class BFSLazyTree implements Serializable, Iterable<String> {

	private static final long serialVersionUID = -5150024522240632714L;

	private final Queue<String> toProcess;
	private final Queue<String> toChildify;

	public BFSLazyTree(Collection<String> initials) {
		this.toProcess = new LinkedList<>(initials);
		this.toChildify = new LinkedList<>();
	}

	public boolean isHasSomeItems() {
		return (!toProcess.isEmpty()) || (!toChildify.isEmpty());
	}

	public int getItemsCount() {
		return toProcess.size() + toChildify.size();
	}

	public Iterable<String> getItems() {
		return toProcess;
	}

	public boolean isHasSomeItemsToProcess() {
		return !toProcess.isEmpty();
	}

	@Override
	public Iterator<String> iterator() {
		return new BFSLazyTreeIterator(this);
	}

	public abstract ChildrenGenerator getGenerator(); // TODO a nešlo by to přes
														// konstantu .. .třeba
														// klidně hold jakože
														// statickou?

	public String nextToProcess() {
		String next = toProcess.poll();
		toChildify.add(next);

		return next;
	}

	public void addToProcess(Set<String> toAdd) {
		toProcess.addAll(toAdd);
	}

	public void calculateNextChildren() {
		String next = toChildify.poll();
		Set<String> nexts = calculateChildren(next);
		toProcess.addAll(nexts);
	}

	private Set<String> calculateChildren(String label) {
		return getGenerator().generate(label);

	}

	public void add(Set<String> set) {
		toProcess.addAll(set);
	}

	public void remove(String word) {
		toProcess.remove(word);
		toChildify.remove(word);
	}

}