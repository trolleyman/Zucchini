package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.EntityIntersection;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;

public abstract class Bullet extends Entity {
	private Vector2f prevPosition = new Vector2f();
	private Vector2f velocity;
	
	/** Damage of the bullet */
	private float damage;
	
	/** Time to live of the bullet: after this time it automatically removes itself from the world */
	private double ttl;
	
	public Bullet(Bullet b) {
		super(b);
		this.prevPosition = new Vector2f(b.prevPosition);
		this.velocity = b.velocity;
		this.damage = b.damage;
		this.ttl = b.ttl;
	}
	
	public Bullet(Vector2f position, Vector2f _velocity, float _damage, double _ttl) {
		super(position);
		this.prevPosition.set(position);
		this.velocity = _velocity;
		this.damage = _damage;
		this.ttl = _ttl;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		Vector2f temp1 = Util.pushTemporaryVector2f();
		Vector2f temp2 = Util.pushTemporaryVector2f();
		
		// Decrement ttl
		this.ttl -= ua.dt;
		if (ttl <= 0.0f) {
			ua.bank.removeEntityCached(this.getId());
		}
		
		// Calculate intersection
		float x = getSizeX();
		float y = getSizeY();
		
		temp1.set(velocity).mul((float)ua.dt);
		position.add(temp1);
		EntityIntersection ei = ua.bank.getIntersection(prevPosition.x+x, prevPosition.y+y, position.x+x, position.y+y);
		Vector2f mi = ua.map.intersectsLine(prevPosition.x+x, prevPosition.y+y, position.x+x, position.y+y, temp1);
		
		// Choose closest point
		Vector2f closest;
		if (ei == null) {
			closest = mi;
		} else {
			temp2.set(ei.x, ei.y);
			closest = PhysicsUtil.getClosest(prevPosition, mi, temp2);
		}
		
		if (closest == null) {
			// Hit nothing
		} else if (closest == mi) {
			// Hit map
			System.out.println("*Plink*: Bullet hit the map");
			ua.audio.play("bullet_impact_wall.wav", 1.0f);
			// Remove bullet from the world
			ua.bank.removeEntityCached(this.getId());
		} else if (closest == temp2) {
			// Hit entity
			// Hit an entity, damage
			System.out.println("Ow! Bullet hit entity id " + ei.id);
			ua.bank.healEntityCached(ei.id, -damage);
			ua.audio.play("bullet_impact_body.wav", 1.0f);
			// Remove bullet from the world
			ua.bank.removeEntityCached(this.getId());
		}
		
		prevPosition.set(position);
		
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
	}
	
	private final float SCALE = 1/80f;
	
	private float getSizeX() {
		return velocity.x*SCALE;
	}
	private float getSizeY() {
		return velocity.y*SCALE;
	}
	
	@Override
	public void render(IRenderer r) {
		float x = getSizeX();
		float y = getSizeY();
		
		r.drawLine(
			position.x, position.y,
			position.x+x, position.y+y,
			ColorUtil.WHITE, 2.0f
		);
	}

	@Override
	public abstract Bullet clone();
}
