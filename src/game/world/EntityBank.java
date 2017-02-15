package game.world;

import java.util.ArrayList;

import game.Util;
import game.net.IServerConnection;
import game.world.entity.Entity;
import org.joml.Vector2f;

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
	
	/** The cache of entities to update */
	private ArrayList<Entity> updateEntities = new ArrayList<>();
	
	/** The cache of entities to heal */
	private ArrayList<HealthUpdate> healEntities = new ArrayList<>();
	
	/** The cache of entities to remove */
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
		for (Entity e : bank.updateEntities) {
			this.updateEntityCached(e.clone());
		}
		for (HealthUpdate hu : bank.healEntities) {
			this.healEntities.add(hu.clone());
		}
		for (Integer id : bank.removeEntities) {
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
	 * Process the cached records, and send them to conn.
	 * @param conns The connections to the clients
	 */
	protected synchronized void processCache(ArrayList<IServerConnection> conns) {
		// Process cached updates
		for (Entity e : updateEntities) {
			this.updateEntity(e);
			for (IServerConnection conn : conns)
				conn.sendUpdateEntity(e);
		}
		updateEntities.clear();
		
		// Process health updates
		for (HealthUpdate hu : healEntities) {
			this.healEntity(hu.id, hu.health);
			Entity e = this.getEntity(hu.id);
			for (IServerConnection conn : conns)
				if (e != null)
					conn.sendUpdateEntity(e);
		}
		healEntities.clear();
		
		// Remove cached entities
		for (Integer id : removeEntities) {
			this.removeEntity(id);
			for (IServerConnection conn : conns)
				conn.sendRemoveEntity(id);
		}
		removeEntities.clear();
	}

	/**
	 * Returns the entity associated with the id entered
	 * @param id The entity ID
	 * @return null if the entity with that id does not exist
	 */
	public synchronized Entity getEntity(int id) {
		if (id == Entity.INVALID_ID)
			return null;
		
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
	 * @return The id of the entity added to the EntityBank.
	 */
	public synchronized int updateEntityCached(Entity e) {
		if (e.getId() == Entity.INVALID_ID)
			e.setId(this.nextEntityId++);
		this.updateEntities.add(e);
		return e.getId();
	}
	
	/**
	 * Adds/updates an entity to/in the world.
	 * <p>
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * <br>
	 * If the id of the entity already exists, then the entity is updated
	 * <b>NB:</b> This should *not* be called when iterating through entities, like in the main update
	 *            loop. See {@link #updateEntityCached(Entity)}
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
	protected synchronized void removeEntity(int id) {
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
	
	/**
	 * Checks if a line intersects with any collideable entity
	 * @param x0 Start x-coordinate of the line
	 * @param y0 Start y-coordinate of the line
	 * @param x1 End x-coordinate of the line
	 * @param y1 End y-coordinate of the line
	 * @return null if there was no intersection, the closest intersection to x0,y0 otherwise
	 */
	public EntityIntersection getIntersection(float x0, float y0, float x1, float y1) {
		// TODO: Implement entity collision
		return null;
	}
	
	protected synchronized void healEntity(int _id, float _health) {
		Entity e = this.getEntity(_id);
		if (e != null) {
			e.addHealth(_health);
		}
	}
	
	public synchronized void healEntityCached(int _id, float _health) {
		this.healEntities.add(new HealthUpdate(_id, _health));
	}
	
	/**
	 * Gets all entities within a certain distance of a specified position
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param radius The maximum distance between x,y and the Entity.
	 * @return A list of entities
	 */
	public synchronized ArrayList<Entity> getEntitiesNear(float x, float y, float radius) {
		float r2 = radius*radius;
		Vector2f temp = Util.pushTemporaryVector2f();
		temp.set(x, y);
		ArrayList<Entity> l = new ArrayList<>();
		for (Entity e : this.entities) {
			float d2 = temp.distanceSquared(e.position);
			if (d2 <= r2)
				l.add(e);
		}
		Util.popTemporaryVector2f();
		return l;
	}
}
