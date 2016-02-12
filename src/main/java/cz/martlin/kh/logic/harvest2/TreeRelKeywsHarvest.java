package cz.martlin.kh.logic.harvest2;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.StuffProvider;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;
import cz.martlin.kh.logic.export.Exporter;
import cz.martlin.kh.logic.picwf.PicwfQueryResult;
import cz.martlin.kh.logic.picwf.Picworkflower;
import cz.martlin.kh.logic.utils.Interruptable;

/**
 * Does the main - harvesting of keywords.
 * 
 * @author martin
 * 
 */
public class TreeRelKeywsHarvest implements Interruptable {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	private final HarvestingListener listener;

	private final Picworkflower picworkflow;
	private final Exporter exporter;

	private boolean interrupted;
	private TreeHarvestProcessData currentData;

	/**
	 * Creates harvester
	 * 
	 * @param config
	 * @param listener
	 *            can be null (see {@link #TreeRelKeywsHarvest(Config)})
	 */
	public TreeRelKeywsHarvest(Config config, HarvestingListener listener) {
		super();

		this.config = config;
		this.listener = listener;
		this.picworkflow = StuffProvider.getPicworkflower(config);
		this.exporter = StuffProvider.getExporter(config);
	}

	public TreeRelKeywsHarvest(Config config) {
		this(config, null);
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
	}

	/**
	 * Gets data currently in process.
	 * 
	 * @return
	 */
	public TreeHarvestProcessData getCurrentData() {
		return currentData;
	}

	/**
	 * Runs harvesting. On start sets {@link #currentData} to given data and
	 * starts.
	 * 
	 * @param data
	 * @return
	 */
	public boolean run(TreeHarvestProcessData data) {
		boolean success = initialize(data);
		if (!success) {
			return false;
		} else {
			currentData = data;
		}

		while (data.isHasSomeToProcess()) {

			if (!data.isSomeToProcess()) {
				createNextToProcess(data);
			}
			if (interrupted) {
				break;
			}

			if (data.isSomeToProcess()) {
				process(data);
			}
			if (interrupted) {
				break;
			}
		}

		finish(data);
		currentData = null;

		return true;
	}

	/**
	 * Initializes harvester.
	 * 
	 * @param data
	 * @return
	 */
	private boolean initialize(TreeHarvestProcessData data) {
		tryToLog("Initializing");
		try {
			this.exporter.initializeExporter(data);
		} catch (IOException e) {
			log.error("Could not initialize exporter", e);
			return false;
		}

		log.info(
				"Harvesting started, now done {} keywords and {} is waiting on level {}.",
				data.getDoneCount(), data.getTreeRootsCount(),
				data.getTreeLevel());
		tryToLog("Ready");

		return true;

	}

/**
	 * Creates next set of keywords to process. This is set to data by {@link TreeHarvestProcessData#setToProcess(Set)).
	 * @param data
	 */
	private void createNextToProcess(TreeHarvestProcessData data) {
		int minSize = config.getHWMinProcessBatchSize();
		log.info("Preparing next set to process with minimal size {}", minSize);
		tryToLog("Loading at least ", minSize, " related keywords...");

		Set<String> keywords = new LinkedHashSet<>(minSize);

		while (!interrupted && keywords.size() < minSize
				&& data.isHasSomeToProcess()) {

			String keyword = data.getNextToProcess();

			if (keyword == null) {
				continue;
			}

			Set<String> subkws = data.getSubkeywordsOf(keyword);
			Set<String> filtered = data.filterDone(subkws);
			keywords.addAll(filtered);
			tryToLog("Loading related keywords... now loaded ", keywords.size());
		}

		data.setToProcess(keywords);

		log.info("Prepared next set to process with size {}", keywords.size());
		tryToLog("Related keywords ready to process");

	}

	/**
	 * Processess given data (in fact,
	 * {@link TreeHarvestProcessData#getToProcess()}.
	 * 
	 * @param data
	 */
	private void process(TreeHarvestProcessData data) {
		Set<String> keywords = data.getToProcess();

		data.setToPicworkflow(keywords);

		if (data.isSomeToPicworkflow() && !interrupted) {
			picworkflow(data);
		}

		if (data.isSomeToExport() && !interrupted) {
			export(data);
		}

		if (data.isSomeToDone() && !interrupted) {
			done(data);
		}

		if (data.isSomeToProcess() && !interrupted) {
			data.unsetToProcess();
		}
	}

	/**
	 * Does picworkflowing of {@link TreeHarvestProcessData#getToPicworkflow()}.
	 * When successfuly completed, unsets them from set and sets to export.
	 * 
	 * @param data
	 */
	private void picworkflow(TreeHarvestProcessData data) {
		Set<String> keywords = data.getToPicworkflow();

		log.info("Picworkflowing of {} keywords", keywords.size());
		tryToLog("Invoking picworkflow with ", keywords.size() + " keywords...");

		PicwfQueryResult result = picworkflow.run(keywords);
		if (interrupted) {
			return;
		}

		data.unsetToPicworkflow();

		if (result != null) {
			data.setToExport(result.getMetadatas());

			log.info(
					"Picworkflowing of {} keywords done with {} success ({} successful, {} failed, {} really)",
					result.getRequestedCount(), result.getSuccessRatio(),
					result.getDoneCount(), result.getNotdoneCount());
			tryToLog("Picworkflow completed with ", result.getDoneCount()
					+ " keywords");
		} else {
			log.error(
					"Picflowork of {} keywords failed completelly (with no result).",
					keywords.size());
		}
	}

	/**
	 * Does exporting of {@link TreeHarvestProcessData#getToExport()}. When
	 * successfuly completed, unsets them from set and sets to done.
	 * 
	 * @param data
	 */
	private void export(TreeHarvestProcessData data) {
		tryToLog("Exporting...");

		Set<Keyword> toExport = data.getToExport();
		Set<Keyword> exported = exporter.export(toExport, data);

		if (interrupted) {
			return;
		}

		data.unsetToExport();
		data.setToDone(exported);

		if (exported != null) {
			log.info("Exported {} keywords", exported.size());
			tryToLog("Export done");
		} else {
			log.error("Exporting of keywords failed");
		}
	}

	/**
	 * Does finish of iteration of {@link TreeHarvestProcessData#getToDone()}.
	 * When successfuly completed, unsets them from set and adds to done (
	 * {@link TreeHarvestProcessData#addDone(Set)}).
	 * 
	 * @param data
	 */
	private void done(TreeHarvestProcessData data) {
		tryToLog("Finishing iteration...");
		Set<Keyword> done = data.getToDone();

		data.unsetToDone();
		data.addDone(done);

		data.saveToDumpFile(config);

		log.info(
				"Completed one iteration with {} keywords. Now completelly done {}.",
				done.size(), data.getDoneCount());
		tryToLog("Next ", data.getDoneCount(), " keywords successfully done");
	}

	/**
	 * Finishes process.
	 * @param data
	 */
	private void finish(TreeHarvestProcessData data) {
		log.info("Harvesting finished, completelly done {} keywords.",
				data.getDoneCount());
		tryToLog("Finished");

	}

	/**
	 * If {@link #listener} is not null, from given args creates message and sends to listener.
	 * @param args
	 */
	protected void tryToLog(Object... args) {
		if (listener != null) {
			StringBuilder msg = new StringBuilder();

			for (Object arg : args) {
				msg.append(arg);
			}
			
			listener.occured(msg.toString());
		}
	}

}
