package game.world;

import java.util.ArrayList;

import game.InputHandler;
import game.Util;
import game.ai.AI;
import game.render.IRenderer;
import game.world.entity.Entity;

/**
 * The base class for the worlds (the common functionality is located here)
 * 
 * @author Callum
 */
public abstract class World {
	/** Number of seconds to process */
	private double dtPool = 0;
	
	/** Next sequential entity ID to use */
	private int nextEntityId = 0;
	
	/**
	 * List of all entities in the world.
	 * <p>
	 * <b>NB:</b> This list is sorted ascending by entity ID.
	 *            Use {@link addEntity} to add entities to this safely
	 */
	protected ArrayList<Entity> entities;
	
	/** Current map */
	protected Map map;
	
	/**
	 * Constructs the world
	 * @param _map The map
	 * @param _entities The list of entities in the world
	 */
	protected World(Map _map, ArrayList<Entity> _entities) {
		this.map = _map;
		
		this.entities = new ArrayList<Entity>();
		for (Entity e : _entities) {
			// Do this to ensure that the entity IDs are set correctly
			updateEntity(e);
		}
	}
	
	/**
	 * Updates the world
	 * @param dt The number of seconds since the last update
	 */
	public synchronized void update(double dt) {
		dtPool += dt;
		while (dtPool > Util.DT_PER_UPDATE) {
			updateStep(Util.DT_PER_UPDATE);
			dtPool -= Util.DT_PER_UPDATE;
		}
	}
	
	/**
	 * An update step. This is called with a constant dt ({@link #DT_PER_UPDATE})
	 * @param dt The number of seconds to update the world by
	 */
	protected abstract void updateStep(double dt);
	
	/**
	 * Returns the entity associated with the id entered
	 * @param id The entity ID
	 * @return null if the entity with that id does not exist
	 */
	public synchronized Entity getEntity(int id) {
		int i = getEntityInsertIndex(id);
		Entity e = entities.get(i);
		if (e.getId() == id) {
			return e;
		}
		return null;
	}
	
	/**
	 * Adds/updates an entity to/in the world.
	 * <p>
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * <br>
	 * If the id of the entity already exists, then the entity is updated
	 * 
	 * @param e The entity
	 */
	public synchronized void updateEntity(Entity e) {
		if (e.getId() == Entity.INVALID_ID) {
			// Insert entity at the end of the array
			int id = this.nextEntityId++;
			e.setId(id);
			this.entities.add(e);
		} else {
			// Insert entity somewhere in the array
			int i = getEntityInsertIndex(e.getId());
			
			if (i < entities.size() && entities.get(i).getId() == e.getId()) {
				// Replace the entity with the new one
				entities.set(i, e);
			} else {
				// Insert the entity into the array
				entities.add(i, e);
			}
		}
	}
	
	/**
	 * Removes the entity associated with the specified id
	 * @param id The entity id
	 */
	public synchronized void removeEntity(int id) {
		int i = getEntityInsertIndex(id);
		
		if (i < this.entities.size() && this.entities.get(i).getId() == id) {
			// Remove entity
			this.entities.remove(i);
		}
	}
	
	/**
	 * Finds where to insert an entity with the specified id into the list
	 * <p>
	 * If the id exists, returns that index
	 * 
	 * @param id The entity id
	 * @return The index to insert the entity into
	 */
	private int getEntityInsertIndex(int id) {
		int min = 0;
		int max = this.entities.size() - 1;
		
		while (min < max) {
			int mid = (min + max) / 2;
			
			Entity e = this.entities.get(mid);
			int eid = e.getId();
			if (id == eid)
				return mid;
			else if (id < eid)
				max = mid - 1;
			else
				min = mid + 1;
		}
		return min;
	}
}
