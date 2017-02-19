package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.EntityIntersection;
import game.world.UpdateArgs;
import game.world.update.HealthUpdate;
import org.joml.Vector2f;

public abstract class Bullet extends Projectile {
	/** Damage of the bullet */
	private transient float damage;
	
	public Bullet(Vector2f position, int sourceTeamID, float angle, float speed, double ttl, float _damage) {
		super(position, sourceTeamID, angle, speed, ttl);
		this.damage = _damage;
	}
	
	public Bullet(Vector2f position, int sourceTeamID, Vector2f velocity, double ttl, float _damage) {
		super(position, sourceTeamID, velocity, ttl);
		this.damage = _damage;
	}
	
	public Bullet(Bullet b) {
		super(b);
		this.damage = b.damage;
	}
	
	@Override
	protected void hitMap(UpdateArgs ua, Vector2f mi) {
		System.out.println("*Plink*: Bullet hit the map");
		ua.audio.play("bullet_impact_wall.wav", 1.0f);
	}
	
	@Override
	protected void hitEntity(UpdateArgs ua, EntityIntersection ei) {
		// Hit an entity, damage
		System.out.println("Ow! Bullet hit entity id " + ei.id);
		ua.bank.updateEntityCached(new HealthUpdate(ei.id, -damage));
		ua.audio.play("bullet_impact_body.wav", 1.0f);
		ua.audio.play("grunt2.wav", 1.0f);
	}
	
	@Override
	public void render(IRenderer r) {
		Vector2f temp = Util.pushTemporaryVector2f();
		temp.set(velocity).normalize().mul(this.getLength());
		float x = temp.x;
		float y = temp.y;
		
		r.drawLine(
				position.x, position.y,
				position.x+x, position.y+y,
				ColorUtil.WHITE, 2.0f
		);
		Util.popTemporaryVector2f();
	}
	
	@Override
	public abstract Bullet clone();
}
