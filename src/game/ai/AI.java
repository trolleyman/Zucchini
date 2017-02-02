package game.ai;

import game.world.ServerWorld;
import game.world.World;
import game.world.entity.Entity;

/**
 * The base class for AI. The AI can see the world and update internal state using the update method.
 * 
 * @author Callum
 */
public abstract class AI implements Cloneable {
	private Entity entity;
	
	public AI(Entity _entity) {
		this.entity = _entity;
	}
	
	/**
	 * This is the main method for the AI. Every world update this is called.
	 * @param w This is the world state at the world update. <b>This *should not* be modified in any way
	 *          during this method!!</b>
	 * @param dt The number of seconds since the last update.
	 */
	public abstract void update(ServerWorld w, double dt);
	
	/**
	 * Returns the {@link game.world.entity.Entity Entity} that this AI controls.
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Sets the internal entity of the AI.
	 * <p>
	 * <b>NB:</b> Ensure that the entity is of the correct type for the subclasses!
	 * @param _entity The entity
	 */
	public void setEntity(Entity _entity) {
		this.entity = _entity;
	}
	
	/**
	 * Implementations must override {@link java.lang.Object#clone clone}
	 */
	@Override
	public abstract Object clone();
}
