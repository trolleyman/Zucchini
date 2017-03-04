package game.net.client;

import game.LobbyInfo;
import game.audio.event.AudioEvent;
import game.net.WorldStart;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

public interface IClientConnectionHandler {
	/**
	 * Called when an entity is added
	 * @param e The entity
	 */
	default void addEntity(Entity e) {
		System.err.println("Warning: CCH Unhandled Event: addEntity");
	}
	
	/**
	 * Called when an entity is updated
	 * @param update The entity update
	 */
	default void updateEntity(EntityUpdate update) {
		System.err.println("Warning: CCH Unhandled Event: updateEntity");
	}
	
	/**
	 * Called when an entity is deleted
	 * @param id The entity id
	 */
	default void removeEntity(int id) {
		System.err.println("Warning: CCH Unhandled Event: removeEntity");
	}
	
	/**
	 * Processes an audio event
	 * @param ae The audio event
	 */
	default void processAudioEvent(AudioEvent ae) {
		System.err.println("Warning: CCH Unhandled Event: processAudioEvent");
	}
	
	/**
	 * Processes a lobby update
	 */
	default void processLobbyUpdate(LobbyInfo info) {
		System.err.println("Warning: CCH Unhandled Event: processLobbyUpdate");
	}
	
	/**
	 * Called when the lobby join request has succeeded
	 */
	default void handleLobbyJoinAccept() {
		System.err.println("Warning: CCH Unhandled Event: handleLobbyJoinAccept");
	}
	
	/**
	 * Called when the lobby join request has been rejected
	 */
	default void handleLobbyJoinReject(String reason) {
		System.err.println("Warning: CCH Unhandled Event: handleLobbyJoinReject");
	}
	
	/**
	 * Called when the lobby join request has been accepted
	 */
	default void handleLobbyCreateAccept() {
		System.err.println("Warning: CCH Unhandled Event: handleLobbyCreateAccept");
	}
	
	/**
	 * Called when the lobby join request has been rejected
	 */
	default void handleLobbyCreateReject(String reason) {
		System.err.println("Warning: CCH Unhandled Event: handleLobbyCreateReject");
	}
	
	/**
	 * Called when the world start message is received, initializing a world session.
	 * @param start The starting state of the world.
	 */
	default void handleWorldStart(WorldStart start) {
		System.err.println("Warning: CCH Unhandled Event: handleWorldStart");
	}
	
	/**
	 * Called when the server tells the user that they have left the current lobby
	 */
	default void handleLobbyLeaveNotify() {
		System.err.println("Warning: CCH Unhandled Event: handleLobbyLeaveNotify");
	}
}
