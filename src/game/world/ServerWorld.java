package game.world;

import java.util.ArrayList;

import game.action.Action;
import game.ai.AIPlayer;
import game.audio.ServerAudioManager;
import game.audio.event.AudioEvent;
import game.exception.ProtocolException;
import game.net.PacketCache;
import game.net.Protocol;
import game.net.WorldStart;
import game.net.server.LobbyClient;
import game.world.entity.Entity;
import game.world.entity.HumanPlayer;
import game.world.entity.Player;
import game.world.map.Map;
import game.world.update.EndTimeWorldUpdate;
import game.world.update.ScoreboardWorldUpdate;
import game.world.update.StartFinishingWorldUpdate;
import game.world.update.StartTimeWorldUpdate;

/**
 * The world located on the server
 * 
 * @author Callum
 */
public class ServerWorld extends World implements Cloneable {
	/** Time in seconds between start time updates */
	private static final float START_TIME_UPDATE_DT = 0.5f;
	/** Time in seconds between end time updates */
	private static final float END_TIME_UPDATE_DT = 0.5f;
	
	/** Cached UpdateArgs object */
	private UpdateArgs ua = new UpdateArgs(0.0, null, null, null, new PacketCache(), null);
	
	/** Server Audio Manager */
	private ServerAudioManager audio;
	
	/** The clients */
	private ArrayList<ServerWorldClient> clients = new ArrayList<>();
	
	/** The time since the last startTime update */
	private float startTimeUpdate;
	
	/** The time since the last endTime update */
	private float endTimeUpdate;
	
	/**
	 * Constructs the world
	 * @param map The map
	 * @param bank The entity bank
	 */
	public ServerWorld(Map map, EntityBank bank) {
		super(map, bank);
		
		this.audio = new ServerAudioManager();
		
		for (Entity e : map.getInitialEntities()) {
			this.bank.addEntityCached(e);
		}
		
		this.startTimeUpdate = 0.0f;
		this.endTimeUpdate = 0.0f;
	}
	
	/**
	 * Should the world start the finishing phase of the game?
	 */
	protected boolean shouldStartFinishing() {
		if (startTime != 0.0f)
			return false;
		
		// Count alive players
		int numAlive = 0;
		for (PlayerScoreboardInfo p : scoreboard.getPlayers()) {
			if (!p.dead)
				numAlive++;
		}
		return numAlive <= 1;
	}
	
	/**
	 * Adds the client specified to the world
	 * @param c The client
	 */
	public synchronized void addClient(LobbyClient c) {
		Player player = new HumanPlayer(c.team, map.getSpawnLocation(c.team), c.handler.getClientInfo().name, Player.getDefaultHeldItem());
		this.bank.addEntity(player);
		this.clients.add(new ServerWorldClient(c.handler, player.getId()));
		this.scoreboard.addPlayer(c.handler.getClientInfo().name);
		try {
			c.handler.sendStringTcp(Protocol.sendWorldStart(new WorldStart(map, player.getId())));
		} catch (ProtocolException e) {
			c.handler.error(e);
		}
	}
	
	/**
	 * Adds an AI to the world
	 * @param ai The AI player
	 */
	public synchronized void addAI(AIPlayer ai) {
		this.bank.addEntity(ai);
		this.scoreboard.addPlayer(ai.getName());
	}
	
	public synchronized void handleAction(String name, Action a) {
		for (ServerWorldClient swc : clients) {
			if (swc.handler.getClientInfo().name.equals(name)) {
				Entity e = bank.getEntity(swc.playerId);
				if (e != null && e instanceof Player) {
					Player p = (Player) e;
					if (ua.bank != null)
						p.handleAction(ua, a);
					break;
				}
				//System.err.println("Warning: Could not find player with ID " + swc.playerId);
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
				String msg = scoreboard.getPlayer(name).dead ? name + " ragequit." : name + " left.";
				ua.packetCache.sendStringTcp(Protocol.sendMessageToClient("", msg));
				clients.remove(i);
				bank.removeEntityCached(swc.playerId);
				// Don't remove from scoreboard
				// scoreboard.removePlayer(name);
				break;
			}
		}
	}
	
	@Override
	protected synchronized void updateStep(double dt) {
		if (this.startTime != 0.0f) {
			this.startTimeUpdate += dt;
			
			if (this.startTimeUpdate > START_TIME_UPDATE_DT) {
				this.startTimeUpdate = 0.0f;
				String s = Protocol.sendWorldUpdate(new StartTimeWorldUpdate(this.startTime));
				for (ServerWorldClient c : clients) {
					try {
						c.handler.sendStringTcp(s);
					} catch (ProtocolException e) {
						c.handler.error(e);
					}
				}
			}
		}
		if (isFinishing()) {
			this.endTimeUpdate += dt;
			
			if (this.endTimeUpdate > END_TIME_UPDATE_DT || this.endTime == 0.0f) {
				this.endTimeUpdate = 0.0f;
				String s = Protocol.sendWorldUpdate(new EndTimeWorldUpdate(this.endTime));
				for (ServerWorldClient c : clients) {
					try {
						c.handler.sendStringTcp(s);
					} catch (ProtocolException e) {
						c.handler.error(e);
					}
				}
			}
		} else if (shouldStartFinishing()) {
			// Send start finishing packet
			for (ServerWorldClient swc : clients) {
				try {
					swc.handler.getClientInfo().tcpConn.sendString(Protocol.sendWorldUpdate(new StartFinishingWorldUpdate()));
				} catch (ProtocolException e) {
					swc.handler.error(e);
				}
			}
			startFinishing();
		}
		
		// If scoreboard is dirty, send update
		if (scoreboard.dirty) {
			for (ServerWorldClient swc : clients) {
				try {
					swc.handler.getClientInfo().tcpConn.sendString(Protocol.sendWorldUpdate(new ScoreboardWorldUpdate(scoreboard)));
				} catch (ProtocolException e) {
					swc.handler.error(e);
				}
			}
			scoreboard.dirty = false;
		}
		
		if (!this.isPaused()) {
			ua.dt = dt;
			ua.bank = bank;
			ua.map = map;
			ua.audio = audio;
			ua.scoreboard = scoreboard;
			
			// Ensure that no entity updates are left out
			this.bank.processCache(clients);
			ua.packetCache.processCache(clients);
			
			// Update entities
			for (Entity e : this.bank.entities.values()) {
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
						c.handler.error(ex);
					}
				}
			}
			
			// Send entity updates
			this.bank.processCache(clients);
			
			// *DING-DONG* *DING-DONG* bring out yer dead
			for (Entity e : this.bank.entities.values()) {
				if (e.getHealth() <= 0.0f) {
					e.death(ua);
					this.bank.removeEntityCached(e.getId());
				}
			}
		}
		
		// Send full updates to the clients who need it
		for (ServerWorldClient swc : clients) {
			if (swc.handler.isClosed() || !swc.fullUpdate)
				continue;
			swc.fullUpdate = false;
			
			for (Entity e : this.bank.entities.values()) {
				try {
					swc.handler.sendStringTcp(Protocol.sendAddEntity(e));
				} catch (ProtocolException ex) {
					swc.handler.error(ex);
					break;
				}
			}
		}
	}
}
