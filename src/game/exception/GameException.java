package game.exception;

public abstract class GameException extends Exception {
	public GameException() {
		super();
	}
	public GameException(String reason) {
		super(reason);
	}
	public GameException(String reason, Throwable cause) {
		super(reason, cause);
	}
	public GameException(Throwable cause) {
		super(cause);
	}
}
