package game.ai;

import game.world.UpdateArgs;

/**
 * Implementation of a state machine of the {@link IStateMachine} interface
 * @author Yean, George
 *
 * @param <E> The entity that owns this state machine
 * @param <S> The type of the states of state machine
 */
public class StateMachine<E, S extends State<E>> implements IStateMachine<E,S> {
	protected E owner;
	protected S currentState;
	protected S previousState;
	/**
	 * constructor of the state machine
	 * @param owner who is the owner of the state machine
	 * @param initialState what is initial state of the state machine
	 */
	public StateMachine(E owner, S initialState){
		this.owner = owner;
		this.setInitialState(initialState);
	}
	
	/**
	 * update the state machine by finding what the current state is and making that state update
	 * @param the update arguments
	 */
	@Override
	public void update(UpdateArgs ua) {
		if(currentState!=null) currentState.update(this.owner, ua);
	}
	/**
	 * get the owner
	 * @return the owner
	 */
	public E getOwner(){
		return owner;
	}
	/**
	 * change the state of the statemachine
	 * @param the new state we want to change to
	 */
	@Override
	public void changeState(S newState) {
		previousState = currentState;
		if(currentState!=null) currentState.exit(owner);
		currentState=newState;
		if(currentState!=null) currentState.enter(owner);		
	}
	/**
	 * set the intial state
	 * @param state we want to start with
	 */
	@Override
	public void setInitialState(S state){
		this.previousState = null;
		this.currentState = state;
	}
	/**
	 * get the current state
	 * @return the current state
	 */
	@Override
	public S getCurrentState(){
		return currentState;
	}
	/**
	 * is the statemachine in this state
	 * @param state to check whether we are in the state
	 * @return whether the state == the current state
	 */
	@Override
	public boolean isInState(S state) {
		return state == currentState;
	}
	
	/**
	 * get the previous state
	 * @return the previous state
	 */
	@Override
	public S getPreviousState(){
		return previousState;
	}
	/** 
	 * revert to the previous state
	 * @return whether it was successful
	 */
	@Override
	public boolean revertToPreviousState(){
		if (previousState==null) return false;
		changeState(previousState);
		return true;
	}
	/**
	 * set the owner of the state machine
	 * @param owner we want the statemachine to be set to
	 */
	public void setOwner(E owner){
		this.owner = owner;
	}
}
