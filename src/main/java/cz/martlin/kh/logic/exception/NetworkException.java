package cz.martlin.kh.logic.exception;

public class NetworkException extends Exception {

	private static final long serialVersionUID = 3525876600755291420L;

	public NetworkException(String message) {
		super(message);
	}

	public NetworkException(Throwable cause) {
		super(cause);
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

}
