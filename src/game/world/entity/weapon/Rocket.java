package game.world.entity.weapon;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.world.EntityIntersection;
import game.world.UpdateArgs;
import org.joml.Vector2f;

public class Rocket extends Projectile {
	private static final float SPEED = 2.0f;
	
	private static final float W = 0.05f;
	private static final float H = 0.3f;
	
	public Rocket(Vector2f position, int sourceTeamID, float angle) {
		super(position, sourceTeamID, angle, SPEED, 10.0);
	}
	
	public Rocket(Rocket r) {
		super(r);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.BM, position.x, position.y, W, H, ColorUtil.RED, angle);
	}
	
	@Override
	protected float getLength() {
		return H;
	}
	
	@Override
	protected void hitMap(UpdateArgs ua, Vector2f mi) {
		this.hit(ua, mi.x, mi.y);
	}
	
	@Override
	protected void hitEntity(UpdateArgs ua, EntityIntersection ei) {
		this.hit(ua, ei.x, ei.y);
	}
	
	private void hit(UpdateArgs ua, float x, float y) {
		// TODO: Implement explosion
		System.out.println("BOOM! Explosion at " + x + ", " + y);
	}
	
	@Override
	public Rocket clone() {
		return new Rocket(this);
	}
}
