package game.net.server;

import game.Util;
import game.net.LinkConnection;
import game.world.ServerWorld;

public class LobbyServer implements Runnable {
	private ServerWorld world;
	private boolean running = false;
	
	public LobbyServer(ServerWorld _world) {
		this.world = _world;
	}
	
	public void addConnection(LinkConnection conn) {
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
	
	public void close() {
		this.running = false;
	}
}
