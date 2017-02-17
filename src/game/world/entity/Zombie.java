package game.world.entity;

import game.ColorUtil;
import game.ai.AI;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import org.joml.Vector2f;

public class Zombie extends MovableEntity {
	private static final float RADIUS = 0.15f;
	
	public Zombie(Vector2f position) {
		super(Team.MONSTER_TEAM, position);
	}
	
	public Zombie(Zombie z) {
		super(z);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	public void render(IRenderer r) {
		float x = position.x + 0.2f * (float) Math.sin(angle);
		float y = position.y + 0.2f * (float) Math.cos(angle);
		
		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
	}
	
	@Override
	protected float getMaxHealth() {
		return 10.0f;
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(position.x, position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public Zombie clone() {
		return new Zombie(this);
	}
}
