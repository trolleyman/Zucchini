package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.EntityIntersection;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageType;
import game.world.entity.update.DamageUpdate;

import java.util.Random;

import game.world.map.Map;
import org.joml.Vector2f;

public abstract class Bullet extends Projectile {
	
	/** Damage of the bullet */
	private transient float damage;
	
	public Bullet(Vector2f position, int ownerId, int ownerTeam, float angle, float speed, double ttl, float _damage) {
		super(position, ownerId, ownerTeam, angle, speed, ttl);
		this.damage = _damage;
	}
	
	public Bullet(Vector2f position, int ownerId, int ownerTeam, Vector2f velocity, double ttl, float _damage) {
		super(position, ownerId, ownerTeam, velocity, ttl);
		this.damage = _damage;
	}
	
	public Bullet(Bullet b) {
		super(b);
		this.damage = b.damage;
	}
	
	@Override
	protected void hitMap(UpdateArgs ua, Vector2f mi, Vector2f vel) {
		ua.audio.play("bullet_impact_wall.wav", 1.0f,mi);
	}
	
	@Override
	protected void hitEntity(UpdateArgs ua, EntityIntersection ei, Vector2f vel) {
		// Hit an entity, damage
		System.out.println("Ow! Bullet hit entity id " + ei.id);
		Damage odamage = new Damage(ownerId, ownerTeam, DamageType.BULLET_DAMAGE, damage);
		ua.bank.updateEntityCached(new DamageUpdate(ei.id, odamage));
		ua.audio.play("bullet_impact_body.wav", 1.0f, new Vector2f(ei.x,ei.y));
		Random rng = new Random();
		ua.audio.play("grunt"+(rng.nextInt(4)+1)+".wav", 0.8f, new Vector2f(ei.x,ei.y));
	}
	
	@Override
	public void render(IRenderer r, Map map) {
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
