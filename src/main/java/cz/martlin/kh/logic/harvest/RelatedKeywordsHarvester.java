package cz.martlin.kh.logic.harvest;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.export.AbstractExporter;
import cz.martlin.kh.logic.export.AppendExporter;
import cz.martlin.kh.logic.export.RewriteExporter;
import cz.martlin.kh.logic.harvest2.TreeRelKeywsHarvest;
import cz.martlin.kh.logic.picwf.PicworkflowQuery;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;
import cz.martlin.kh.logic.utils.Interruptable;

/**
 * Performs process of related keywords harvesting. After initialization (
 * {@link #initialize(HarvestProcessData)}) methods {@link #doSubkeywording()},
 * {@link #doPicworkflowing()} and {@link #doExporting()} does as expected -
 * find related keywords, load metadata and export them. The implementation is
 * prepared to run theese methods in separate threads. (they communicate via
 * HarvestProcessData instance) and can be interrupted ({@link #interrupt()}).
 * 
 * @deprecated Too complicated && seems buggy while used as
 *             {@link ParalellHarvester}. Use {@link TreeRelKeywsHarvest}.
 * @see TreeRelKeywsHarvest
 * @author martin
 * 
 */
@Deprecated
public class RelatedKeywordsHarvester implements Interruptable {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	private final Set<AbstractServiceWrapper> services;
	private final PicworkflowWrapper picwork;
	private final Set<AbstractExporter> exporters;

	private HarvestProcessData data;
	private AbstractExporter export;
	private boolean interrupted;

	private PicworkflowQuery currentQuery;

	/**
	 * Creates harvester.
	 * 
	 * @param config
	 * @param services
	 * @param exporters
	 */
	public RelatedKeywordsHarvester(Config config,
			Set<AbstractServiceWrapper> services,
			Set<AbstractExporter> exporters) {

		super();
		this.config = config;
		this.services = services;
		this.picwork = new PicworkflowWrapper(config);
		this.exporters = exporters;

	}

	@Override
	public void interrupt() {
		interrupted = true;

		for (AbstractServiceWrapper service : services) {
			service.interrupt();
		}

		// avoids race condition and NullPointerException
		PicworkflowQuery querry = currentQuery;
		if (querry != null) {
			querry.interrupt();
		}

	}

	/**
	 * Prepares to run with given harvest data.
	 * 
	 * @param data
	 * @return
	 */
	public boolean initialize(HarvestProcessData data) {

		try {
			this.data = data;

			this.picwork.initialize();
			this.export = initializeExporter();

			this.interrupted = false;

			return true;
		} catch (Exception e) {
			log.error(
					"An fatal error occured during harvester initialization.",
					e);
			return false;
		}
	}

	private AbstractExporter initializeExporter() throws IOException {
		this.export = AbstractExporter.getBySuffix(exporters,
				config.getExportFile());

		if (export == null) {
			log.error("Unkown exporter for export file {}",
					config.getExportFile());
			return null;
		}

		export.initializeExporter();

		if (export instanceof AppendExporter) {
			AppendExporter ae = (AppendExporter) export;
			ae.exportInitial(data.getExported());
		}

		return export;
	}

	/**
	 * Finishes work.
	 * 
	 * @throws NetworkException
	 * @throws IOException
	 */
	public void finish() throws NetworkException, IOException {
		picwork.finish();
		export.finishExporter();

		HarvestDataDumperInporter dump = new HarvestDataDumperInporter(config);
		dump.save(data);

		data = null;
	}

	/**
	 * Runs subkeywoding. If there is too much keywords done (by {@link #config}
	 * and ready to picworkflow, waits.
	 */
	public void doSubkeywording() {
		while (!interrupted) {

			try {
				// this algorithm causes subkeywording buffer to grow
				// uncontrollable ...

				while (data.getToPicworkflowCount() > config
						.getHWToPicworkflowQueueSize() && !interrupted) {
					Thread.sleep(config.getWaitStep());
				}

				loadSubkeywords();

				waitOrBeInterrupted(config.getWaitBetweenSubkeywordQueries());

				while (data.getToPicworkflowCount() > config
						.getHWToPicworkflowQueueSize() && !interrupted) {
					Thread.sleep(config.getWaitStep());
				}
			} catch (InterruptedException e) {
				log.error("Subkeywording has been interrupted", e);
			}
		}
	}

	/**
	 * Runs pickworkflow querying. Awaits data from subkeywording, proceeds and
	 * sends to export.
	 */
	public void doPicworkflowing() {
		while (!interrupted) {
			try {
				while (data.getToPicworkflowCount() < config
						.getHWToPicworkflowQueueSize() && !interrupted) {
					Thread.sleep(config.getWaitStep());
				}

				loadMetadata();

				waitOrBeInterrupted(config.getWaitBetweenPicflowQueries());

				while (data.getToExportCount() > config
						.getHWToExportQueueSize() && !interrupted) {
					Thread.sleep(config.getWaitStep());
				}
			} catch (InterruptedException e) {
				log.error("Picworflowing has been interrupted", e);
			}
		}
	}

