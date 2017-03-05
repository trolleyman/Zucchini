package game.world.entity;

import game.Util;
import game.world.UpdateArgs;
import game.world.update.PositionUpdate;
import game.world.update.VelocityUpdate;
import org.joml.Vector2f;

public abstract class MovableEntity extends Entity {
	
	/** A scale for how much momentum the entity has.
	 * <p>
	 * The higher this is, the "heavier" the entity will feel
	 **/
	protected float momentumScale;
	
	/** The current velocity of the entity. */
	public Vector2f velocity;
	
	public MovableEntity(int team, Vector2f position, float _momentumScale) {
		super(team, position);
		
		this.velocity = new Vector2f();
		this.momentumScale = _momentumScale;
	}
	
	public MovableEntity(MovableEntity e) {
		super(e);
		this.velocity = e.velocity;
		this.momentumScale = e.momentumScale;
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
	public abstract MovableEntity clone();
}
