package game.net.client;

import game.LobbyInfo;
import game.action.Action;
import game.exception.ProtocolException;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * This is the main interface used by the client to communicate with the server.
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
public interface IClientConnection {
	/**
	 * Notifies the server that an action has been performed
	 * @param a The action
	 */
	void sendAction(Action a) throws ProtocolException;
	
	/**
	 * Notifies the server to send a full update of the world on the next snapshot
	 */
	void requestFullUpdate() throws ProtocolException;
	
	/**
	 * Sends a request to join a lobby
	 */
	void sendLobbyJoinRequest(String lobbyName) throws ProtocolException;
	
	/**
	 * Sends a toggle request for the ready state of the client
	 */
	void sendToggleReady() throws ProtocolException;
	
	/**
	 * Sends a lobby leave request
	 */
	void sendLobbyLeaveRequest() throws ProtocolException;
	
	/**
	 * Sends a lobby create request
	 */
	void sendLobbyCreateRequest(LobbyInfo lobby) throws ProtocolException;
	
	/**
	 * Sets the current connection event handler
	 * @param cch The client connection handler
	 */
	void setHandler(IClientConnectionHandler cch);
	
	/**
	 * Gets the name of the current user
	 */
	String getName();
	
	/**
	 * Gets the list of lobbies from the server
	 * @param successCallback Called if succesfully received the lobbies info
	 * @param errorCallback Called if an error occurs
	 */
	void getLobbies(Consumer<ArrayList<LobbyInfo>> successCallback, Consumer<String> errorCallback);
	
	/**
	 * Sends a string to the server via TCP
	 */
	void sendStringTcp(String msg) throws ProtocolException;
	
	/**
	 * Sends a string to the server via UDP
	 */
	void sendStringUdp(String msg) throws ProtocolException;
	
	/**
	 * Should be called when an error happens while receiving/sending. Closes the connection.
	 */
	void error(ProtocolException e);
	
	/**
	 * Called when the connection to the server should be closed
	 */
	void close();
	
	/**
	 * Returns true if the connection is currently closed
	 */
	boolean isClosed();
}
