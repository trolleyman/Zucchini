package game.ai;

import game.world.UpdateArgs;

/**
 * The state of a state machine is the logic of the entities
 * that enter or exit a state. 
 * @author Yean
 *
 * @param <E> The entity handled by this state machine
 */
public interface State<E> {
	/**This method will execute when a state is entered*/
	public void enter(E entity);
	/**This method will be what the entity will do while in this state*/
	public void update(E entity, UpdateArgs ua);
	/**This will be executed when the state is exited*/
	public void exit(E entity);
}
