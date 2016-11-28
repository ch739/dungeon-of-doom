package dod.game;

/**
 * An exception to handle invalid message commands
 */
public class MessageException extends Exception {
	private static final long serialVersionUID = -1965743877993357846L;

	public MessageException(String message) {
		super(message);
	}
}
