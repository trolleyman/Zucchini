package game.world.entity;

import org.joml.Vector2f;

import game.render.IRenderer;

public class HandgunBullet extends Bullet {
	private static final float SPEED = 50.0f;
	
	public HandgunBullet(HandgunBullet b) {
		super(b);
	}
	
	public HandgunBullet(Vector2f position, float angle) {
		super(position, new Vector2f(SPEED * (float)Math.sin(angle), SPEED * (float)Math.cos(angle)), 1.5f, 10.0);
	}
	
	@Override
	public HandgunBullet clone() {
		return new HandgunBullet(this);
	}
}
