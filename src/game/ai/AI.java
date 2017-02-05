package game.ai;

import game.world.EntityBank;
import game.world.ServerWorld;
import game.world.World;
import game.world.entity.Entity;

/**
 * The base class for AI. The AI can see the world and update internal state using the update method.
 * 
 * @author Callum
 */
public abstract class AI implements Cloneable {
	private int entity;
	
	/**
	 * Clones the specified AI
	 * @param ai The AI
	 */
	public AI(AI ai) {
		this.entity = ai.entity;
	}
	
	/**
	 * Constructs an AI that tracks a specified entity
	 * @param _entity The entity
	 */
	public AI(int _entity) {
		this.entity = _entity;
	}
	
	/**
	 * This is the main method for the AI. Every world update this is called.
	 * @param w This is the world state at the world update. <b>This *should not* be modified in any way
	 *          during this method! (Apart from through EntityBank)</b>
	 * @param dt The number of seconds since the last update.
	 */
	public abstract void update(ServerWorld w, double dt);
	
	/**
	 * Implementations must override {@link java.lang.Object#clone clone}
	 */
	@Override
	public abstract AI clone();
}
