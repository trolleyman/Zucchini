package game.world.entity;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.particle.ExplosionParticleSystem;
import game.world.entity.particle.ParticleSystem;
import org.joml.Vector2f;

public class Explosion extends ParticleSystem {
	/** true if the object has just been constructed */
	private boolean init;
	
	/** The damage suffered at 0 distance. The damage at distance is determined by the inverse square law */
	private transient float damage;
	
	public Explosion(Vector2f pos, float _damage) {
		super(Team.PASSIVE_TEAM, pos);
		init = true;
		this.damage = _damage;
	}
	
	public Explosion(Explosion e) {
		super(e);
		this.init = e.init;
		this.damage = e.damage;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		ua.bank.addEntityCached(new ExplosionParticleSystem(this.position));
		ua.bank.removeEntityCached(this.getId());
	}
	
	@Override
	public void render(IRenderer r) {
		
	}
	
	@Override
	public Explosion clone() {
		return new Explosion(this);
	}
}
