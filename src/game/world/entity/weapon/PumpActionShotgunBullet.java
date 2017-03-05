package game.world.entity.weapon;

import org.joml.Vector2f;

public class PumpActionShotgunBullet extends Bullet {
	private static final float SPEED = 25.0f;
	
	public PumpActionShotgunBullet(PumpActionShotgunBullet b) {
		super(b);
	}
	
	public PumpActionShotgunBullet(Vector2f position, int teamID, float angle) {
		super(position, teamID, angle, SPEED, 10.0, 0.8f);
	}
	
	@Override
	protected float getLength() {
		return velocity.length() / 80.0f;
	}
	
	@Override
	public PumpActionShotgunBullet clone() {
		return new PumpActionShotgunBullet(this);
	}
}
