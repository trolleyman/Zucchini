package game.action;

/**
 * An action that can be performed by a player.
 * 
 * @author Callum
 */
public class Action {
	private ActionType type;
	
	/**
	 * Constructs an action of a specific {@link game.action.ActionType ActionType}.
	 * @param _type The type of the action.
	 */
	public Action(ActionType _type) { this.type = _type; }
	
	/**
	 * Sets the current type of the action
	 * @param _type The new type
	 */
	public void setType(ActionType _type) {
		this.type = _type;
	}
	
	/**
	 * Returns the {@link game.action.ActionType ActionType} of this action.
	 */
	public ActionType getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return this.type.toString();
	}
}
