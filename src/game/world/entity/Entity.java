package game.world.entity;

import org.joml.Vector2f;

import game.render.IRenderer;

/**
 * Abstract root of all the Entity classes.
 * <p>
 * An entity is essentially an object with a position in the world. For example, the player, monsters,
 * bullets.
 * 
 * @author Callum
 */
public abstract class Entity {
	/** Represents an ivalid entity ID */
	public static int INVALID_ID = -1;
	/** Current id of the entity */
	private int id = INVALID_ID;
	
	/**
	 * Position of the entity
	 */
	public Vector2f position;
	
	/**
	 * The angle clockwise in radians from the north direction.
	 */
	public float angle;
	
	/**
	 * Constructs an entity at the position specified
	 * @param _position The position
	 */
	public Entity(Vector2f _position) {
		this.position = _position;
	}
	
	/**
	 * Updates the entity. Called every update cycle
	 * @param dt Number of seconds to update the entity by
	 */
	public abstract void update(double dt);
	/**
	 * Renders the entity to the screen
	 * @param r The renderer
	 */
	public abstract void render(IRenderer r);
	
	/**
	 * Returns the entity ID
	 */
	public int getId() {
		return id;
	}
	/**
	 * Sets the entity ID
	 */
	public void setId(int _id) {
		this.id = _id;
	}
}
