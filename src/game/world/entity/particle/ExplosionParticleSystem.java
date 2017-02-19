package game.world.entity.particle;

import game.world.UpdateArgs;
import game.world.entity.Entity;
import org.joml.Vector2f;

public class ExplosionParticleSystem extends ParticleSystem {
	private boolean init;
	
	public ExplosionParticleSystem(Vector2f position) {
		super(position);
		this.init = true;
	}
	
	public ExplosionParticleSystem(ExplosionParticleSystem p) {
		super(p);
		this.init = p.init;
	}
	
	@Override
	public ExplosionParticleSystem clone() {
		return new ExplosionParticleSystem(this);
	}
}
