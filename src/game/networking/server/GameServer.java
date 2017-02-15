package game.networking.server;

import game.LobbyInfo;
import game.Util;
import game.action.Action;
import game.net.IServerConnection;
import game.net.IServerConnectionHandler;
import game.world.ServerWorld;
import game.world.entity.Entity;
import game.world.entity.Player;

import java.util.ArrayList;

public class GameServer extends ServerAbstract implements Runnable {
	private IGetLobbies lobbyInterface;
	private ServerWorld world;
	
	private boolean running = false;
	
	public GameServer(ServerWorld _world, ArrayList<IServerConnection> conns, IGetLobbies _lobbyInterface) {
		this.world = _world;
		this.lobbyInterface = _lobbyInterface;
		for (IServerConnection conn : conns) {
			this.addConnection(conn);
		}
	}
	
	public void addConnection(IServerConnection conn) {
		conn.setHandler(new IServerConnectionHandler() {
			@Override
			public void handleAction(Action a) {
				Entity e = world.getEntityBank().getEntity(conn.getPlayerID());
				if (e != null && e instanceof Player)
					((Player) e).handleAction(world.getEntityBank(), a);
			}
			
			@Override
			public ArrayList<LobbyInfo> getLobbies() { return lobbyInterface.getLobbies(); }
		});
		this.world.addConnection(conn);
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
			
			this.world.update(dt);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// This is fine
			}
		}
	}
	
	public void close() {
		this.running = false;
	}
}
