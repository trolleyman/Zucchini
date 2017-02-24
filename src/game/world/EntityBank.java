package game.world;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

import game.Util;
import game.net.IServerConnection;
import game.world.entity.Entity;
import game.world.physics.EntityIntersection;
import game.world.physics.PhysicsWorld;
import game.world.physics.shape.Shape;
import game.world.update.EntityUpdate;
import org.joml.Vector2f;

public class EntityBank {
	/** Next sequential entity ID to use */
	private int nextEntityId = 0;
	
	/** Next sequential team ID to use */
	private int nextFreeTeam = Team.START_FREE_TEAM;
	
	/**
	 * List of all entities in the world.
	 * <p>
	 * <b>NB:</b> This list is sorted ascending by entity ID.
	 *            Use {@link #addEntity(Entity)}/{@link #addEntityCached(Entity)} to add entities to this safely
	 */
	protected ArrayList<Entity> entities;
	
	/** The cache of entities to add */
	private ArrayList<Entity> addEntities = new ArrayList<>();
	
	/** The cache of entities to update */
	private ArrayList<EntityUpdate> updateEntities = new ArrayList<>();
	
	/** The cache of entities to remove */
	private ArrayList<Integer> removeEntities = new ArrayList<>();
	
	/** The physics world for the object */
	private PhysicsWorld physics;
	
	public EntityBank(PhysicsWorld _physics, ArrayList<Entity> _entities) {
		this.physics = _physics;
		this.entities = new ArrayList<>();
		for (Entity e : _entities) {
			// Do this to ensure that the entity IDs are set correctly
			addEntity(e);
		}
	}
	
	/**
	 * Clone EntityBank
	 * @param bank The bank
	 */
	public EntityBank(EntityBank bank) {
		// Clone entities
		this.physics = bank.physics.clone();
		this.entities = new ArrayList<>();
		for (Entity e : bank.entities) {
			this.addEntity(e.clone());
		}
		// Clone caches
		for (Entity e : bank.addEntities) {
			this.addEntityCached(e.clone());
		}
		for (EntityUpdate eu : bank.updateEntities) {
			this.updateEntityCached(eu);
		}
		for (Integer id : bank.removeEntities) {
			this.removeEntityCached(id);
		}
	}
	
	/**
	 * Create an empty EntityBank
	 */
	public EntityBank(PhysicsWorld physics) {
		this(physics, new ArrayList<>());
	}

	/**
	 * Process the cached records.
	 */
	protected synchronized void processCacheClient() {
		this.processCache(new ArrayList<>());
	}
	
	/**
	 * Process the cached records, and send them to conn.
	 * @param conns The connections to the clients.
	 */
	protected synchronized void processCache(ArrayList<IServerConnection> conns) {
		// Process cached entity adds
		for (Entity e : addEntities) {
			this.addEntity(e);
			for (IServerConnection conn : conns)
				conn.sendAddEntity(e);
		}
		addEntities.clear();
		
		// Process updates
		for (EntityUpdate eu : updateEntities) {
			Entity e = this.getEntity(eu.getId());
			if (e != null)
				eu.applyUpdate(physics, e);
			
			for (IServerConnection conn : conns)
				conn.sendUpdateEntity(eu);
		}
		updateEntities.clear();
		
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
	 * Caches an addition to the world of an entity.
	 * <p>
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * <br>
	 * If the id of the entity already exists, then the entity will be replaced
	 * 
	 * @param e The entity
	 * @return The id of the entity added to the EntityBank.
	 */
	public synchronized int addEntityCached(Entity e) {
		if (e.getId() == Entity.INVALID_ID)
			e.setId(this.nextEntityId++);
		this.addEntities.add(e);
		//System.out.println("EntityBank: Added Entity ID: " + e.getId());
		return e.getId();
	}
	
	/**
	 * Adds an entity to the world. If the entity ID already exists, returns a new entity ID.
	 * <p>
	 * If the id of the entity is Entity.INVALID_ID, then the id is set to a valid id
	 * <br>
	 * If the id of the entity already exists, then the entity is replaced.
	 * <b>NB:</b> This should *not* be called when iterating through entities, like in the main update
	 *            loop. See {@link #addEntityCached(Entity)}
	 * 
	 * @param e The entity
	 * @return The new id of the entity if it was Entity.INVALID_ID, the current id otherwise
	 */
	protected synchronized int addEntity(Entity e) {
		if (e.getId() == Entity.INVALID_ID) {
			// Insert entity at the end of the array
			int id = this.nextEntityId++;
			e.setId(id);
			this.entities.add(e);
		} else {
			// Insert entity somewhere in the array
			int i = getEntityInsertIndex(e.getId());
			
			if (i < entities.size() && entities.get(i).getId() == e.getId()) {
				// Re-add entity with fresh entityID.
				System.err.println("Warning: Added new entity that has same ID as old entity (" + e.getId() + "). Re-adding.");
				e.setId(Entity.INVALID_ID);
				this.addEntity(e);
			} else {
				// Insert the entity into the array
				if (i == entities.size() - 1)
					entities.add(i + 1, e);
				else
					entities.add(i, e);
			}
		}
		if (e.getShape() != null)
			physics.addShape(e.getShape());
		return e.getId();
	}
	
	/**
	 * Caches the update of the entity
	 * @param update The entity update
	 */
	public synchronized void updateEntityCached(EntityUpdate update) {
		this.updateEntities.add(update);
	}
	
	/**
	 * Applies the EntityUpdate
	 * @param update The entity update
	 */
	protected synchronized void updateEntity(EntityUpdate update) {
		Entity e = getEntity(update.getId());
		if (e != null)
			update.applyUpdate(physics, e);
	}
	
	/**
	 * Caches the removal of the entity associated with the specified id
	 * @param id The entity id
	 */
	public synchronized void removeEntityCached(int id) {
		//System.out.println("EntityBank: Removed Entity ID: " + id);
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
			Entity e = this.entities.remove(i);
			if (e.getShape() != null)
				physics.removeShape(e.getShape());
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
	 * Gets the closest hostile entity to x,y
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param team The team that the returned entity is hostile to
	 * @return null if an entity could not be found
	 */
	public synchronized Entity getClosestHostileEntity(float x, float y, int team) {
		Optional<Entity> oe = entities.stream()
				.filter((e) -> Team.isHostileTeam(team, e.getTeam()))
				.min((l, r) -> Float.compare(l.position.distanceSquared(x, y),
						r.position.distanceSquared(x, y)));
		
		if (oe.isPresent())
			return oe.get();
		return null;
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
	
	public int getNextFreeTeam() {
		return nextFreeTeam++;
	}
}
