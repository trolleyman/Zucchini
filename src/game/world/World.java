package game.world;

import java.util.ArrayList;

import game.InputHandler;
import game.Util;
import game.ai.AI;
import game.render.IRenderer;
import game.world.entity.Entity;

public abstract class World {
	private static final double UPS = 120;
	private static final long NANOS_PER_UPDATE = (long) (Util.NANOS_PER_SECOND / UPS);
	private static final double DT_PER_UPDATE = NANOS_PER_UPDATE / (double) Util.NANOS_PER_SECOND;
	
	private double dtPool = 0;
	
	private int nextEntityId = 0;
	
	protected Map map;
	
	protected ArrayList<Entity> entities;
	
	public World(Map _map, ArrayList<Entity> _entities) {
		this.map = _map;
		
		this.entities = _entities;
	}
	
	public void update(double dt) {
		dtPool += dt;
		while (dtPool > DT_PER_UPDATE) {
			updateStep(DT_PER_UPDATE);
			dtPool -= DT_PER_UPDATE;
		}
	}
	
	protected abstract void updateStep(double dt);
	
	/**
	 * Returns the entity associated with the id entered
	 * @param id The entity ID
	 * @return null if the entity with that id does not exist
	 */
	public Entity getEntity(int id) {
		int i = getEntityInsertIndex(id);
		Entity e = entities.get(i);
		if (e.getId() == id) {
			return e;
		}
		return null;
	}
	
	/**
	 * Finds where to insert an entity with the specified id into the list
	 * 
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
	
	/**
	 * Adds an entity to the world.
	 * 
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * If the id of the entity already exists, then the entity is updated
	 * 
	 * @param e The entity
	 */
	public void addEntity(Entity e) {
		if (e.getId() == Entity.INVALID_ID) {
			// Insert entity at the end of the array
			int id = this.nextEntityId++;
			e.setId(id);
			this.entities.add(e);
		} else {
			// Insert entity somewhere in the array
			int i = getEntityInsertIndex(e.getId());
			Entity tempEntity = entities.get(i);
			
			if (tempEntity.getId() == e.getId()) {
				// Replace the entity with the new one
				entities.set(i, e);
			} else {
				// Insert the entity into the array
				entities.add(i, e);
			}
		}
	}
}
