package cz.martlin.kh.logic.picwf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keysolutions.ddpclient.DDPClient;

import cz.martlin.kh.logic.Keyword;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper.AddedMessageListener;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper.ResultMessageListener;
import cz.martlin.kh.logic.utils.Interruptable;

/**
 * Represents query to existing connection (PicworkflowWrapper) to Picworkflow.
 * 
 * @author martin
 * 
 */
public class PicworkflowQuery implements Interruptable {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final KeywordsJsonMetadataParser PARSER = new KeywordsJsonMetadataParser();

	public static final String NAME = PicworkflowWrapper.NAME;

	private static final String COLLECTION_NAME = "searches";
	private static final String SUBMIT_METHOD_NAME = "queryRemote";

	private final PicworkflowWrapper wrapper;

	private final Set<String> required;
	private final Set<String> querying;
	private final Set<String> toSubscribe;
	private final Set<Keyword> done;

	private boolean interrupted;

	/**
	 * Creates an instance with given connection and set of keywords to process
	 * during this query.
	 * 
	 * @param wrapper
	 * @param keywordsToProcess
	 */
	public PicworkflowQuery(PicworkflowWrapper wrapper,
			Set<String> keywordsToProcess) {

		this.wrapper = wrapper;
		this.required = keywordsToProcess;

		Set<String> onServer = new HashSet<>(required.size());
		this.querying = Collections.synchronizedSet(onServer);

		Set<String> toSubscribe = new HashSet<>(required.size());
		this.toSubscribe = Collections.synchronizedSet(toSubscribe);

		Set<Keyword> done = new HashSet<>(required.size());
		this.done = Collections.synchronizedSet(done);

	}

	@Override
	public void interrupt() {
		this.interrupted = true;
		log.debug("Query interrupted, awaiting timeout step");
	}

	public Set<Keyword> getDone() {
		return done;
	}

	/**
	 * Runs querying. Sends data to server and awaits until all responses
	 * returns back. If no in specified (by Config) timeout, skips.
	 * 
	 * @return
	 * @throws NetworkException
	 */
	public Set<Keyword> runQuery() throws NetworkException {
		wrapper.checkIsConnected();

		try {
			log.info("Querying to get metadata of {} started", required);

			sendRemoteQuery();
			sendSubscribeAndUnsubscribe();
			awaitAddedReponses();

			log.info("Querying to get metadata finised, {} of {} successful",
					done.size(), required.size());

			tryToWaitOnFailed(done);

			return done;
		} catch (Exception e) {
			throw new NetworkException(NAME
					+ " query: Some error during querring", e);
		}
	}

	/**
	 * If no keyword is successfully done, waits time given by config (config#getPwFailedWait).
	 * 
	 * @param done
	 * @throws NetworkException
	 *             when wait fails
	 */
	private void tryToWaitOnFailed(Set<Keyword> done) throws NetworkException {
		if (done.isEmpty()) {

			int wait = wrapper.getConfig().getPwFailedWait();
			log.warn(
					"No successful keywords done, waiting {} ms and hoping it will help",
					wait);

			long step = wrapper.getConfig().getWaitStep();

			try {
				int waited = 0;
				while (waited < wait && !interrupted) {
					Thread.sleep(step);
					waited += step;
				}
			} catch (InterruptedException e) {
				throw new NetworkException(NAME + " query: Error during wait",
						e);
			}
		}

	}

	/**
	 * For each required keyword sends request querry and tries to await
	 * response.
	 * 
	 * @throws InterruptedException
	 */
	private void sendRemoteQuery() throws InterruptedException {
		ResultMessageListener resultList = new ResultMessageListener(this);
		wrapper.getClient().addMessageListener(DDPClient.DdpMessageType.RESULT,
				resultList);

		for (String keyword : required) {
			querying.add(keyword);
			resultList.setAwaitingFor(keyword);

			Object[] params = new Object[] { keyword, 2 };
			wrapper.getClient().call(SUBMIT_METHOD_NAME, params);

			awaitResponseAndLog(keyword);
		}

		wrapper.getClient().removeMessageListener(resultList);
	}

