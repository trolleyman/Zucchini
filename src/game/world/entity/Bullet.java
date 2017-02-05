package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.EntityBank;

public abstract class Bullet extends Entity {
	private Vector2f velocity;
	private Vector2f temp = new Vector2f();
	
	public Bullet(Bullet b) {
		super(b);
		this.velocity = b.velocity;
	}
	
	public Bullet(Vector2f position, Vector2f _velocity) {
		super(position);
		this.velocity = _velocity;
	}

	@Override
	public void update(EntityBank bank, double dt) {
		temp.set(velocity).mul((float)dt);
		position.add(temp);
	}
	
	@Override
	public void render(IRenderer r) {
		final float SCALE = 1/40f;
		
		float x = velocity.x*SCALE;
		float y = velocity.y*SCALE;
		
		r.drawLine(
			position.x-x, position.y-y,
			position.x+x, position.y+y,
			ColorUtil.WHITE, 2.0f
		);
	}

	@Override
	public abstract Bullet clone();
}
