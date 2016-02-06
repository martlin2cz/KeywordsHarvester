package cz.martlin.kh.logic.utils;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.keysolutions.ddpclient.DDPClient;

/**
 * Extends standart DDPClient with connect-save connection (waits until
 * connection is acknowledged from server) and listeners of arbitrary messages
 * from server.
 * 
 * @author martin
 * 
 */
public class DDPClientExtension extends DDPClient {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final Gson mGson = new Gson();

	private final Map<String, List<DDPMsgListener>> listeners = new HashMap<>();

	private boolean errorOccured = false;

	public DDPClientExtension(String meteorServerIp, Integer meteorServerPort)
			throws URISyntaxException {
		super(meteorServerIp, meteorServerPort);
	}

	/**
	 * Connects and waits until server connection acknowledges.
	 * 
	 * @throws InterruptedException
	 */
	public boolean connectAndInit() throws InterruptedException {
		this.connect();

		while (this.getState() != CONNSTATE.Connected && !errorOccured) {
			Thread.sleep(100);
		}

		return errorOccured;
	}

	/**
	 * Registers listener to server response message.
	 * 
	 * @param message
	 * @param listener
	 */
	public void addMessageListener(String message, DDPMsgListener listener) {
		List<DDPMsgListener> lists = listeners.get(message);

		if (lists == null) {
			lists = new LinkedList<>();
			listeners.put(message, lists);
		}

		lists.add(listener);
	}

	/**
	 * Removes listener (or all its occurences if are).
	 * 
	 * @param listener
	 */
	public void removeMessageListener(DDPMsgListener listener) {
		Set<List<DDPMsgListener>> toRemove = new HashSet<>();

		for (String message : listeners.keySet()) {
			List<DDPMsgListener> lists = listeners.get(message);
			for (DDPMsgListener list : lists) {
				if (list.equals(listener)) {
					toRemove.add(lists);
				}
			}
		}
		for (List<DDPMsgListener> lists : toRemove) {
			lists.remove(listener);
		}

	}

	/**
	 * Removes all message listeners of selected message.
	 * 
	 * @param message
	 */
	public void removeMessageListeners(String message) {
		listeners.remove(message);
	}

	@Override
	public void received(String msg) {
		super.received(msg);

		HashMap<String, Object> jsonFields = mGson
				.<HashMap<String, Object>> fromJson((String) msg, HashMap.class);

		String msgtype = (String) jsonFields
				.get(DdpMessageField.MSG.toString());

		if (msgtype == null) {
			return;
		}

		if (msgtype.equals(DdpMessageType.ERROR.toString())) {
			log.error("Server respond errorOccured: " + jsonFields);
			this.errorOccured = true;
		}

		List<DDPMsgListener> lists = listeners.get(msgtype);
		if (lists != null) {
			for (DDPMsgListener list : lists) {
				list.process(msgtype, jsonFields);
			}
		}

	}
}
