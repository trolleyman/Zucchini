package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.EntityIntersection;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;

public abstract class Bullet extends Entity {
	private Vector2f prevPosition = new Vector2f();
	private Vector2f velocity;
	private Vector2f temp = new Vector2f();
	
	private float damage;
	
	public Bullet(Bullet b) {
		super(b);
		this.velocity = b.velocity;
		this.damage = b.damage;
	}
	
	public Bullet(Vector2f position, Vector2f _velocity, float _damage) {
		super(position);
		this.prevPosition.set(position);
		this.velocity = _velocity;
		this.damage = _damage;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		temp.set(velocity).mul((float)ua.dt);
		position.add(temp);
		EntityIntersection ei = ua.bank.getIntersection(prevPosition.x, prevPosition.y, position.x, position.y);
		Vector2f mi = ua.map.intersects(prevPosition.x, prevPosition.y, position.x, position.y);
		
		// Choose closest point
		Vector2f closest;
		if (ei == null) {
			closest = mi;
		} else {
			temp.set(ei.x, ei.y);
			closest = PhysicsUtil.getClosest(prevPosition, mi, temp);
		}
		
		if (closest == null) {
			// Hit nothing
		} else if (closest == mi) {
			// Hit map
			// Remove bullet from the world
			System.out.println("*Plink*: Bullet hit the map");
			ua.bank.removeEntityCached(this.getId());
		} else if (closest == temp) {
			// Hit entity
			// Hit an entity, damage
			System.out.println("Ow! Bullet hit entity");
			ua.bank.healEntityCached(ei.id, -damage);
			// Remove bullet from the world
			ua.bank.removeEntityCached(this.getId());
		}
		
		prevPosition.set(position);
	}
	
	@Override
	public void render(IRenderer r) {
		final float SCALE = 1/80f;
		
		float x = velocity.x*SCALE;
		float y = velocity.y*SCALE;
		
		r.drawLine(
			position.x, position.y,
			position.x+x, position.y+y,
			ColorUtil.WHITE, 2.0f
		);
	}

	@Override
	public abstract Bullet clone();
}
