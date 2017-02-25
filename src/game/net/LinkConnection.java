package game.net;

import game.action.Action;
import game.audio.event.AudioEvent;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

/**
 * A dummy class to implement {@link IClientConnection} and {@link IServerConnection} so that we can
 * play the game and prototype aspects of it without having to implement networking atm.
 * 
 * @author Callum
 */
public class LinkConnection implements IServerConnection, IClientConnection {
	private IServerConnectionHandler sch = new DummyServerConnectionHandler();
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
		sch.handleAction(a);
	}
	
	@Override
	public void requestFullUpdate() {
		sch.handleFullUpdateRequest();
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
	public int getPlayerID() {
		return playerID;
	}
	
	@Override
	public void close() {
		// Nothing needs to be here
	}
}
