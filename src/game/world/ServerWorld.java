package game.world;

import java.util.ArrayList;

import game.action.Action;
import game.ai.AI;
import game.audio.ServerAudioManager;
import game.audio.event.AudioEvent;
import game.net.DummyConnection;
import game.net.IClientConnectionHandler;
import game.net.IServerConnection;
import game.net.IServerConnectionHandler;
import game.render.IRenderer;
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
	
	/** The list of connections to give full updates to */
	private ArrayList<IServerConnection> fullConns = new ArrayList<>();
	
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
		this.fullConns = new ArrayList<>(w.fullConns);
	}
	
	/**
	 * Constructs the world
	 * @param map The map
	 * @param bank The entity bank
	 * @param _ais List of AI controllers
	 * @param _conns The list of connections to clients
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
	public void addConnection(IServerConnection conn) {
		this.conns.add(conn);
		this.fullConns.add(conn);
		conn.setHandler((a) -> {
			EntityBank bank = this.getEntityBank();
			Entity e = bank.getEntity(conn.getPlayerID());
			if (e != null && e instanceof Player)
				((Player) e).handleAction(bank, a);
		});
	}
	
	public EntityBank getEntityBank() {
		return this.bank;
	}

	@Override
	protected void updateStep(double dt) {
		ua.dt = dt;
		ua.bank = bank;
		ua.map = map;
		ua.audio = audio;
		
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
		ua.bank.processCache(conns);
		
		// Send full entity updates
		for (Entity e : this.bank.entities)
			for (IServerConnection conn : fullConns)
				conn.sendUpdateEntity(e);
		fullConns.clear();
	}
	
	@Override
	public ServerWorld clone() {
		return new ServerWorld(this);
	}
	
	
	
}
