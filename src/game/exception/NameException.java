package game.exception;

/**
 * This is thrown if another client is already connected with the same name, or the name is invalid.
 */
public class NameException extends GameException {
	public NameException() {
		super();
	}
	public NameException(String reason) {
		super(reason);
	}
	public NameException(String reason, Throwable cause) {
		super(reason, cause);
	}
	public NameException(Throwable cause) {
		super(cause);
	}
}
