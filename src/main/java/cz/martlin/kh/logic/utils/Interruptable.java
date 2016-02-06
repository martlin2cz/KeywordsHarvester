package cz.martlin.kh.logic.utils;

/**
 * This service can be interrupted.
 * 
 * @author martin
 * 
 */
public interface Interruptable {
	/**
	 * Marks this service as interrupted and it shoud stop its work as soon as
	 * possible.
	 */
	public void interrupt();

}
