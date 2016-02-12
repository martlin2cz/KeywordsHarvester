package cz.martlin.kh.logic.harvest2;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;
import cz.martlin.kh.logic.harvest2.tree.LazyTree;
import cz.martlin.kh.logic.harvest2.tree.LazyTreeIterator;

/**
 * Data object representing harvesting process.
 * 
 * @author martin
 * 
 */
public class TreeHarvestProcessData implements Serializable {
	private static final long serialVersionUID = 1793788020744701466L;

	private final LazyTree tree;
	private LazyTreeIterator treeIterator;

	private Set<String> toProcess;
	private Set<String> toPicworkflow;
	private Set<String> toExport;
	private Set<String> toDone;

	private final Map<String, Keyword> metadatas;
	private final Set<String> done;

	/**
	 * Use rather factory methods {@link #createNew(Config, Set)},
	 * {@link #loadFromDumpFile(Config)},
	 * {@link #importThem(Config, File, String)}.
	 * 
	 * @param config
	 * @param initialKeywords
	 * @param subkeywServices
	 * @param config
	 * @param intialKeywords
	 */
	private TreeHarvestProcessData(Config config, Set<String> initialKeywords) {

		this.tree = new RelatedKeywordsTree(config, initialKeywords);
		this.treeIterator = (LazyTreeIterator) tree.iterator();

		this.metadatas = new HashMap<>(initialKeywords.size());
		this.done = new LinkedHashSet<>();
	}

	/**
	 * Use rather factory methods {@link #createNew(Config, Set)},
	 * {@link #loadFromDumpFile(Config)},
	 * {@link #importThem(Config, File, String)}.
	 * 
	 * @param config
	 */
	private TreeHarvestProcessData(Config config) {
		this(config, new LinkedHashSet<String>());
	}

	/**
	 * Has some data to process (should obviously allways return true, but you
	 * know ...)
	 * 
	 * @return
	 */
	public boolean isHasSomeToProcess() {
		return treeIterator.hasNext();
	}

	/**
	 * Gets and returns next keyword waiting to be processed.
	 * 
	 * @return
	 */
	public String getNextToProcess() {
		return treeIterator.next();
	}

	/**
	 * Gets (loads) related keyword of given keyword.
	 * 
	 * @param keyword
	 * @return
	 */
	public Set<String> getSubkeywordsOf(String keyword) {
		return tree.getChildren(keyword);
	}

	/**
	 * Returns count of keywords on tree level.
	 * 
	 * @return
	 */
	public int getTreeRootsCount() {
		return tree.getRootsCount();
	}

	/**
	 * Returns level of tree.
	 * 
	 * @return
	 */
	public int getTreeLevel() {
		return tree.getLevel();
	}

	// /////////////////////////////////////////////////////////////////////////
	/**
	 * Are some data to, or in, process?
	 * 
	 * @return
	 */
	public boolean isSomeToProcess() {
		return toProcess != null && !toProcess.isEmpty();
	}

	/**
	 * Gets data to, or in, process.
	 * 
	 * @return
	 */
	public Set<String> getToProcess() {
		return toProcess;
	}

	/**
	 * Sets data to process.
	 * 
	 * @param toProcess
	 */
	public void setToProcess(Set<String> toProcess) {
		this.toProcess = toProcess;
	}

	/**
	 * Sets there is no data to, or in, process.
	 */
	public void unsetToProcess() {
		this.toProcess = null;
	}

	// //////////////////////

	/**
	 * Is some data to, or currently beeing in, picworkflow?
	 * 
	 * @return
	 */
	public boolean isSomeToPicworkflow() {
		return toPicworkflow != null && !toPicworkflow.isEmpty();
	}

	/**
	 * Returns to, or currently beeing in, picworkflow.
	 * 
	 * @return
	 */
	public Set<String> getToPicworkflow() {
		return toPicworkflow;
	}

	/**
	 * Sets data to, or currently beeing in, picworkflow.
	 * 
	 * @param toPicworkflow
	 */
	public void setToPicworkflow(Set<String> toPicworkflow) {
		this.toPicworkflow = toPicworkflow;
	}

	/**
	 * Set there is no data to, or currently beeing in, picworkflow.
	 */
	public void unsetToPicworkflow() {
		this.toPicworkflow = null;
	}

	// ////////////////////

	/**
	 * Is some data to, or currently beeing in, export?
	 * @return
	 */
	public boolean isSomeToExport() {
		return toExport != null && !toExport.isEmpty();
	}

	/**
	 * Returns data to, or currently beeing in, export.
	 * @return
	 */
	public Set<Keyword> getToExport() {
		return getMetadata(toExport);
	}

