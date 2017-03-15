package game.ai;

import game.world.UpdateArgs;

public interface IStateMachine<E, S extends State<E>> {
	public void update(UpdateArgs ua);
	public void changeState(S newState);
	public void setInitialState(S state);
    public S getCurrentState();
    public boolean isInState(S state);
	public S getPreviousState();
	public boolean revertToPreviousState();
}
