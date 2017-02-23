package game.world.entity.weapon;

import game.Util;
import game.world.physics.EntityIntersection;
import game.world.physics.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.update.PositionUpdate;
import org.joml.Vector2f;

public abstract class Projectile extends Entity {
	private transient Vector2f prevPosition = new Vector2f();
	
	/** Identifies the source team of the bullet */
	private int sourceTeamID;
	
	/** The current velocity of the projectile */
	protected transient Vector2f velocity;
	
	/** Time to live of the bullet: after this time it automatically removes itself from the world */
	private transient double ttl;
	
	public Projectile(Vector2f position, int sourceTeamID, float angle, float speed, double ttl) {
		this(position, sourceTeamID, new Vector2f(speed * Util.getDirX(angle), speed * Util.getDirY(angle)), ttl);
	}
	
	public Projectile(Vector2f position, int _sourceTeamID, Vector2f _velocity, double _ttl) {
		super(Team.PASSIVE_TEAM, position);
		this.prevPosition.set(position);
		this.sourceTeamID = _sourceTeamID;
		this.velocity = _velocity;
		this.ttl = _ttl;
	}
	
	public Projectile(Projectile b) {
		super(b);
		this.prevPosition = new Vector2f(b.prevPosition);
		this.sourceTeamID = b.sourceTeamID;
		this.velocity = b.velocity;
		this.ttl = b.ttl;
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
		float ang = Util.getAngle(velocity.x, velocity.y);
		float x = getLength() * Util.getDirX(ang);
		float y = getLength() * Util.getDirY(ang);
		
		temp1.set(velocity).mul((float)ua.dt);
		Vector2f newPos = new Vector2f(position).add(temp1);
		ua.bank.updateEntityCached(new PositionUpdate(this.getId(), newPos));
		EntityIntersection ei = ua.bank.getIntersection(prevPosition.x, prevPosition.y, newPos.x+x, newPos.y+y,
				(e) -> Team.isHostileTeam(this.sourceTeamID, e.getTeam()));
		Vector2f mi = ua.map.intersectsLine(prevPosition.x, prevPosition.y, newPos.x+x, newPos.y+y, temp1);
		
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
			this.hitMap(ua, mi);
			// Remove bullet from the world
			ua.bank.removeEntityCached(this.getId());
		} else if (closest == temp2) {
			// Hit entity
			this.hitEntity(ua, ei);
			// Remove projectile from the world
			ua.bank.removeEntityCached(this.getId());
		}
		
		prevPosition.set(position);
		
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
	}
	
	protected abstract void hitMap(UpdateArgs ua, Vector2f mi);
	protected abstract void hitEntity(UpdateArgs ua, EntityIntersection ei);
	
	protected abstract float getLength();

	@Override
	public abstract Projectile clone();
}
