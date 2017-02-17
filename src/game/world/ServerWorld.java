package game.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import game.action.Action;
import game.ai.AI;
import game.audio.ServerAudioManager;
import game.audio.event.AudioEvent;
import game.net.IServerConnection;
import game.net.IServerConnectionHandler;
import game.world.entity.Entity;
import game.world.entity.Player;
import game.world.map.Map;

/**
 * The world located on the server
 * 
 * @author Callum
 */
public class ServerWorld extends World implements Cloneable {
	/** Cached UpdateArgs object */
	private UpdateArgs ua = new UpdateArgs(0.0, null, null, null);
	
	/** The AIs/Players in the world. */
	private ArrayList<AI> ais;
	
	/** Server Audio Manager */
	private ServerAudioManager audio;
	
	/** The connections to the clients */
	private ArrayList<IServerConnection> conns = new ArrayList<>();
	private ArrayList<IServerConnection> fullUpdateRequests = new ArrayList<>();
	
	/**
	 * Clones a ServerWorld
	 */
	public ServerWorld(ServerWorld w) {
		super(w.map, new EntityBank(w.bank));
		
		// Clone & update AIs
		this.ais = new ArrayList<>();
		for (AI ai : w.ais) {
			this.ais.add(ai.clone());
		}
		
		this.audio = w.audio;
		
		this.conns = w.conns;
		this.fullUpdateRequests = w.fullUpdateRequests;
	}
	
	/**
	 * Constructs the world
	 * @param map The map
	 * @param bank The entity bank
	 * @param _ais List of AI controllers
	 */
	public ServerWorld(Map map, EntityBank bank, ArrayList<AI> _ais) {
		super(map, bank);
		
		this.ais = _ais;
		
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
		ua.map = map;
		ua.audio = audio;
		
		// Ensure that no entity updates are left out
		this.bank.processCache(conns);
		
		// Update entities
		for (Entity e : this.bank.entities) {
			e.update(ua);
		}
		
		// Update AI/players
		for (AI ai : ais) {
			ai.update(ua);
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
	}
	
	@Override
	public ServerWorld clone() {
		return new ServerWorld(this);
	}
}
