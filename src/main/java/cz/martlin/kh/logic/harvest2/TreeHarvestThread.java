package cz.martlin.kh.logic.harvest2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.utils.Interruptable;

/**
 * Thread encapsulating {@link TreeRelKeywsHarvest} instance run.
 * 
 * @author martin
 * 
 */
public class TreeHarvestThread extends Thread implements Interruptable {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final TreeRelKeywsHarvest harvest;
	private final TreeHarvestProcessData data;

	public TreeHarvestThread(TreeRelKeywsHarvest harvest,
			TreeHarvestProcessData data) {
		super("TreeHarvestT");

		this.harvest = harvest;
		this.data = data;
	}

	@Override
	public void interrupt() {
		log.info("Thread interrupt invoked");
		harvest.tryToLog("Stopping...");

		harvest.interrupt();

		super.interrupt();
		try {
			this.join();
		} catch (InterruptedException e) {
		}

		log.info("Thread interrupt completed");
		harvest.tryToLog("Stopped");
	}

	@Override
	public void run() {
		log.info("Harvester in thread started");
		harvest.tryToLog("Starting...");

		harvest.run(data);

		log.info("Harvester in thread finished");
		harvest.tryToLog("Finished.");
	}
}
