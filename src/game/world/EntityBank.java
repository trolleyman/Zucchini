package game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Predicate;

import game.Util;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.world.entity.Entity;
import game.world.entity.update.EntityUpdate;
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
	protected HashMap<Integer, Entity> entities;
	
	/** The cache of entities to add */
	private ArrayList<Entity> addEntities = new ArrayList<>();
	
	/** The cache of entities to update */
	private ArrayList<EntityUpdate> updateEntities = new ArrayList<>();
	
	/** The cache of entities to remove */
	private ArrayList<Integer> removeEntities = new ArrayList<>();
	
	public EntityBank(ArrayList<Entity> _entities) {
		this.entities = new LinkedHashMap<>();
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
		this.entities = new HashMap<>();
		for (Entity e : bank.entities.values()) {
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
	public EntityBank() {
		this(new ArrayList<>());
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
	protected synchronized void processCache(ArrayList<ServerWorldClient> conns) {
		// Process cached entity adds
		for (Entity e : addEntities) {
			this.addEntity(e);
			for (ServerWorldClient swc : conns) {
				if (swc.handler.isClosed())
					continue;
				
				try {
					swc.handler.sendStringTcp(Protocol.sendAddEntity(e));
				} catch (ProtocolException ex) {
					// This is ok, as the ClientHandler will handle this
				}
			}
		}
		addEntities.clear();
		
		// Process updates
		for (EntityUpdate eu : updateEntities) {
			Entity e = this.getEntity(eu.getId());
			if (e != null)
				eu.updateEntity(e);
			
			for (ServerWorldClient swc : conns) {
				if (swc.handler.isClosed())
					continue;
				
				try {
					String euStr = Protocol.sendUpdateEntity(eu);
					if (eu.isTcp())
						swc.handler.sendStringTcp(euStr);
					else
						swc.handler.sendStringUdp(euStr);
					
				} catch (ProtocolException ex) {
					// This is ok, as the ClientHandler will handle this
				}
			}
		}
		updateEntities.clear();
		
		// Remove cached entities
		for (Integer id : removeEntities) {
			this.removeEntity(id);
			for (ServerWorldClient swc : conns) {
				if (swc.handler.isClosed())
					continue;
				
				try {
					swc.handler.sendStringTcp(Protocol.sendRemoveEntity(id));
				} catch (ProtocolException ex) {
					// This is ok, as the ClientHandler will handle this
				}
			}
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
		
		return entities.get(id);
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
	 * Adds an entity to the world.
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
			// Generate new id
			int id = this.nextEntityId++;
			e.setId(id);
			this.addEntity(e);
		} else {
			// Insert entity
			entities.put(e.getId(), e);
		}
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
			update.updateEntity(e);
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
		entities.remove(id);
	}
	
	/**
	 * Checks if a line intersects with any collideable entity. Only tests entities that the predicate returns true for.
	 * @param x0 Start x-coordinate of the line
	 * @param y0 Start y-coordinate of the line
	 * @param x1 End x-coordinate of the line
	 * @param y1 End y-coordinate of the line
	 * @param pred The predicate
	 * @return null if there was no intersection, the closest intersection to x0,y0 otherwise
	 */
	public EntityIntersection getIntersection(float x0, float y0, float x1, float y1, Predicate<Entity> pred) {
		int id = Entity.INVALID_ID;
		Vector2f ret = null;
		for (Entity e : entities.values()) {
			if (!pred.test(e))
				continue;
			
			Vector2f intersection = e.intersects(x0, y0, x1, y1);
			if (ret == null) {
				ret = intersection;
				id = e.getId();
			} else if (intersection != null) {
				float retd2 = ret.distanceSquared(x0, y0);
				float intersectiond2 = intersection.distanceSquared(x0, y0);
				if (retd2 > intersectiond2) {
					ret = intersection;
					id = e.getId();
				}
			}
		}
		if (ret == null)
			return null;
		
		return new EntityIntersection(id, ret.x, ret.y);
	}
	
	/**
	 * Gets the closest hostile entity to x,y
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param team The team that the returned entity is hostile to
	 * @return null if an entity could not be found
	 */
	public synchronized Entity getClosestHostileEntity(float x, float y, int team) {
		Optional<Entity> oe = entities.values().stream()
				.filter((e) -> Team.isHostileTeam(team, e.getTeam()))
				.min((l, r) -> Float.compare(l.position.distanceSquared(x, y),
						r.position.distanceSquared(x, y)));
		
		return oe.orElse(null);
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
		for (Entity e : this.entities.values()) {
			float d2 = temp.distanceSquared(e.position);
			if (d2 <= r2)
				l.add(e);
		}
		Util.popTemporaryVector2f();
		return l;
	}
	
	/**
	 * Get all entities that satisfy a predicate
	 * @return The entities. Can be null if there were no entities.
	 */
	public ArrayList<Entity> getEntities(Predicate<Entity> pred) {
		ArrayList<Entity> ret = null;
		for (Entity e : entities.values()) {
			if (pred.test(e)) {
				if (ret == null)
					ret = new ArrayList<>();
				ret.add(e);
			}
		}
		return ret;
	}
	
	public int getNextFreeTeam() {
		return nextFreeTeam++;
	}
}
