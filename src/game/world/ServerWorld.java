package game.world;

import java.util.ArrayList;

import game.action.Action;
import game.audio.ServerAudioManager;
import game.audio.event.AudioEvent;
import game.net.IServerConnection;
import game.net.IServerConnectionHandler;
import game.world.entity.Entity;
import game.world.entity.Player;
import game.world.map.Map;
import game.world.physics.PhysicsWorld;

/**
 * The world located on the server
 * 
 * @author Callum
 */
public class ServerWorld extends World implements Cloneable {
	/** Cached UpdateArgs object */
	private UpdateArgs ua = new UpdateArgs(0.0, null, null, null, null);
	
	/** Server Audio Manager */
	private ServerAudioManager audio;
	
	/** The connections to the clients */
	private ArrayList<IServerConnection> conns = new ArrayList<>();
	private ArrayList<IServerConnection> fullUpdateRequests = new ArrayList<>();
	
	/**
	 * Clones a ServerWorld
	 */
	public ServerWorld(ServerWorld w) {
		super(w.map, new EntityBank(w.bank), new PhysicsWorld(w.physics));
		
		this.audio = w.audio;
		
		this.conns = w.conns;
		this.fullUpdateRequests = w.fullUpdateRequests;
	}
	
	/**
	 * Constructs the world
	 * @param map The map
	 * @param bank The entity bank
	 */
	public ServerWorld(Map map, EntityBank bank, PhysicsWorld physics) {
		super(map, bank, physics);
		
		this.audio = new ServerAudioManager();
	}
	
	/**
	 * Adds a connection to the server
	 * @param conn The connection
	 */
	public synchronized void addConnection(IServerConnection conn) {
		if (this.conns.contains(conn)) {
			System.err.println("Warning: Connection already added");
			return;
		}
		this.conns.add(conn);
		ServerWorld that = this;
		conn.setHandler(new IServerConnectionHandler() {
			@Override
			public void handleAction(Action a) {
				synchronized (that) {
					EntityBank bank = that.getEntityBank();
					Entity e = bank.getEntity(conn.getPlayerID());
					if (e != null && e instanceof Player)
						((Player) e).handleAction(bank, a);
				}
			}
			
			@Override
			public void handleFullUpdateRequest() {
				synchronized (that) {
					if (!that.fullUpdateRequests.contains(conn))
						that.fullUpdateRequests.add(conn);
				}
			}
		});
		
		// Send a full update of the world
		if (!this.fullUpdateRequests.contains(conn))
			this.fullUpdateRequests.add(conn);
	}
	
	public EntityBank getEntityBank() {
		return this.bank;
	}
	
	@Override
	protected synchronized void updateStep(double dt) {
		ua.dt = dt;
		ua.bank = bank;
		ua.physics = physics;
		ua.map = map;
		ua.audio = audio;
		
		// Ensure that no entity updates are left out
		this.bank.processCache(conns);
		
		// Update entities
		for (Entity e : this.bank.entities) {
			e.update(ua);
		}
		
		// Send audio
		for (AudioEvent ae : audio.clearCache())
			for (IServerConnection conn : conns)
				conn.sendAudioEvent(ae);
		
		// Send entity updates
		this.bank.processCache(conns);
		
		// *DING-DONG* *DING-DONG* bring out yer dead
		for (Entity e : this.bank.entities) {
			if (e.getHealth() <= 0.0f) {
				e.death(ua);
				this.bank.removeEntityCached(e.getId());
			}
		}
		
		// Send full updates to those who need it
		for (IServerConnection conn : fullUpdateRequests)
			for (Entity e : this.bank.entities)
				conn.sendAddEntity(e);
		fullUpdateRequests.clear();
		
		physics.clean();
	}
	
	@Override
	public ServerWorld clone() {
		return new ServerWorld(this);
	}
}
