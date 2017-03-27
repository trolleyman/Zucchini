package game.world.entity.weapon;

import game.world.entity.damage.DamageSource;
import org.joml.Vector2f;


public class MachineGunBullet extends Bullet {
	private static final float SPEED = 30.0f;
	
	public MachineGunBullet(Vector2f position, DamageSource source, float angle) {
		super(position, source, angle, SPEED, 10.0, 1.2f);
	}
	
	@Override
	protected float getLength() {
		return velocity.length() / 80.0f;
	}
}
