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
	 * Clones a ServerWorld
	 */
	public ServerWorld(ServerWorld w) {
		super(w.map, new EntityBank(w.bank));
		
		// Clone & update AIs
		this.ais = new ArrayList<>();
		for (AI ai : w.ais) {
			this.ais.add(ai.clone());
		}
	}
	
	/**
	 * Constructs the world
	 * @param map The map
	 * @param bank The entity bank
	 * @param _ais List of AI controllers
	 */
	public ServerWorld(Map map, EntityBank bank, ArrayList<AI> _ais) {
		super(map, bank);
		
		this.ais = _ais;
	}
	
	public EntityBank getEntityBank() {
		return this.bank;
	}

	@Override
	protected void updateStep(double dt) {
		// Update entities
		for (Entity e : this.bank.entities) {
			e.update(this.bank, dt);
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
		for (Entity e : prevWorld.bank.entities) {
			// If the entity doesn't exist anymore
			if (this.bank.getEntity(e.getId()) == null) {
				// Remove entity
				cch.removeEntity(e.getId());
			}
		}
		
		// Update/create entities from the current world
		for (Entity e : this.bank.entities) {
			cch.updateEntity(e);
		}
	}
	
	@Override
	public ServerWorld clone() {
		return new ServerWorld(this);
	}
}
