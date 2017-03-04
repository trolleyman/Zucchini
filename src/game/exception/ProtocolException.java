package game.exception;

public class ProtocolException extends GameException {
	public ProtocolException() {
		super();
	}
	public ProtocolException(String reason) {
		super(reason);
	}
	public ProtocolException(String reason, Throwable cause) {
		super(reason, cause);
	}
	public ProtocolException(Throwable cause) {
		super(cause);
	}
}
