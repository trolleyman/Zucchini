package game.world;

import java.util.ArrayList;

import game.action.Action;
import game.audio.ServerAudioManager;
import game.audio.event.AudioEvent;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.net.WorldStart;
import game.net.server.ClientHandler;
import game.net.server.ClientInfo;
import game.net.server.LobbyClient;
import game.world.entity.Entity;
import game.world.entity.Player;
import game.world.entity.weapon.Handgun;
import game.world.map.Map;
import org.joml.Vector2f;

/**
 * The world located on the server
 * 
 * @author Callum
 */
public class ServerWorld extends World implements Cloneable {
	/** Cached UpdateArgs object */
	private UpdateArgs ua = new UpdateArgs(0.0, null, null, null);
	
	/** Server Audio Manager */
	private ServerAudioManager audio;
	
	/** The clients */
	private ArrayList<ServerWorldClient> clients = new ArrayList<>();
	
	/**
	 * Clones a ServerWorld
	 */
	public ServerWorld(ServerWorld w) {
		super(w.map, new EntityBank(w.bank));
		
		this.audio = w.audio;
		
		this.clients = w.clients;
	}
	
	/**
	 * Constructs the world
	 * @param map The map
	 * @param bank The entity bank
	 */
	public ServerWorld(Map map, EntityBank bank) {
		super(map, bank);
		
		this.audio = new ServerAudioManager();
	}
	
	/**
	 * Adds the client specified to the world
	 * @param c The client
	 */
	public synchronized void addClient(LobbyClient c) {
		Player player = new Player(c.team, map.getSpawnLocation(c.team), new Handgun(new Vector2f()));
		this.bank.addEntity(player);
		this.clients.add(new ServerWorldClient(c.handler, player.getId()));
		try {
			c.handler.sendStringTcp(Protocol.sendWorldStart(new WorldStart(map, player.getId())));
		} catch (ProtocolException e) {
			// This is ok as the handler will take care of it
		}
	}
	
	public synchronized void handleAction(String name, Action a) {
		for (ServerWorldClient swc : clients) {
			if (swc.handler.getClientInfo().name.equals(name)) {
				Entity e = bank.getEntity(swc.playerId);
				if (e != null && e instanceof Player) {
					Player p = (Player) e;
					p.handleAction(bank, a);
					break;
				}
				System.err.println("Warning: Could not find player with ID " + swc.playerId);
				break;
			}
		}
	}
	
	/**
	 * Handle a full update request for the specified client
	 * @param name The name of the client
	 */
	public synchronized void handleFullUpdateRequest(String name) {
		for (ServerWorldClient swc : clients) {
			if (swc.handler.getClientInfo().name.equals(name)) {
				swc.fullUpdate = true;
				break;
			}
		}
	}
	
	/**
	 * Removes the client with the name specified
	 * @param name The name
	 */
	public synchronized void removeClient(String name) {
		for (int i = 0; i < clients.size(); i++) {
			ServerWorldClient swc = clients.get(i);
			
			if (swc.handler.getClientInfo().name.equals(name)) {
				clients.remove(i);
				bank.removeEntityCached(swc.playerId);
				break;
			}
		}
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
		this.bank.processCache(clients);
		
		// Update entities
		for (Entity e : this.bank.entities) {
			e.update(ua);
		}
		
		// Send audio
		for (AudioEvent ae : audio.clearCache()) {
			for (ServerWorldClient c : clients) {
				if (c.handler.isClosed())
					continue;
				
				try {
					c.handler.getClientInfo().tcpConn.sendString(Protocol.sendAudioEvent(ae));
				} catch (ProtocolException ex) {
					// This is ok as ClientHandler takes care of this
				}
			}
		}
		
		// Send entity updates
		this.bank.processCache(clients);
		
		// *DING-DONG* *DING-DONG* bring out yer dead
		for (Entity e : this.bank.entities) {
			if (e.getHealth() <= 0.0f) {
				e.death(ua);
				this.bank.removeEntityCached(e.getId());
			}
		}
		
		// Send full updates to the clients who need it
		for (ServerWorldClient swc : clients) {
			if (swc.handler.isClosed() || !swc.fullUpdate)
				continue;
			swc.fullUpdate = false;
			
			for (Entity e : this.bank.entities) {
				try {
					swc.handler.sendStringTcp(Protocol.sendAddEntity(e));
				} catch (ProtocolException ex) {
					// This is ok as ClientHandler takes care of this
					break;
				}
			}
		}
	}
	
	@Override
	public ServerWorld clone() {
		return new ServerWorld(this);
	}
}
