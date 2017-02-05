package game.net;

import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.ui.UI;
import game.world.ClientWorld;
import game.world.EntityBank;
import game.world.ServerWorld;
import game.world.World;
import game.world.entity.Entity;
import game.world.entity.Player;

/**
 * A dummy class to implement {@link IClientConnection} so that we can play the game and prototype aspects of it
 * without having to implement networking atm.
 * 
 * @author Callum
 */
public class DummyConnection extends Thread implements IClientConnection {
	private ServerWorld serverWorld;
	
	private int playerID;
	
	private IClientConnectionHandler cch = new DummyClientConnectionHandler();

	private boolean running;
	
	/**
	 * Constructs a new {@link DummyConnection}.
	 * @param _serverWorld The server world
	 * @param _playerID The player id to receive actions from the connection.
	 */
	public DummyConnection(ServerWorld _serverWorld, int _playerID) {
		this.serverWorld = _serverWorld;
		this.playerID = _playerID;
	}
	
	@Override
	public void sendAction(Action a) {
		EntityBank bank = this.serverWorld.getEntityBank();
		Entity e = bank.getEntity(playerID);
		if (e != null && e instanceof Player)
			((Player) e).handleAction(bank, a);
	}
	
	@Override
	public void setHandler(IClientConnectionHandler _cch) {
		this.cch = _cch;
	}
	
	@Override
	public void run() {
		this.running = true;
		
		long prevTime = System.nanoTime();
		while (this.running) {
			long now = System.nanoTime();
			long dtNanos = now - prevTime;
			prevTime = now;
			double dt = (double)dtNanos / Util.NANOS_PER_SECOND;
			
			ServerWorld prevWorld = (ServerWorld) this.serverWorld.clone();
			this.serverWorld.update(dt);
			this.serverWorld.send(prevWorld, cch);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				System.err.println("Warning: Sleep Thread Interrupted: " + e.toString());
			}
		}
	}
	
	@Override
	public void close() {
		this.running = false;
	}
}
