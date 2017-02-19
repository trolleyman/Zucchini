package game.world.entity.particle;

import game.world.Team;
import game.world.entity.Entity;
import org.joml.Vector2f;

/**
 * Represents a particle effect.
 * <p>
 * e.g. smoke, explosion, sparks
 */
public abstract class ParticleSystem extends Entity {
	public ParticleSystem(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
	}
	
	public ParticleSystem(ParticleSystem p) {
		super(p);
	}
	
	@Override
	public abstract ParticleSystem clone();
}
