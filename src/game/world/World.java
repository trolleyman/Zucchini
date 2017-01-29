package game.world;

import java.util.ArrayList;

import game.Util;

public abstract class World {
	private static final double UPS = 120;
	private static final long NANOS_PER_UPDATE = (long) (Util.NANOS_PER_SECOND / UPS);
	
	private long dtPool;
	
	protected ArrayList<Entity> entities;
	
	protected ArrayList<Player> players;
	
	protected Map map;
	
	public World(Map _map) {
		this.map = _map;
		
		dtPool = 0;
		
		entities = new ArrayList<>();
	}
	
	public void update(double dt) {
		while (dtPool > NANOS_PER_UPDATE) {
			updateStep(NANOS_PER_UPDATE / ((double) Util.NANOS_PER_SECOND));
			dtPool -= NANOS_PER_UPDATE;
		}
	}
	
	public void updateStep(double dt) {
		for (Entity e : entities) {
			e.update(dt);
		}
	}
	
	/**
	 * Returns the entity associated with the id entered
	 * @param id The entity ID
	 * @return null if the entity with that id does not exist
	 */
	public Entity getEntity(int id) {
		// TODO: Search for the entity
		throw new UnsupportedOperationException();
	}
	
	public abstract void addEntity(Entity e);
}