	/**
	 * Sets data to, or currently beeing in, export.
	 * @param toExport
	 */
	public void setToExport(Set<Keyword> toExport) {
		this.toExport = getKeywords(toExport);
		justSimplyFokinAdd(toExport);
	}

	/**
	 * Sets no data to, or currently beeing in, export.
	 */
	public void unsetToExport() {
		this.toExport = null;
	}

	// ////////////////////

	/**
	 * Is some data to, or currently beeing in, complete process?
	 * @return
	 */
	public boolean isSomeToDone() {
		return toDone != null && !toDone.isEmpty();
	}

	/**
	 * Returns data to, or currently beeing in, completing of process.
	 * @return
	 */
	public Set<Keyword> getToDone() {
		return getMetadata(toDone);
	}

	/**
	 * Sets data to, or currently beeing in, completing of process.
	 * @param toDone
	 */
	public void setToDone(Set<Keyword> toDone) {
		this.toDone = getKeywords(toDone);
	}

	/**
	 * Sets to no data to, or currently beeing in, completing of process.
	 */
	public void unsetToDone() {
		this.toDone = null;
	}

	/////////////////////////////////////
	
	/**
	 * Adds given keywords into done set.
	 * @param done
	 */
	public void addDone(Set<Keyword> done) {
		Set<String> doneMetadata = getKeywords(done);
		this.done.addAll(doneMetadata);
	}

	/**
	 * Returns count of completelly done keywords.
	 * @return
	 */
	public int getDoneCount() {
		return done.size();
	}

	/**
	 * From given set removes keywords which have been currently done yet.
	 * 
	 * @param keywords
	 * @return
	 */
	public Set<String> filterDone(Set<String> keywords) {
		Set<String> result = new LinkedHashSet<>(keywords.size());

		for (String keyword : keywords) {
			if (!done.contains(keyword)) {
				result.add(keyword);
			}
		}

		return result;
	}

	/**
	 * Removes given keyword from all process.
	 * 
	 * @param keyword
	 */
	public synchronized void removeFromData(String keyword) {
		tree.remove(keyword);
		treeIterator.reset();
	}

	/**
	 * Adds given keyword to process queue. Does not check occurence in done.
	 * 
	 * @param keyword
	 */
	public synchronized void addToData(String keyword) {
		Set<String> set = new HashSet<>();
		set.add(keyword);
		tree.add(set);
		treeIterator.reset();
	}

	/**
	 * Adds all given keywords into queues (if not done yet).
	 * 
	 * @param keywords
	 */
	public synchronized void addToData(Set<String> keywords) {
		tree.add(keywords);
	}

	// ///////////////////////////////////////////////////////////

	/**
	 * Returns keywords currently waiting (are roots of tree).
	 * @return
	 */
	public synchronized Set<String> getWaitingProcess() {
		return tree.getRoots();
	}

	/**
	 * Returns done keywords.
	 * @return
	 */
	public synchronized Set<Keyword> getDone() {
		return getMetadata(done);
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
	 * Yeah, just simply fokin adds given keywords into metadata set.
	 * 
	 * @param keywords
	 */
	private void justSimplyFokinAdd(Set<Keyword> keywords) {

		for (Keyword keyword : keywords) {
			metadatas.put(keyword.getKeyword(), keyword);
		}
	}

	// /////////////////////////////////////////////////////////////

	/**
	 * Saves this instance into file given by config.
	 * 
	 * @param config
	 */
	public synchronized void saveToDumpFile(Config config) {
		TreeHarvestDataDumperInporter dump = new TreeHarvestDataDumperInporter(
				config);
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
	public static TreeHarvestProcessData createNew(Config config,
			Set<String> initialKeywords) {
		return new TreeHarvestProcessData(config, initialKeywords);
	}

	/**
	 * Creates new instance with given config. The data are loaded from file
	 * (see {@link Config#getQueuesDumpFile()}).
	 * 
	 * @param config
	 * @return
	 */
	public static TreeHarvestProcessData loadFromDumpFile(Config config) {
		TreeHarvestDataDumperInporter dump = new TreeHarvestDataDumperInporter(
				config);
		TreeHarvestProcessData data = dump.load();

		return data;
	}

	/**
	 * Creates new instance by import from file and using given keywords
	 * separator regex (i.e. "\n" or "\\, *").
	 * 
	 * @param config
	 * @param file
	 * @param separator
	 * @return data or null when failed
	 */
	public static TreeHarvestProcessData importThem(Config config, File file,
			String separator) {
		TreeHarvestDataDumperInporter dump = new TreeHarvestDataDumperInporter(
				config);
		TreeHarvestProcessData data = new TreeHarvestProcessData(config);

		boolean succeed = dump.importFromTextFile(file, separator, data);
		if (!succeed) {
			return null;
		} else {
			return data;
		}
	}

}