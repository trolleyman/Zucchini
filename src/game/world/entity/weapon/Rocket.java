package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.EntityIntersection;
import game.world.UpdateArgs;
import game.world.entity.Explosion;
import game.world.entity.damage.DamageSource;
import game.world.entity.update.VelocityUpdate;
import game.world.map.Map;
import org.joml.Vector2f;

public class Rocket extends Projectile {
	private static final float INITIAL_SPEED = 5.0f;
	private static final float ACCELERATION = 100.0f;
	private static final float MAX_SPEED = 40.0f;
	
	private static final float W = 0.05f;
	private static final float H = 0.2f;
	
	public Rocket(Vector2f position, DamageSource source, float angle) {
		super(position, source, angle, INITIAL_SPEED, 12.0);
	}
	
	public Rocket(Rocket r) {
		super(r);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
		Vector2f vdir = Util.pushTemporaryVector2f().set(velocity).normalize();
		Vector2f newvel = new Vector2f(vdir)
				.mul(ACCELERATION * (float)ua.dt)
				.add(velocity);
		float len = newvel.length();
		if (len > MAX_SPEED) {
			newvel.mul(1/len).mul(MAX_SPEED);
		}
		ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), newvel));
		Util.popTemporaryVector2f();
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		r.drawBox(Align.BM, position.x, position.y, W, H, ColorUtil.WHITE, Util.getAngle(velocity.x, velocity.y));
	}
	
	@Override
	protected float getLength() {
		return H;
	}
	
	@Override
	protected void hitMap(UpdateArgs ua, Vector2f mi, Vector2f velocity) {
		this.hit(ua, new Vector2f(mi), velocity);
	}
	
	@Override
	protected void hitEntity(UpdateArgs ua, EntityIntersection ei, Vector2f velocity) {
		this.hit(ua, new Vector2f(ei.x, ei.y), velocity);
	}
	
	private void hit(UpdateArgs ua, Vector2f pos, Vector2f vel) {
		//System.out.println("[Game]: BOOM! Explosion at " + pos.x + ", " + pos.y + " with vel " + vel.x + ", " + vel.y);
		ua.audio.play("explosion.wav", 1.0f, pos);
		
		Vector2f nvel = Util.pushTemporaryVector2f().set(vel).normalize().mul(0.05f);
		ua.bank.addEntityCached(new Explosion(pos.sub(nvel), source, 30.0f, 3.0f));
		Util.popTemporaryVector2f();
	}
	
	@Override
	public Rocket clone() {
		return new Rocket(this);
	}
}
