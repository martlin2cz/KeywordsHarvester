package cz.martlin.kh.logic.picwf;

import java.util.HashMap;
import java.util.Set;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.utils.DDPClientExtension;
import cz.martlin.kh.logic.utils.DDPMsgListener;

/**
 * Implements querying to Picworkflow.
 * 
 * @author martin
 * 
 */
public class PicworkflowWrapper {

	private final Config config;
	private DDPClientExtension client;

	public static final String NAME = "Picworkflow";
	private static final String HOST = "research.picworkflow.com";
	private static final int PORT = 80;

	public PicworkflowWrapper(Config config) {
		super();
		this.config = config;

	}

	public DDPClientExtension getClient() {
		return client;
	}

	public Config getConfig() {
		return config;
	}

	/**
	 * Connects to web service. Must not be connected yet (see {@link #finish()}
	 * to disconect).
	 * 
	 * @throws NetworkException
	 */
	public void initialize() throws NetworkException {

		checkIsNotConnected();

		try {
			client = new DDPClientExtension(HOST, PORT);

			boolean error = client.connectAndInit();
			if (error) {
				throw new NetworkException(NAME
						+ " connecting to web service seems to failed");
			}
		} catch (Exception e) {
			if (client != null) {
				try {
					client.disconnect();
				} catch (Exception eIgnore) {
				}
			}
			client = null;
			throw new NetworkException(NAME + " init: some error", e);
		}

	}

	/**
	 * Disconects. See {@link #initialize()} for connect again (but, rather
	 * create new object again).
	 * 
	 * @throws NetworkException
	 */
	public void finish() throws NetworkException {
		checkIsConnected();

		try {
			client.disconnect();
		} catch (Exception e) {
			throw new NetworkException(NAME
					+ " querry: Some error during finishing", e);
		}

		client = null;
	}

	/**
	 * Closes and again initializes connection.
	 * 
	 * @throws NetworkException
	 * 
	 */
	public void reconnect() throws NetworkException {
		finish();
		initialize();
	}

	/**
	 * Throws exception when is yet connected.
	 * 
	 * @throws NetworkException
	 */
	public void checkIsNotConnected() throws NetworkException {
		if (client != null) {
			throw new NetworkException(NAME + ": Yet connected");
		}
	}

	/**
	 * Throws exception when is not (yet) connected.
	 * 
	 * @throws NetworkException
	 */
	public void checkIsConnected() throws NetworkException {
		if (client == null) {
			throw new NetworkException(NAME + ": Not connected");
		}
	}

	public PicworkflowQuery createQuerry(Set<String> keywords) {
		return new PicworkflowQuery(this, keywords);
	}

	/**
	 * Listener of "added" message from server which contains metadata of
	 * keyword. When recieves "added" method from server, invokes
	 * {@link PicworkflowQuery#addedMessageCame(HashMap)}.
	 * 
	 * @author martin
	 * 
	 */
	public static class AddedMessageListener implements DDPMsgListener {

		private final PicworkflowQuery query;

		public AddedMessageListener(PicworkflowQuery query) {
			this.query = query;
		}

		@Override
		public void process(String message, HashMap<String, Object> jsonFields) {
			this.query.addedMessageCame(jsonFields);
		}

	}

	public static class ResultMessageListener implements DDPMsgListener {

		private final PicworkflowQuery query;
		private String awaitingFor;

		public ResultMessageListener(PicworkflowQuery query) {
			this.query = query;
		}

		public void setAwaitingFor(String awaitingFor) {
			this.awaitingFor = awaitingFor;
		}

		@Override
		public void process(String message, HashMap<String, Object> jsonFields) {
			this.query.resultMessageCame(awaitingFor, jsonFields);

		}

	}

}
