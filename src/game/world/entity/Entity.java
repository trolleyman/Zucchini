package game.world.entity;

import com.google.gson.annotations.SerializedName;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
import game.world.map.Map;
import org.joml.Vector2f;

/**
 * Abstract root of all the Entity classes.
 * <p>
 * An entity is essentially an object with a position in the world. For example, the player, monsters,
 * bullets.
 * @author Callum
 */
public abstract class Entity implements Cloneable {
	/** The default maximum health of an entity */
	public static final float DEFAULT_MAX_HEALTH = 0.1f;
	/** Represents an invalid entity ID */
	public static int INVALID_ID = -1;
	/** Current id of the entity */
	private int id = INVALID_ID;
	
	/**
	 * The team that the Entity is on.
	 * <p>
	 * The passive team is Team.PASSIVE_TEAM, and the monster team is Team.MONSTER_TEAM
	 */
	private int team;
	
	/**
	 * Position of the entity
	 */
	@SerializedName("pos")
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
	 * This holds the last damage that this entity received. Can be null.
	 */
	private transient Damage lastDamage;
	
	/**
	 * Constructs an entity at the position specified
	 * @param _position The position
	 */
	public Entity(int _team, Vector2f _position) {
		this.team = _team;
		this.position = _position;
		
		this.health = this.getMaxHealth();
	}
	
	/**
	 * Updates the entity. Called every update cycle
	 * @param ua The arguments passed to each update function. See {@link game.world.UpdateArgs UpdateArgs}.
	 */
	public abstract void update(UpdateArgs ua);
	
	/**
	 * Updates the entity. Only called on the client - use this for things that don't have a gameplay impact - simple
	 * rendering changes. By default this function does nothing.
	 * @param ua The UpdateArgs class
	 */
	public void clientUpdate(UpdateArgs ua) {
	}
	
	/**
	 * Called when the Entity dies from low health. This allows entities to say their final farewells.
	 * <p>
	 * By default ths function does nothing.
	 * @param ua The UpdateArgs class
	 */
	public void death(UpdateArgs ua) {
		//System.out.println("*URK*: Death of entity " + id + ". R.I.P.");
		if (lastDamage != null) {
			System.out.println("[Game]: " + lastDamage.type.getDescription(lastDamage.source, this));
		}
	}
	
	/**
	 * Renders the entity to the screen
	 * @param r   The renderer
	 * @param map The map
	 */
	public abstract void render(IRenderer r, Map map);
	
	/**
	 * Renders the light associated with the entity. By default the entity emits no light.
	 * @param r   The renderer
	 * @param map The map
	 */
	public void renderLight(IRenderer r, Map map) {
	}
	
	/**
	 * Renders the glitch effect associated with the entity. For most objects this will be nothing.
	 * @param r   The renderer
	 * @param map The map
	 */
	public void renderGlitch(IRenderer r, Map map) {
	}
	
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
	
	public float getHealth() {
		return this.health;
	}
	
	/**
	 * Returns the maximum health of the entity
	 */
	public float getMaxHealth() {
		return DEFAULT_MAX_HEALTH;
	}
	
	/**
	 * Damages the entity.
	 */
	public void addDamage(Damage damage) {
		this.addHealth(-damage.amount);
		this.lastDamage = damage;
	}
	
	/**
	 * Gets the last damage suffered by the entity. Can be null.
	 */
	public Damage getLastDamage() {
		return lastDamage;
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
	 * Sets the team of the Entity
	 */
	public void setTeam(int team) {
		this.team = team;
	}
	
	/**
	 * Gets the team of the Entity
	 */
	public int getTeam() {
		return this.team;
	}
	
	/**
	 * Gets the human-readable name for the entity.
	 */
	public String getReadableName() {
		return toString();
	}
}
