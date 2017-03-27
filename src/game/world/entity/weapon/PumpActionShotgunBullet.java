package game.world.entity.weapon;

import game.world.entity.damage.DamageSource;
import org.joml.Vector2f;

public class PumpActionShotgunBullet extends Bullet {
	private static final float SPEED = 25.0f;
	
	public PumpActionShotgunBullet(Vector2f position, DamageSource source, float angle) {
		super(position, source, angle, SPEED, 10.0, 2.0f);
	}
	
	@Override
	protected float getLength() {
		return velocity.length() / 80.0f;
	}
}