	/**
	 * Waits until keyword's query response gets back. Logs and - returns true
	 * if in given time arrives, false elsewhere.
	 * 
	 * @param keyword
	 * @return
	 * @throws InterruptedException
	 */
	private boolean awaitResponseAndLog(String keyword)
			throws InterruptedException {
		long waited = 0;
		long waitStep = wrapper.getConfig().getWaitStep();
		long waitMax = wrapper.getConfig().getPWQueryTimeout();

		while (querying.contains(keyword) && !interrupted && (waited < waitMax)) {
			Thread.sleep(waitStep);
			waited += waitStep;
		}

		boolean stilOnServer = querying.contains(keyword);
		if (stilOnServer) {
			log.warn(
					"Keyword {} submit did not get response in given time {} ms ({}/{} done)",
					keyword, waitMax, querying.size(), required.size());
			return false;
		} else {
			log.debug("Keyword {} submit got response in {} ms ({}/{} done)",
					keyword, waited, querying.size(), required.size());
			return true;
		}
	}

	/**
	 * Sends "subscribe" and "unsubscribe" requests to server. Theese leads to
	 * respond "added" message, which must be awaited and captured.
	 * 
	 * @param querying
	 */
	private void sendSubscribeAndUnsubscribe() {

		Object[] keywordsParam = new ArrayList<>(toSubscribe).toArray();
		Object[] subscribeParams = new Object[] { keywordsParam, 2 };

		wrapper.getClient().subscribe(COLLECTION_NAME, subscribeParams);
		wrapper.getClient().unsubscribe(COLLECTION_NAME);
	}

	/**
	 * Waits until all querying keywords gets its "added" responses with meta
	 * While this method waits, listener should each response process via
	 * {@link #keywordProcesseded(HashMap)}.
	 * 
	 * @param querying
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void awaitAddedReponses() throws InterruptedException, IOException {
		AddedMessageListener addedList = new AddedMessageListener(this);

		log.debug(
				"Started waiting to keywords' encrypts' {} metadata's messages",
				toSubscribe);

		wrapper.getClient().addMessageListener(DDPClient.DdpMessageType.ADDED,
				addedList);

		long waited = 0;
		long waitStep = wrapper.getConfig().getWaitStep();
		long waitMax = wrapper.getConfig().getPWQueryTimeout();

		while ((done.size() < required.size()) && !interrupted
				&& (waited < waitMax)) {
			Thread.sleep(waitStep);
			waited += waitStep;
		}

		if ((done.size() >= required.size())) { // rather >= you know ...
			log.debug("Got all {} metadata responses in {} ms", done.size(),
					waited);
		} else {
			Set<String> notDone = getNotDone();
			log.warn(
					"Got only {} of {} metadata responses in (given time) {} ms (not done: {})",
					done.size(), required.size(), waited, notDone);
			wrapper.getUnpicwfExporter().save(notDone);
		}

		wrapper.getClient().removeMessageListener(addedList);
	}

	/**
	 * Processes "result" message came from server corresponding to given
	 * keyword. Removes it from querying and adds to toSubscribe collection.
	 * 
	 * @param awaitingFor
	 * @param jsonFields
	 */
	public void resultMessageCame(String awaitingFor,
			HashMap<String, Object> jsonFields) {

		String keyword = awaitingFor;
		String keywordEncrypt = (String) jsonFields
				.get(DDPClient.DdpMessageField.RESULT);

		querying.remove(keyword);
		toSubscribe.add(keywordEncrypt);

		awaitingFor = null;
	}

	/**
	 * Processes "added" message came from server. Parses metadata and calls
	 * {@link #keywordProcesseded(Keyword)}.
	 * 
	 * @param jsonData
	 * @return
	 */
	public boolean addedMessageCame(HashMap<String, Object> jsonData) {

		Keyword keyword = PARSER.createKeywordOrNull(jsonData);
		if (keyword == null) {
			return false;
		}

		keywordProcesseded(keyword);
		return true;
	}

	/**
	 * Removes given keyword from remaining and adds into done. Logs.
	 * 
	 * @param keyword
	 */
	public void keywordProcesseded(Keyword keyword) {
		done.add(keyword);

		log.trace(
				"Keyword {}'s metadata successfully loaded and parsed (done {} of {})",
				keyword, done.size(), required.size());
	}

	/**
	 * Returns set of keywords which aren't or have not been successfully
	 * procceded (yet).
	 * 
	 * @return
	 */
	public Set<String> getNotDone() {
		Set<String> notDone = new HashSet<>(required);

		for (Keyword keyword : done) {
			boolean removed = notDone.remove(keyword.getKeyword());
			if (!removed) {
				log.trace(
						"In done keywords occured keyword {} which have not been required to process.",
						keyword.getKeyword());
			}
		}

		return notDone;
	}

}