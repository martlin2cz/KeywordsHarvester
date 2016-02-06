package cz.martlin.kh.logic.harvest;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Represents data of harvesting process. To create instance use static methods
 * {@link #createNew(Config, Set)} and {@link #loadFromDumpFile(Config)}.
 * 
 * @author martin
 * 
 */
public class HarvestProcessData implements Serializable {

	private static final long serialVersionUID = -2624293193757930412L;

	private final Queue<String> toSubkeyword;
	private final Queue<String> toPicwfKeyws;
	private final Queue<String> toPicwfSubkeyws;
	private final Queue<String> toExport;
	private final Queue<String> exported;

	private final Map<String, Keyword> metadatas;

	private HarvestProcessData(Config config, Set<String> initialKeywords) {
		// this.config = config;

		this.toSubkeyword = new LinkedList<>(initialKeywords);
		this.toPicwfKeyws = new LinkedList<>();
		this.toPicwfSubkeyws = new LinkedList<>(initialKeywords);
		this.toExport = new LinkedList<>();
		this.exported = new LinkedList<>();

		this.metadatas = new HashMap<>(initialKeywords.size());
		// no needed to be ordered
	}

	private HarvestProcessData(Config config) {
		this(config, new LinkedHashSet<String>());
	}

	/**
	 * Removes given keyword from all queues.
	 * 
	 * @param keyword
	 */
	public synchronized void removeFromData(String keyword) {
		toSubkeyword.remove(keyword);
		toPicwfKeyws.remove(keyword);
		toPicwfSubkeyws.remove(keyword);
		toExport.remove(keyword);
		exported.remove(keyword);
	}

	/**
	 * Adds given keyword to process queue. Does not check occurence in done.
	 * 
	 * @param keyword
	 */
	public synchronized void addToData(String keyword) {
		toSubkeyword.add(keyword);
		toPicwfSubkeyws.add(keyword);
	}

	/**
	 * Adds all given keywords into queues (if not done yet).
	 * 
	 * @param keywords
	 */
	public synchronized void addToData(Set<String> keywords) {
		Set<String> filtered = removeInProcess(keywords);
		for (String keyword : filtered) {
			addToData(keyword);
		}
	}

	// ///////////////////////////////////////////////////////////
	public synchronized boolean isSomeToSubkeyword() {
		return !toSubkeyword.isEmpty();
	}

	public synchronized int getTosubkeywordCount() {
		return toSubkeyword.size();
	}

	public Iterable<String> getToSubkeywording() {
		return toSubkeyword;
	}

	public synchronized String gimmeNextSubkeyword() {
		return toSubkeyword.poll();
	}

	public synchronized void subkeyworded(String keyword,
			Set<String> subkeywords) {

		Set<String> filtered = removeInProcess(subkeywords);
		filtered.remove(keyword); // in addition

		toSubkeyword.addAll(filtered);

		toPicwfKeyws.add(keyword);
		toPicwfSubkeyws.addAll(filtered);
	}

	// /////////////////////////////////////////////////////////////

	public synchronized boolean isSomeToPicworkflow() {
		return !(toPicwfKeyws.isEmpty() && toPicwfSubkeyws.isEmpty());
	}

	public synchronized int getToPicworkflowCount() {
		return (toPicwfKeyws.size() + toPicwfSubkeyws.size());
	}

	public synchronized Set<String> gimmeNextPicworkflow(int count) {
		return dequeueCount(toPicwfKeyws, toPicwfSubkeyws, count);
	}

	public synchronized void picworkflowed(Set<String> keywords,
			Set<Keyword> metadatas) {
		addMetadatas(metadatas);

		inOrderAddInto(metadatas, keywords, toExport);
	}

	// /////////////////////////////////////////////////////////////

	public synchronized boolean isSomeToExport() {
		return !toExport.isEmpty();
	}

	public synchronized int getToExportCount() {
		return toExport.size();
	}

	public synchronized int getExportedCount() {
		return exported.size();
	}

	public synchronized Set<Keyword> getExported() {
		return getMetadata(exported);
	}

	public synchronized Set<Keyword> gimmeNextExport(int count) {
		Set<String> keywords = dequeueCount(toExport, count);
		return getMetadata(keywords);
	}

	public synchronized void exported(Set<Keyword> metadata) {
		Set<String> keywords = getKeywords(metadata);
		exported.addAll(keywords);
	}

	// /////////////////////////////////////////////////////////////
	/**
	 * Copies from toAdd into returned set such keywords that aren't in process
	 * or haven̈́'t been done yet. It should take a while, so be circumspect.
	 * 
	 * @param destination
	 * @param toAdd
	 */
	private Set<String> removeInProcess(Set<String> toAdd) {
		Set<String> result = new LinkedHashSet<>(toAdd);

		result.removeAll(toSubkeyword);
		result.removeAll(toPicwfKeyws);
		result.removeAll(toPicwfSubkeyws);
		result.removeAll(toExport);
		result.removeAll(exported);

		return result;
	}

	/**
	 * Removes first count objects from given queue. If queue contains less
	 * keywords than count, returns set containing only count items.
	 * 
	 * @param queueu
	 * @param count
	 * @return
	 */
	public synchronized <T> Set<T> dequeueCount(Queue<T> queueu, int count) {
		Set<T> result = new LinkedHashSet<>(count);

		for (int i = 0; i < count && !queueu.isEmpty(); i++) {
			T keyword = queueu.poll();
			result.add(keyword);
		}

		return result;
	}

	/**
	 * Dequeues first count items from given concatenation of queues (in given
	 * order).
	 * 
	 * @param firstQueue
	 * @param secondQueue
	 * @param count
	 * @return
	 */
	private <T> Set<T> dequeueCount(Queue<T> firstQueue, Queue<T> secondQueue,
			int count) {

		Set<T> result = dequeueCount(firstQueue, count);

		if (result.size() < count) {
			int subcount = count - result.size();
			Set<T> subresult = dequeueCount(secondQueue, subcount);
			result.addAll(subresult);
		}

		return result;
	}

	// /////////////////////////////////////////////////////////////

	/**
	 * For given set of keywords returns corresponding set of theirs metadatas.
	 * 
	 * @param keywords
	 * @return
	 */
	private Set<Keyword> getMetadata(Collection<String> keywords) {
		Set<Keyword> result = new LinkedHashSet<>(keywords.size());

		for (String keyword : keywords) {
			Keyword metadata = this.metadatas.get(keyword);
			if (metadata == null) {
				throw new IllegalStateException("Keyword " + keyword
						+ " has no metadata");
			}
			result.add(metadata);
		}

		return result;
	}

	/**
	 * For given set of metadatas returns set of theirs keywords.
	 * 
	 * @param metadatas
	 * @return
	 */
	private Set<String> getKeywords(Collection<Keyword> metadatas) {
		Set<String> result = new LinkedHashSet<>(metadatas.size());

		for (Keyword metadata : metadatas) {
			String keyword = metadata.getKeyword();
			result.add(keyword);
		}

		return result;
	}

	/**
	 * Adds given metadatas into #metadatas collection.
	 * 
	 * @param metadatas
	 */
	private void addMetadatas(Collection<Keyword> metadatas) {
		for (Keyword keyword : metadatas) {
			this.metadatas.put(keyword.getKeyword(), keyword);
		}
	}

	/**
	 * Huh, given metadatas converts back to keywords and in order by order adds
	 * them into destination. Assumes that metadatas is subset of order. So,
	 * keywords in order, but not in metadatas will be ignored and not added
	 * into destination.
	 * 
	 * @param metadatas
	 * @param order
	 * @param destination
	 */
	private void inOrderAddInto(Set<Keyword> metadatas, Set<String> order,
			Queue<String> destination) {

		Set<String> keywords = getKeywords(metadatas);
		for (String orderItem : order) {
			if (keywords.contains(orderItem)) {
				destination.add(orderItem);
			}
		}
	}

	// /////////////////////////////////////////////////////////////

	/**
	 * Saves this instance into file given by config.
	 * 
	 * @param config
	 */
	public synchronized void saveToDumpFile(Config config) {
		HarvestDataDumperInporter dump = new HarvestDataDumperInporter(config);
		dump.save(this);
	}

	// /////////////////////////////////////////////////////////////

	/**
	 * Creates new instace with given config and set of initial keywords.
	 * 
	 * @param config
	 * @param initialKeywords
	 * @return
	 */
	public static HarvestProcessData createNew(Config config,
			Set<String> initialKeywords) {
		return new HarvestProcessData(config, initialKeywords);
	}

	/**
	 * Creates new instance with given config. The data are loaded from file
	 * (see {@link Config#getQueuesDumpFile()}).
	 * 
	 * @param config
	 * @return
	 */
	public static HarvestProcessData loadFromDumpFile(Config config) {
		HarvestDataDumperInporter dump = new HarvestDataDumperInporter(config);
		HarvestProcessData data = dump.load();

		return data;
	}

	/**
	 * Creates new instance by import from file and using given keywords
	 * separator regex (i.e. "\n" or "\\, *").
	 * 
	 * @param config
	 * @param file
	 * @param separator
	 * @return
	 */
	public static HarvestProcessData importThem(Config config, File file,
			String separator) {
		HarvestDataDumperInporter dump = new HarvestDataDumperInporter(config);
		HarvestProcessData data = new HarvestProcessData(config);

		boolean succeed = dump.importFromTextFile(file, separator, data);
		if (!succeed) {
			return null;
		} else {
			return data;
		}

	}

	@Override
	public String toString() {
		return "HarvestProcessData [getTosubkeywordCount()="
				+ getTosubkeywordCount() + ", getToPicworkflowCount()="
				+ getToPicworkflowCount() + ", getToExportCount()="
				+ getToExportCount() + ", getExportedCount()="
				+ getExportedCount() + ", toSubkeyword=" + toSubkeyword
				+ ", toPicwfKeyws=" + toPicwfKeyws + ", toPicwfSubkeyws="
				+ toPicwfSubkeyws + ", toExport=" + toExport + ", exported="
				+ exported + ", metadatas=" + metadatas + "]";
	}

}