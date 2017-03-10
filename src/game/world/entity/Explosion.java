package game.world.entity;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageType;
import game.world.entity.update.DamageUpdate;
import game.world.entity.update.HealthUpdate;
import org.joml.Vector2f;

import java.util.ArrayList;

public class Explosion extends Entity {
	/** The damage suffered at distance 1. The maxDamage at distance is determined by the inverse square law */
	private transient float maxDamage;
	
	/** The radius of the explosion */
	private transient float radius;
	
	/** The player that caused this explosion */
	private int fromId;
	/** The team that caused this explostion */
	private int fromTeam;
	
	public Explosion(Vector2f pos, int fromId, int fromTeam, float _damage, float _radius) {
		super(Team.PASSIVE_TEAM, pos);
		this.maxDamage = _damage;
		this.radius = _radius;
		this.fromId = fromId;
		this.fromTeam = fromTeam;
	}
	
	public Explosion(Explosion e) {
		super(e);
		this.maxDamage = e.maxDamage;
		this.radius = e.radius;
		this.fromId = e.fromId;
		this.fromTeam = e.fromTeam;
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		if (this.getHealth() >= this.getMaxHealth()) {
			// Damage nearby entities
			ArrayList<Entity> entities = ua.bank.getEntitiesNear(this.position.x, this.position.y, this.radius);
			for (Entity e : entities) {
				if (e.getTeam() != Team.PASSIVE_TEAM) {
					float d2 = e.position.distanceSquared(this.position);
					float fdamage = maxDamage / Math.min(maxDamage, d2);
					Damage damage = new Damage(fromId, fromTeam, DamageType.LASER_DAMAGE, fdamage);
					ua.bank.updateEntityCached(new DamageUpdate(e.getId(), damage));
				}
			}
		}
		Damage damage = new Damage(Entity.INVALID_ID, Team.INVALID_TEAM, DamageType.UNKNOWN_DAMAGE, (float)ua.dt);
		ua.bank.updateEntityCached(new DamageUpdate(this.getId(), damage));
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawCircle(this.position.x, this.position.y, this.radius, ColorUtil.RED);
	}
	
	@Override
	protected float getMaxHealth() {
		return 1.0f;
	}
	
	@Override
	public Explosion clone() {
		return new Explosion(this);
	}
}
