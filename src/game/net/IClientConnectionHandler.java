package game.net;

import game.audio.event.AudioEvent;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

public interface IClientConnectionHandler {
	/**
	 * Called when an entity is added
	 * @param e The entity
	 */
	void addEntity(Entity e);
	
	/**
	 * Called when an entity is updated
	 * @param update The entity update
	 */
	void updateEntity(EntityUpdate update);
	
	/**
	 * Called when an entity is deleted
	 * @param id The entity id
	 */
	void removeEntity(int id);
	
	/**
	 * Processes an audio event
	 * @param ae The audio event
	 */
	void processAudioEvent(AudioEvent ae);
}
