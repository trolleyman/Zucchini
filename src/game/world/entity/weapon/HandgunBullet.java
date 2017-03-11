package game.world.entity.weapon;

import org.joml.Vector2f;

public class HandgunBullet extends Bullet {
	private static final float SPEED = 50.0f;
	
	public HandgunBullet(HandgunBullet b) {
		super(b);
	}
	
	public HandgunBullet(Vector2f position, int ownerId, int teamId, float angle) {
		super(position, ownerId, teamId, angle, SPEED, 10.0, 1.5f);
	}
	
	@Override
	protected float getLength() {
		return velocity.length() / 80.0f;
	}
	
	@Override
	public HandgunBullet clone() {
		return new HandgunBullet(this);
	}
}
