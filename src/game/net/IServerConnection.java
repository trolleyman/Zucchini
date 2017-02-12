package game.net;

import game.audio.event.AudioEvent;
import game.world.entity.Entity;

/**
 * This is the main interface used by the sever to communicate with the client.
 * It is mirrored on the client-side by {@link game.net.IClientConnection IClientConnection}.
 * <p>
 * This class assumes that the client is already connected to the server with an acceptable username.
 * <p>
 * It is currently a WIP.
 * TODO: Lobby system
 * TODO: Username system
 * TODO: Password system
 * 
 * @author Callum
 */
public interface IServerConnection {
	/**
	 * Notifies the client when an entity has been added/updated
	 * @param e The entity
	 */
	public void sendUpdateEntity(Entity e);
	
	/**
	 * Notifies the client when an entity is deleted
	 * @param id The entity id
	 */
	public void sendRemoveEntity(int id);
	
	/**
	 * Notifies the client when to process an audio event
	 * @param ae The audio event
	 */
	public void sendAudioEvent(AudioEvent ae);
	
	/**
	 * Sets the current connection event handler
	 * @param sch The server connection handler
	 */
	public void setHandler(IServerConnectionHandler sch);
	
	/**
	 * Called when the connection to the client should be closed
	 */
	public void close();
	
	/**
	 * Gets the client connect to this' player id.
	 */
	public int getPlayerID();
}
