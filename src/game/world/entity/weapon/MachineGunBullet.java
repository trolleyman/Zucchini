package game.world.entity;

import org.joml.Vector2f;


public class MachineGunBullet extends Bullet {
	private static final float SPEED = 30.0f;
	
	public MachineGunBullet(MachineGunBullet b) {
		super(b);
	}
	
	public MachineGunBullet(Vector2f position, int teamID, float angle) {
		super(position, teamID, new Vector2f(SPEED * (float)Math.sin(angle), SPEED * (float)Math.cos(angle)), 0.5f, 10.0);
	}
	
	@Override
	public MachineGunBullet clone() {
		return new MachineGunBullet(this);
	}
}
