package game.action;

public class Action {
	private ActionType type;
	
	public Action(ActionType _type) {
		this.type = _type;
	}

	public ActionType getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return this.type.toString();
	}
}
