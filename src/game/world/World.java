package game.world;

import game.Util;
import game.world.map.Map;

/**
 * The base class for the worlds (the common functionality is located here)
 * 
 * @author Callum
 */
public abstract class World {
	/** Number of seconds to process */
	private double dtPool = 0;
	
	/** Current map */
	protected Map map;
	
	/** Entity bank */
	protected EntityBank bank;
	
	/**
	 * Constructs the world
	 * @param _map The map
	 * @param _bank The list of entities in the world
	 */
	protected World(Map _map, EntityBank _bank) {
		this.map = _map;
		
		this.bank = _bank;
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
	
	
	
}

