package game.world.entity;

import game.Util;
import game.world.UpdateArgs;
import game.world.update.PositionUpdate;
import org.joml.Vector2f;

public abstract class MovableEntity extends Entity {
	public static final float VELOCITY_EPSILON = 0.00000001f;
	
	/**
	 * The current velocity of the entity.
	 */
	public Vector2f velocity;
	
	public MovableEntity(MovableEntity e) {
		super(e);
		this.velocity = e.velocity;
	}
	
	public MovableEntity(Vector2f position) {
		super(position);
		
		this.velocity = new Vector2f();
	}
	
	@Override
	public void update(UpdateArgs ua) {
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
	
	@Override
	public abstract MovableEntity clone();
}
