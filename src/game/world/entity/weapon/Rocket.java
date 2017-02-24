package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.entity.Entity;
import game.world.physics.EntityIntersection;
import game.world.UpdateArgs;
import game.world.entity.Explosion;
import org.joml.Vector2f;

public class Rocket extends Projectile {
	private static final float SPEED = 10.0f;
	
	private static final float W = 0.05f;
	private static final float H = 0.2f;
	
	public Rocket(Vector2f position, int sourceTeamID, float angle) {
		super(position, sourceTeamID, angle, SPEED, 12.0);
	}
	
	public Rocket(Rocket r) {
		super(r);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.BM, position.x, position.y, W, H, ColorUtil.WHITE, Util.getAngle(velocity.x, velocity.y));
	}
	
	@Override
	protected float getLength() {
		return H;
	}
	
	@Override
	protected void hitMap(UpdateArgs ua, Vector2f point) {
		this.hit(ua, new Vector2f(point));
	}
	
	@Override
	protected void hitEntity(UpdateArgs ua, Entity e, Vector2f point) {
		this.hit(ua, new Vector2f(point));
	}
	
	private void hit(UpdateArgs ua, Vector2f pos) {
		System.out.println("BOOM! Explosion at " + pos.x + ", " + pos.y);
		ua.bank.addEntityCached(new Explosion(pos, 10.0f, 1.5f));
	}
	
	@Override
	public Rocket clone() {
		return new Rocket(this);
	}
}