	/**
	 * Exports data from picworkflow.
	 */
	public void doExporting() {
		while (!interrupted) {
			try {
				while (data.getToExportCount() < config
						.getHWToExportQueueSize() && !interrupted) {
					Thread.sleep(config.getWaitStep());
				}

				export();

				waitOrBeInterrupted(config.getWaitBetweenExports());

			} catch (InterruptedException e) {
				log.error("Exporting has been interrupted", e);
			}
		}
	}

	/**
	 * Dequeue one keyword from subkeywording queue, finds related subkeywords
	 * (via {@link #services}), and adds them into both queue to picworkflow and
	 * subkeyword.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	private boolean loadSubkeywords() throws InterruptedException {
		String keyword = data.gimmeNextSubkeyword();

		log.info("Subkeywording keyword {}", keyword);
		int count = config.getSamplesCount();
		Set<String> allSubkeywords = new LinkedHashSet<>(
				data.getTosubkeywordCount());

		for (AbstractServiceWrapper service : services) {
			Set<String> subKeywords = service
					.getRelatedKeywords(keyword, count);

			if (interrupted) {
				break;
			}
			if (subKeywords == null) {
				continue;
			}

			allSubkeywords.addAll(subKeywords);

			waitOrBeInterrupted(config.getSKWaitBetweenServices());
		}

		data.subkeyworded(keyword, allSubkeywords);

		log.info("Subkeywording of keyword {} gave: {}", keyword,
				allSubkeywords);

		return true;
	}

	/**
	 * Dequeues first n item from picworkflow queue, submits to picworkflow and
	 * returned metadata set puts into export queue.
	 */
	private void loadMetadata() {
		Set<String> keywords = data.gimmeNextPicworkflow(config
				.getHWToPicworkflowQueueSize());

		log.info("Picworkflowing of {} keywords: {}", keywords.size(), keywords);
		PicworkflowQuery query = picwork.createQuerry(keywords);

		try {
			currentQuery = query;
			query.runQuery();
			currentQuery = null;

			picwork.reconnect();
		} catch (NetworkException e) {
			log.warn("Picflowork of {} failed, will try later.", keywords);
		}

		Set<Keyword> done = null;//XXX query.getDone();
		Set<String> notdone = null; //XXX query.getNotDone();

		if (done != null && notdone != null) {
			data.picworkflowed(keywords, done);
		}

		log.info("Picworkflowing of {} keywords gave: {}, failed: {}",
				keywords, done, notdone);
	}

	/**
	 * Removes first n keywords from export queue and exports them into file.
	 * 
	 * @throws IOException
	 */
	private void export() {
		try {
			if (export instanceof AppendExporter) {
				exportBatch((AppendExporter) export);
			} else {
				exportAll((RewriteExporter) export);
			}
		} catch (IOException e) {
			log.error("An error occured during exporting", e);
			return;
		}

	}

	/**
	 * Exports batch of keywords.
	 * 
	 * @param export2
	 * 
	 * @throws IOException
	 */
	private void exportBatch(AppendExporter appendExporter) throws IOException {
		Set<Keyword> batch = data
				.gimmeNextExport(config.getExExportBatchSize());

		if (batch != null) {
			log.info("Exporting batch of {} keywords: {}", batch.size(), batch);
			appendExporter.export(batch);

			data.exported(batch);

			log.info(
					"Exporting of {} keywords done, now is completelly done {} keywords",
					batch.size(), data.getExportedCount());
		}
	}

	/**
	 * (Re)exports all keywords.
	 * 
	 * @param export2
	 * 
	 * @throws IOException
	 */
	private void exportAll(RewriteExporter rewriteExport) throws IOException {
		// System.out.println("To export total = " + data.getToExportCount() +
		// ", exported total = " + data.getExportedCount());

		Set<Keyword> newToExport = data.gimmeNextExport(config
				.getExExportBatchSize());
		// System.out.println("To export batch = " + newToExport.size()+
		// ", remains = " + data.getToExportCount());
		Set<Keyword> oldExported = data.getExported();

		int allSize = oldExported.size() + newToExport.size();
		Set<Keyword> all = new LinkedHashSet<>(allSize);
		all.addAll(oldExported);
		all.addAll(newToExport);

		// System.out.println("To export batch = " + newToExport.size() +
		// ", done = " + oldExported.size() + ", so all = " + all.size());

		log.info("Exporting all {} keywords: {}", all.size(), all);
		rewriteExport.export(all);

		data.exported(newToExport);

		// System.out.println("Now to export = " + data.getToExportCount() +
		// ", exported = " + data.getExportedCount());
		// System.out.println();
		log.info(
				"Exporting of all {} keywords done, now is completelly done {} keywords",
				all.size(), data.getExportedCount());

	}

	private void waitOrBeInterrupted(long wait) {
		try {
			Thread.sleep(wait);
		} catch (InterruptedException eIgnore) {
		}
	}
}
