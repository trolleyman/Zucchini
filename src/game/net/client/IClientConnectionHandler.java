package game.net.client;

import game.LobbyInfo;
import game.audio.event.AudioEvent;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

public interface IClientConnectionHandler {
	/**
	 * Called when an entity is added
	 * @param e The entity
	 */
	default void addEntity(Entity e) {}
	
	/**
	 * Called when an entity is updated
	 * @param update The entity update
	 */
	default void updateEntity(EntityUpdate update) {}
	
	/**
	 * Called when an entity is deleted
	 * @param id The entity id
	 */
	default void removeEntity(int id) {}
	
	/**
	 * Processes an audio event
	 * @param ae The audio event
	 */
	default void processAudioEvent(AudioEvent ae) {}
	
	/**
	 * Processes a lobby update
	 */
	default void processLobbyUpdate(LobbyInfo info) {}
	
	/**
	 * Called when the lobby join request has succeeded
	 */
	default void handleLobbyJoinAccept() {}
	
	/**
	 * Called when the lobby join request has been rejected
	 */
	default void handleLobbyJoinReject(String reason) {}
}
