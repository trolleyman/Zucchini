package game.world.entity;

import org.joml.Vector2f;

import game.audio.AudioManager;
import game.render.IRenderer;
import game.world.UpdateArgs;

/**
 * Abstract root of all the Entity classes.
 * <p>
 * An entity is essentially an object with a position in the world. For example, the player, monsters,
 * bullets.
 * 
 * @author Callum
 */
public abstract class Entity implements Cloneable {
	/** The default maximum health of an entity */
	public static final float DEFAULT_MAX_HEALTH = 0.1f;
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
	 * The current amount of health the entity has.
	 * <p>
	 * If this goes below 0, then the entity will be removed from the world.
	 */
	private float health;
	
	/**
	 * Clones the specified entity
	 * @param e The entity
	 */
	public Entity(Entity e) {
		this.id = e.id;
		this.position = new Vector2f(e.position);
		this.angle = e.angle;
		this.health = e.health;
	}

	/**
	 * Constructs an entity at the position specified
	 * @param _position The position
	 */
	public Entity(Vector2f _position) {
		this.position = _position;
		
		this.health = this.getMaxHealth();
	}
	
	/**
	 * Updates the entity. Called every update cycle
	 * @param ua The arguments passed to each update function. See {@link game.world.UpdateArgs UpdateArgs}.
	 */
	public abstract void update(UpdateArgs ua);
	
	/**
	 * Renders the entity to the screen
	 * @param r The renderer
	 */
	public abstract void render(IRenderer r);
	
	/**
	 * Calculates an intersection with the entity and a line
	 * <p>
	 * By default this function returns null, i.e. no hitbox.
	 * @param x0 Start x-coordinate of the line
	 * @param y0 Start y-coordinate of the line
	 * @param x1 End x-coordinate of the line
	 * @param y1 End y-coordinate of the line
	 * @return null if there was not an intersection, the point of intersection otherwise
	 */
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return null;
	}
	
	/**
	 * Adds the specified amount of health to an entity.
	 * <p>
	 * To damage an entity, use a negative health.
	 * @param health The health
	 */
	public void addHealth(float health) {
		this.health += health;
	}
	
	/**
	 * Returns the maximum health of the entity
	 */
	public float getMaxHealth() {
		return DEFAULT_MAX_HEALTH;
	}
	
	/**
	 * @author abby
	 * @return health, the current health
	 */
	public float getCurrentHealth(){
		return this.health;
	}
	
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
	
	/**
	 * Implementations must override this function.
	 */
	@Override
	public abstract Entity clone();
}
