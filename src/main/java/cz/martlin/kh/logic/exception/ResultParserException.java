package cz.martlin.kh.logic.exception;

public class ResultParserException extends Exception {

	private static final long serialVersionUID = 6844867798348332902L;

	public ResultParserException(String message) {
		super(message);
	}

	public ResultParserException(Throwable cause) {
		super(cause);
	}

	public ResultParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
