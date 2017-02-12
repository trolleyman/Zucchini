package game.net;

import game.audio.event.AudioEvent;
import game.world.entity.Entity;

public interface IClientConnectionHandler {
	/**
	 * Called when an entity is added/updated
	 * @param e The entity
	 */
	public void updateEntity(Entity e);
	
	/**
	 * Called when an entity is deleted
	 * @param id The entity id
	 */
	public void removeEntity(int id);
	
	/**
	 * Processes an audio event
	 * @param ae The audio event
	 */
	public void processAudioEvent(AudioEvent ae);
}
