package cz.martlin.kh.logic.picwf;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.utils.Interruptable;

/**
 * Wraps picworkflow functionality.
 * 
 * @author martin
 * 
 */
public class Picworkflower implements Interruptable {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	private final PicworkflowWrapper picworkflow;
	private final UnpicworkflowableExporter unpicwfExporter;

	private PicworkflowQuery currentQuery;

	public Picworkflower(Config config) {
		this.config = config;
		this.picworkflow = new PicworkflowWrapper(config);
		this.unpicwfExporter = new UnpicworkflowableExporter(config);
	}

	public PicworkflowQuery getCurrentQuery() {
		return currentQuery;
	}

	@Override
	public synchronized void interrupt() {
		if (currentQuery != null) {
			currentQuery.interrupt();
		}
	}

	/**
	 * Completelly runs query. It is not required no other initialization or
	 * some other shit.
	 * 
	 * @param keywords
	 * @return result or null (and logs more info) if some error occurs
	 */
	public PicwfQueryResult run(Set<String> keywords) {
		try {
			picworkflow.initialize();
		} catch (NetworkException e) {
			log.error("Could not initialize picworkflower", e);
			return null;
		}

		PicwfQueryResult result = null;
		try {
			currentQuery = picworkflow.createQuerry(keywords);
			result = currentQuery.runQuery();
			currentQuery = null;
		} catch (NetworkException e) {
			log.error("Picworkflow currentQuery execution failed", e);
			result = null;
		}

		tryToWaitOnFailed(result);
		saveNotdones(result);

		try {
			picworkflow.finish();
		} catch (NetworkException e) {
			log.warn("Could not finish picworkflower", e);
		}

		return result;
	}

	/**
	 * If is result not null exports all not-really-done keywords.
	 * 
	 * @param result
	 */
	private void saveNotdones(PicwfQueryResult result) {
		if (result != null) {
			Set<String> notdone = result.getReallyNotdone();
			if (!notdone.isEmpty()) {

				boolean success = unpicwfExporter.simplySave(notdone);
				if (!success) {
					log.warn("Saving of {} unpicworkflowable keywords failed.",
							notdone.size());
				}
			}
		}
	}

	/**
	 * If no keyword is successfully done (or query totally failed), waits time
	 * given by config (config#getPwFailedWait).
	 * 
	 * @param done
	 */
	private void tryToWaitOnFailed(PicwfQueryResult result) {

		if (result == null || result.isNoSuccessfulyDone()) {

			int wait = config.getPwFailedWait();
			log.warn(
					"No successful keywords done, waiting {} ms and hoping it will help",
					wait);

			try {
				Thread.sleep(wait);
			} catch (InterruptedException eIgnore) {
			}
		}
	}
}
