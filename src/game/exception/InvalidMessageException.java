package game.exception;

public class InvalidMessageException extends ProtocolException {
	public InvalidMessageException(String message) {
		super("Invalid message received: " + message);
	}
}
