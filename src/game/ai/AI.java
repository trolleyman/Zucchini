package game.ai;

import game.world.UpdateArgs;

/**
 * The base class for AI. The AI can see the world and update internal state using the update method.
 * 
 * @author Callum
 */
public abstract class AI implements Cloneable {
	protected int entityID;
	
	public AI(int _entityID) {
		this.entityID = _entityID;
	}
	
	/**
	 * Clones the specified AI
	 * @param ai The AI
	 */
	public AI(AI ai) {
		this.entityID = ai.entityID;
	}
	
	/**
	 * This is the main method for the AI. Every world update this is called.
	 * @param ua The UpdateArgs object. See {@link game.world.UpdateArgs UpdateArgs}.
	 */
	public abstract void update(UpdateArgs ua);
	
	/**
	 * Implementations must override {@link java.lang.Object#clone clone}
	 */
	@Override
	public abstract AI clone();
}
