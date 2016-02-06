package cz.martlin.kh.logic.utils;

import java.util.HashMap;

/**
 * Processes DDP message from server.
 * 
 * @author martin
 * 
 */
public interface DDPMsgListener {

	/**
	 * Process {@code message} with {@code jsonFields} data.
	 * 
	 * @param message
	 * @param jsonFields
	 */
	void process(String message, HashMap<String, Object> jsonFields);

}
