package game.net.server;

import game.audio.event.AudioEvent;
import game.exception.ProtocolException;
import game.net.client.IClientConnection;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

/**
 * This is the main interface used by the sever to communicate with the client.
 * It is mirrored on the client-side by {@link IClientConnection IClientConnection}.
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
	 * Notifies the client when an entity has been added
	 * @param e The entity
	 */
	void sendAddEntity(Entity e) throws ProtocolException;
	
	/**
	 * Notifies the client when an entity has been updated
	 * @param update The update
	 */
	void sendUpdateEntity(EntityUpdate update) throws ProtocolException;
	
	/**
	 * Notifies the client when an entity is deleted
	 * @param id The entity id
	 */
	void sendRemoveEntity(int id) throws ProtocolException;
	
	/**
	 * Notifies the client when to process an audio event
	 * @param ae The audio event
	 */
	void sendAudioEvent(AudioEvent ae) throws ProtocolException;
	
	/**
	 * Sets the current connection event handler
	 * @param sch The server connection handler
	 */
	void setHandler(IServerConnectionHandler sch);
	
	/**
	 * Called when the connection to the client should be closed
	 */
	void close();
	
	/**
	 * Gets the client connect to this' player id.
	 */
	int getPlayerID();
	
	/**
	 * Returns true if the connection is currently closed
	 */
	boolean isClosed();
}
