package game.world.entity.weapon;

import game.Util;
import game.world.EntityIntersection;
import game.world.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.MovableEntity;
import org.joml.Vector2f;

public abstract class Projectile extends MovableEntity {
	private transient Vector2f prevPosition = new Vector2f();
	
	/** for sound */
	private transient int whizzSoundID = -1;
	
	/** Identifies the source team of the bullet */
	protected transient int ownerId;
	
	/** Identifies the source team of the bullet */
	protected transient int ownerTeam;
	
	/** Time to live of the bullet: after this time it automatically removes itself from the world */
	private transient double ttl;
	
	public Projectile(Vector2f position, int ownerId, int ownerTeam, float angle, float speed, double ttl) {
		this(position, ownerId, ownerTeam, new Vector2f(speed * Util.getDirX(angle), speed * Util.getDirY(angle)), ttl);
	}
	
	public Projectile(Vector2f position, int _ownerId, int _sourceTeamID, Vector2f _velocity, double _ttl) {
		super(Team.PASSIVE_TEAM, position, 0.0f);
		this.prevPosition.set(position);
		this.ownerId = _ownerId;
		this.ownerTeam = _sourceTeamID;
		this.velocity = _velocity;
		this.ttl = _ttl;
	}
	
	public Projectile(Projectile b) {
		super(b);
		this.prevPosition = new Vector2f(b.prevPosition);
		this.ownerTeam = b.ownerTeam;
		this.ttl = b.ttl;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
		
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
		
		EntityIntersection ei = ua.bank.getIntersection(prevPosition.x, prevPosition.y, position.x+x, position.y+y,
				(e) -> Team.isHostileTeam(this.ownerTeam, e.getTeam()));
		Vector2f mi = ua.map.intersectsLine(prevPosition.x, prevPosition.y, position.x+x, position.y+y, temp1);
		
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
			this.hitMap(ua, mi, velocity);
			// Remove bullet from the world
			ua.bank.removeEntityCached(this.getId());
		} else if (closest == temp2) {
			// Hit entity
			this.hitEntity(ua, ei, velocity);
			// Remove projectile from the world
			ua.bank.removeEntityCached(this.getId());
		}
		
		prevPosition.set(position);
		
		
		// Play sounds
		if (whizzSoundID == -1) {
			this.whizzSoundID = ua.audio.play("bullet_whizz_silent.wav", 0.6f,this.position);
		} else {
			ua.audio.continueLoop(whizzSoundID, this.position);
		}
		
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
	}
	
	protected abstract void hitMap(UpdateArgs ua, Vector2f mi, Vector2f velocity);
	protected abstract void hitEntity(UpdateArgs ua, EntityIntersection ei, Vector2f velocity);
	
	protected abstract float getLength();

	@Override
	public abstract Projectile clone();
}
