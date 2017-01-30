package game.action;

import java.util.ArrayList;

public interface IActionSource {
	/**
	 * Gets the actions accumulated since last calling getActions(). Clears the internal buffer on call.
	 * @return The list of actions to apply.
	 */
	public ArrayList<Action> getActions();
}
