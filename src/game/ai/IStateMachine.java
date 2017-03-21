package game.ai;

import game.world.UpdateArgs;

public interface IStateMachine<E, S extends State<E>> {
	void update(UpdateArgs ua);
	void changeState(S newState);
	void setInitialState(S state);
	S getCurrentState();
    boolean isInState(S state);
	S getPreviousState();
	boolean revertToPreviousState();
}
