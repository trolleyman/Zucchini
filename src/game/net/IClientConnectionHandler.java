package game.net;

import game.world.entity.Entity;

public interface IClientConnectionHandler {
	/**
	 * Called when an entity is added/updated
	 * @param e The entity
	 */
	public void handleUpdateEntity(Entity e);
	
	/**
	 * Called when an entity is deleted
	 * @param id The entity id
	 */
	public void handleRemoveEntity(int id);
}
