package game.world.entity;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.render.IRenderer;
import game.world.EntityBank;

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
	 * Gets the list of entities to add to the world. This resets the list.
	 */
	public abstract ArrayList<Entity> getEntities();
	
	/**
	 * Starts the firing of the weapon
	 */
	public abstract void fireBegin();
	
	/**
	 * Ends the firing of the weapon
	 */
	public abstract void fireEnd();
}
