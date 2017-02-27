package game.net;

import game.LobbyInfo;
import game.PlayerInfo;
import game.action.Action;
import game.audio.event.AudioEvent;
import game.net.client.DummyClientConnectionHandler;
import game.net.client.IClientConnection;
import game.net.client.IClientConnectionHandler;
import game.net.server.*;
import game.world.Team;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * A dummy class to implement {@link IClientConnection} and {@link IServerConnection} so that we can
 * play the game and prototype aspects of it without having to implement networking atm.
 * 
 * @author Callum
 */
public class LinkConnection implements IServerConnection, IClientConnection {
	private IServerConnectionHandler sch = new DummyServerConnectionHandler();
	private ILobbyServerConnectionHandler lsch = new DummyLobbyServerConnectionHandler();
	private IClientConnectionHandler cch = new DummyClientConnectionHandler();
	
	private int playerID;
	
	/**
	 * Constructs a new {@link LinkConnection}.
	 */
	public LinkConnection(int _playerID) {
		this.playerID = _playerID;
	}
	
	@Override
	public void sendAction(Action a) {
		lsch.handleAction(a);
	}
	
	@Override
	public void requestFullUpdate() {
		lsch.handleFullUpdateRequest();
	}
	
	@Override
	public void setHandler(IClientConnectionHandler _cch) {
		this.cch = _cch;
		this.requestFullUpdate();
	}
	
	@Override
	public void sendAddEntity(Entity e) {
		cch.addEntity(e);
	}
	
	@Override
	public void sendUpdateEntity(EntityUpdate update) {
		cch.updateEntity(update);
	}
	
	@Override
	public void sendRemoveEntity(int id) {
		cch.removeEntity(id);
	}
	
	@Override
	public void sendAudioEvent(AudioEvent ae) {
		cch.processAudioEvent(ae);
	}
	
	@Override
	public void setHandler(IServerConnectionHandler _sch) {
		this.sch = _sch;
		this.requestFullUpdate();
	}
	
	@Override
	public void getLobbies(Consumer<ArrayList<LobbyInfo>> successCallback, Consumer<String> errorCallback) {
		ArrayList<LobbyInfo> lobbies = new ArrayList<>();
		lobbies.add(new LobbyInfo("Test Lobby 1", 4, new PlayerInfo[]{
				new PlayerInfo("Player1", Team.START_FREE_TEAM)
		}));
		successCallback.accept(lobbies);
	}
	
	@Override
	public void setLobbyHandler(ILobbyServerConnectionHandler _lsch) {
		this.lsch = _lsch;
		this.requestFullUpdate();
	}
	
	@Override
	public int getPlayerID() {
		return playerID;
	}
	
	@Override
	public void close() {
		// Nothing needs to be here
	}
	
	@Override
	public boolean isClosed() {
		return false;
	}
}
