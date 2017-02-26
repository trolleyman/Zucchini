package game.net.server;

import java.util.ArrayList;

import game.Util;
import game.world.ServerWorld;

public class GameServer implements Runnable {
	private ServerWorld world;
	private boolean running = false;
	
	public GameServer(ServerWorld _world, ArrayList<IServerConnection> conns) {
		this.world = _world;
		for (IServerConnection conn : conns)
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
				System.err.println("Warning: Sleep Thread Interrupted: " + e.toString());
			}
		}
	}
}
