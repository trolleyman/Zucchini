package game.exception;

public class ConnectionException extends GameException {
	public ConnectionException() {
		super();
	}
	public ConnectionException(String reason) {
		super(reason);
	}
	public ConnectionException(String reason, Throwable cause) {
		super(reason, cause);
	}
	public ConnectionException(Throwable cause) {
		super(cause);
	}
}
