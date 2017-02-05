package game.world;

import java.util.ArrayList;

import game.ai.AI;
import game.net.IClientConnectionHandler;
import game.world.entity.Entity;
import game.world.map.Map;

/**
 * The world located on the server
 * 
 * @author Callum
 */
public class ServerWorld extends World implements Cloneable {
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
			this.handleUpdateEntity(ai.getEntity());
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
	
	/**
	 * DEBUG: This is a debug function used to transfer data to the client
	 * @param cch The client connection handler
	 */
	public void send(ServerWorld prevWorld, IClientConnectionHandler cch) {
		for (Entity e : prevWorld.entities) {
			// If the entity doesn't exist anymore
			if (getEntity(e.getId()) == null) {
				// Remove entity
				cch.handleRemoveEntity(e.getId());
			}
		}
		
		// Update/create entities from the current world
		for (Entity e : this.entities) {
			cch.handleUpdateEntity(e);
		}
	}
	
	@Override
	public Object clone() {
		ServerWorld world = new ServerWorld(this.map, new ArrayList<>(this.entities.size()), new ArrayList<>());
		
		// Clone all entities
		for (Entity e : this.entities) {
			world.handleUpdateEntity((Entity) e.clone());
		}
		
		// Clone & update AIs
		ArrayList<AI> newAIs = new ArrayList<>(this.ais.size());
		for (AI ai : this.ais) {
			AI newAI = (AI) ai.clone();
			int id = ai.getEntity().getId();
			// Reset the AI's internal entity
			newAI.setEntity(world.getEntity(id));
			newAIs.add(newAI);
		}
		
		world.ais = newAIs;
		
		return world;
	}
}
