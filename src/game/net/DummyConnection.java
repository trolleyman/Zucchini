package game.net;

import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.audio.event.AudioEvent;
import game.ui.UI;
import game.world.ClientWorld;
import game.world.EntityBank;
import game.world.ServerWorld;
import game.world.World;
import game.world.entity.Entity;
import game.world.entity.Player;

/**
 * A dummy class to implement {@link IClientConnection} and {@link IServerConnection} so that we can
 * play the game and prototype aspects of it without having to implement networking atm.
 * 
 * @author Callum
 */
public class DummyConnection implements IServerConnection, IClientConnection {
	private IServerConnectionHandler sch = new DummyServerConnectionHandler();
	private IClientConnectionHandler cch = new DummyClientConnectionHandler();
	
	private int playerID;
	
	/**
	 * Constructs a new {@link DummyConnection}.
	 */
	public DummyConnection(int _playerID) {
		this.playerID = _playerID;
	}
	
	@Override
	public void sendAction(Action a) {
		sch.handleAction(a);
	}
	
	@Override
	public void setHandler(IClientConnectionHandler _cch) {
		this.cch = _cch;
	}
	
	@Override
	public void sendUpdateEntity(Entity e) {
		cch.updateEntity(e);
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
	}
	
	@Override
	public int getPlayerID() {
		return playerID;
	}
	
	@Override
	public void close() {
		// Nothing needs to be here
	}
}
