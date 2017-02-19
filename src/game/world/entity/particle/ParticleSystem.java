package game.world.entity.particle;

import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * Represents a particle effect.
 * <p>
 * e.g. smoke, explosion, sparks
 */
public abstract class ParticleSystem extends Entity {
	private ArrayList<Particle> particles;
	
	public ParticleSystem(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
	}
	
	public ParticleSystem(ParticleSystem p) {
		super(p);
		if (p.particles != null)
			this.particles = new ArrayList<>(p.particles);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		if (this.particles == null)
			this.particles = initialParticles();
		
		
	}
	
	protected abstract ArrayList<Particle> initialParticles();
	
	@Override
	public abstract ParticleSystem clone();
}
