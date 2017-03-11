package game.world.entity.weapon;

import org.joml.Vector2f;


public class MachineGunBullet extends Bullet {
	private static final float SPEED = 30.0f;
	
	public MachineGunBullet(MachineGunBullet b) {
		super(b);
	}
	
	public MachineGunBullet(Vector2f position, int ownerId, int teamId, float angle) {
		super(position, ownerId, teamId, angle, SPEED, 10.0, 0.5f);
	}
	
	@Override
	protected float getLength() {
		return velocity.length() / 80.0f;
	}
	
	@Override
	public MachineGunBullet clone() {
		return new MachineGunBullet(this);
	}
}
