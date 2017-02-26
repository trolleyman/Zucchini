package game.world.entity;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.update.HealthUpdate;
import org.joml.Vector2f;

import java.util.ArrayList;

public class Explosion extends Entity {
	/** The damage suffered at distance 1. The maxDamage at distance is determined by the inverse square law */
	private transient float maxDamage;
	
	/** The radius of the explosion */
	private transient float radius;
	
	public Explosion(Vector2f pos, float _damage, float _radius) {
		super(Team.PASSIVE_TEAM, pos);
		this.maxDamage = _damage;
		this.radius = _radius;
	}
	
	public Explosion(Explosion e) {
		super(e);
		this.maxDamage = e.maxDamage;
		this.radius = e.radius;
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
					float damage = maxDamage / Math.min(maxDamage, d2);
					ua.bank.updateEntityCached(new HealthUpdate(e.getId(), -damage));
				}
			}
		}
		ua.bank.updateEntityCached(new HealthUpdate(this.getId(), (float) -ua.dt));
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
