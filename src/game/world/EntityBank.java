package game.world;

import java.util.ArrayList;

import game.world.entity.Entity;

public class EntityBank {
	/** Next sequential entity ID to use */
	private int nextEntityId = 0;
	
	/**
	 * List of all entities in the world.
	 * <p>
	 * <b>NB:</b> This list is sorted ascending by entity ID.
	 *            Use {@link #updateEntity(Entity)}/{@link #updateEntityCached(Entity)} to add entities to this safely
	 */
	protected ArrayList<Entity> entities;
	
	/**
	 * The cache of entities to update
	 */
	private ArrayList<Entity> updateEntities = new ArrayList<>();
	
	/**
	 * The cache of entities to remove
	 */
	private ArrayList<Integer> removeEntities = new ArrayList<>();
	
	public EntityBank(ArrayList<Entity> _entities) {
		this.entities = new ArrayList<>();
		for (Entity e : _entities) {
			// Do this to ensure that the entity IDs are set correctly
			updateEntity(e);
		}
	}
	
	/**
	 * Clone EntityBank
	 * @param bank The bank
	 */
	public EntityBank(EntityBank bank) {
		// Clone entities
		this.entities = new ArrayList<>();
		for (Entity e : bank.entities) {
			this.updateEntity(e.clone());
		}
		// Clone caches
		for (Entity e : updateEntities) {
			this.updateEntityCached(e.clone());
		}
		for (Integer id : removeEntities) {
			this.removeEntityCached(id);
		}
	}
	
	/**
	 * Create an empty EntityBank
	 */
	public EntityBank() {
		this(new ArrayList<>());
	}

	/**
	 * Process the cached records
	 */
	protected synchronized void processCache() {
		// Process cached updates
		for (Entity e : updateEntities) {
			this.updateEntity(e);
		}
		updateEntities.clear();
		
		// Remove cached entities
		for (Integer id : removeEntities) {
			this.removeEntity(id);
		}
		removeEntities.clear();
	}
	
	/**
	 * Returns the entity associated with the id entered
	 * @param id The entity ID
	 * @return null if the entity with that id does not exist
	 */
	public synchronized Entity getEntity(int id) {
		int i = getEntityInsertIndex(id);
		if (i >= entities.size())
			return null;
		Entity e = entities.get(i);
		if (e.getId() == id) {
			return e;
		}
		return null;
	}
	
	/**
	 * Caches an addition to the world of an entity, or a modification of a current entity.
	 * <p>
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * <br>
	 * If the id of the entity already exists, then the entity will be updated
	 * 
	 * @param e The entity
	 */
	public synchronized void updateEntityCached(Entity e) {
		this.updateEntities.add(e);
	}
	
	/**
	 * Adds/updates an entity to/in the world.
	 * <p>
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * <br>
	 * If the id of the entity already exists, then the entity is updated
	 * <b>NB:</b> This should *not* be called when iterating through entities, like in the main update
	 *            loop. See {@link updateEntityCached(Entity)}
	 * 
	 * @param e The entity
	 * @return The new id of the entity if it was Entity.INVALID_ID, the current id otherwise
	 */
	protected synchronized int updateEntity(Entity e) {
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
		return e.getId();
	}
	
	/**
	 * Caches the removal of the entity associated with the specified id
	 * @param id The entity id
	 */
	public synchronized void removeEntityCached(int id) {
		this.removeEntities.add(id);
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
