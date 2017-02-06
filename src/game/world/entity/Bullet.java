package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.EntityIntersection;
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
		
		// --- After this section id will hold the id of the entity that the intersection hit
		//     (Entity.INVALID_ID for the map), and temp will hold the position of the intersection. ---
		boolean hit;
		int id;
		if (ei != null && mi != null) {
			hit = true;
			// Get closest intersection
			float eiX = prevPosition.x - ei.x;
			float eiY = prevPosition.y - ei.y;
			float eiD = eiX*eiX + eiY*eiY;
			
			// Compare by distance squared to save precious CPU cycles
			float miD = mi.distanceSquared(prevPosition);
			
			if (miD < eiD) {
				// Use map intersection
				id = Entity.INVALID_ID;
				temp.set(mi);
			} else {
				id = ei.id;
				temp.set(ei.x, ei.y);
			}
		} else if (ei == null && mi != null) {
			hit = true;
			id = Entity.INVALID_ID;
			temp.set(mi);
		} else if (ei != null && mi == null) {
			hit = true;
			id = ei.id;
			temp.set(ei.x, ei.y);
		} else {
			hit = false;
			id = Entity.INVALID_ID;
		}
		// --- Now id contains the id, temp contains the position ---
		
		if (hit) {
			if (id == Entity.INVALID_ID) {
				// Hit the map
				System.out.println("*Plink*: Bullet hit the map");
			} else {
				// Hit an entity, damage
				System.out.println("Ow! Bullet hit entity");
				ua.bank.healEntityCached(id, -damage);
			}
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
