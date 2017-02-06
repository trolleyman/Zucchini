package game.world.entity;

import org.joml.Vector2f;

/**
 * A weapon is something that the player can hold, and fire.
 * 
 * @author Callum
 */
public abstract class Weapon extends Entity {
	public Weapon(Weapon w) {
		super(w);
	}
	
	public Weapon(Vector2f position) {
		super(position);
	}
	
	/**
	 * Starts the firing of the weapon
	 */
	public abstract void fireBegin();
	
	/**
	 * Ends the firing of the weapon
	 */
	public abstract void fireEnd();
}
