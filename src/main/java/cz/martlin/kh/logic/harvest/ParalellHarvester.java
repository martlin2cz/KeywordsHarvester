package cz.martlin.kh.logic.harvest;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.export.AbstractExporter;
import cz.martlin.kh.logic.subkeyw.AbstractServiceWrapper;

/**
 * Makes {@link RelatedKeywordsHarvester} paraller. Paraller processing is
 * stared by method {@link #startNew(Set)} and aborted by method {@link #stop()}
 * .
 * 
 * @author martin
 * 
 */
public class ParalellHarvester {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final RelatedKeywordsHarvester harvester;

	private final SubkeywordThread subkeyworder;
	private final PicworkflowThread picworkflower;
	private final ExportThread exprorter;

	/**
	 * Creates paraller harvester with given parameters.
	 * 
	 * @param config
	 * @param services
	 * @param exporters
	 */
	public ParalellHarvester(Config config,
			Set<AbstractServiceWrapper> services,
			Set<AbstractExporter> exporters) {
		
		harvester = new RelatedKeywordsHarvester(config, services, exporters);

		subkeyworder = new SubkeywordThread(harvester);
		picworkflower = new PicworkflowThread(harvester);
		exprorter = new ExportThread(harvester);
	}

	/**
	 * Initializes all required and starts service threads. Returns true if
	 * succeeds.
	 * 
	 * @param initialKeywords
	 * @return
	 */
	public boolean start(HarvestProcessData data) {
		log.debug("Paraller harvester is starting with data: {}.", data);

		boolean inited = harvester.initialize(data);
		if (!inited) {
			return false;
		}

		startThreads();

		log.debug("Paraller harvester running.");
		return true;
	}

	/**
	 * Starts service threads.
	 */
	private void startThreads() {
		subkeyworder.start();
		picworkflower.start();
		exprorter.start();
	}

	/**
	 * Stops all service threads, awaits them and finishes harvester work.
	 */
	public void stop() {
		log.debug("Paraller harvester is stopping.");

		harvester.interrupt();

		try {
			subkeyworder.join();
		} catch (Exception e) {
		}
		try {
			picworkflower.join();
		} catch (Exception e) {
		}
		try {
			exprorter.join();
		} catch (Exception e) {
		}

		try {
			harvester.finish();
		} catch (NetworkException | IOException e) {
		}

		log.debug("Paraller harvester stopped.");
	}

	/**
	 * Exporting thread.
	 * 
	 * @author martin
	 * 
	 */
	public class ExportThread extends Thread {

		private final RelatedKeywordsHarvester harvester;

		public ExportThread(RelatedKeywordsHarvester harvester) {
			super("ExportT");
			this.harvester = harvester;
		}

		@Override
		public void run() {
			harvester.doExporting();
		}

	}

	/**
	 * Pickworkflow thread.
	 * 
	 * @author martin
	 * 
	 */
	public class PicworkflowThread extends Thread {

		private final RelatedKeywordsHarvester harvester;

		public PicworkflowThread(RelatedKeywordsHarvester harvester) {
			super("PickworkflowT");
			this.harvester = harvester;
		}

		@Override
		public void run() {
			harvester.doPicworkflowing();
		}

	}

	/**
	 * Subkeywording thread.
	 * 
	 * @author martin
	 * 
	 */
	public class SubkeywordThread extends Thread {

		private final RelatedKeywordsHarvester harvester;

		public SubkeywordThread(RelatedKeywordsHarvester harvester) {
			super("SubkeywordT");
			this.harvester = harvester;
		}

		@Override
		public void run() {
			harvester.doSubkeywording();
		}

	}
}
