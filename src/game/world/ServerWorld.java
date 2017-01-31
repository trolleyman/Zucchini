package game.world;

import java.util.ArrayList;

import game.ai.AI;
import game.world.entity.Entity;

/**
 * The world located on the server
 * 
 * @author Callum
 */
public class ServerWorld extends World {
	/**
	 * The AIs/Players in the world.
	 */
	private ArrayList<AI> ais;
	
	/**
	 * Constructs the world
	 * @param _map The map
	 * @param _entities The list of entities to begin with
	 * @param _ais List of AI controllers
	 */
	public ServerWorld(Map _map, ArrayList<Entity> _entities, ArrayList<AI> _ais) {
		super(_map, _entities);
		
		this.ais = _ais;
		
		// Ensure ais are entities
		for (AI ai : ais) {
			this.addEntity(ai.getEntity());
		}
	}

	@Override
	protected void updateStep(double dt) {
		// Update entities
		for (Entity e : entities) {
			e.update(dt);
		}
		
		// Update AI/players
		for (AI ai : ais) {
			ai.update(this, dt);
		}
	}
}
