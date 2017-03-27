package game.world.entity;

import com.google.gson.annotations.SerializedName;
import game.Util;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import game.world.entity.update.PositionUpdate;
import game.world.entity.update.VelocityUpdate;
import org.joml.Vector2f;

public abstract class MovableEntity extends Entity {
	/**
	 * A scale for how much momentum the entity has.
	 * <p>
	 * The higher this is, the "heavier" the entity will feel
	 **/
	@SerializedName("mscale")
	protected float momentumScale;
	
	/** The current velocity of the entity. */
	@SerializedName("vel")
	public Vector2f velocity;
	
	/** The radius of the entity. If this is < 0, then the entity doesn't collide with anything */
	private float radius;
	
	/**
	 * Constructs a MovableEntity that doesn't collide with anything
	 * @param team           The team of the entity
	 * @param position       The position of the entity
	 * @param _momentumScale The momentum scale that the entity has. A player is at 1.0f scale
	 */
	public MovableEntity(int team, Vector2f position, float _momentumScale) {
		this(team, position, -1.0f, _momentumScale);
	}
	
	/**
	 * Constructs a MovableEntity that has a circular collision shape
	 * @param team           The team of the entity
	 * @param position       The position of the entity
	 * @param _radius        The radius of the entity
	 * @param _momentumScale The momentum scale that the entity has. A player is at 1.0f scale
	 */
	public MovableEntity(int team, Vector2f position, float _radius, float _momentumScale) {
		super(team, position);
		
		this.velocity = new Vector2f();
		this.radius = _radius;
		this.momentumScale = _momentumScale;
	}
	
	public boolean isCollidable() {
		return radius >= 0.0f;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		// Apply velocity
		if (this.velocity.distanceSquared(0.0f, 0.0f) <= Util.EPSILON)
			return;
		
		Vector2f newPosition = new Vector2f();
		newPosition.set(position);
		
		Vector2f temp = Util.pushTemporaryVector2f();
		temp.set(velocity).mul((float) ua.dt);
		newPosition.add(temp);
		Util.popTemporaryVector2f();
		
		if (isCollidable()) {
			// Calculate intersection
			Vector2f intersection = Util.pushTemporaryVector2f();
			if (ua.map.intersectsCircle(position.x, position.y, radius, intersection) != null) {
				// Intersection with map - push out
				newPosition.set(position)
						.sub(intersection)
						.normalize()
						.mul(radius + 0.001f)
						.add(intersection);
				//ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), new Vector2f()));
			}
			Util.popTemporaryVector2f();
		}
		ua.bank.updateEntityCached(new PositionUpdate(this.getId(), newPosition));
	}
	
	/**
	 * Add a target velocity, taking into account the momentum scaling
	 */
	public void addTargetVelocity(UpdateArgs ua, Vector2f target) {
		if (Math.abs(target.distanceSquared(this.velocity)) > Util.EPSILON) {
			Vector2f newVelocity = new Vector2f();
			newVelocity.set(this.velocity);
			newVelocity.lerp(target, Math.min(1.0f, (float) ua.dt * 8.0f / momentumScale));
			
			ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), newVelocity));
		}
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		if (isCollidable())
			PhysicsUtil.intersectCircleLine(position.x, position.y, radius, x0, y0, x1, y1, null);
		return null;
	}
}
