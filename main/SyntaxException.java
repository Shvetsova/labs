package main;

public class SyntaxException extends Exception {
	public SyntaxException(String message) {
		super(message);
	}

	public SyntaxException(String message, Exception innEx) {
		super(message, innEx);
	}

}
