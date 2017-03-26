package game.net;

import game.LobbyInfo;
import game.PlayerInfo;
import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.audio.event.AudioEvent;
import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.client.ClientConnection;
import game.net.client.ClientDiscovery;
import game.net.client.IClientConnectionHandler;
import game.net.server.Server;
import game.world.entity.Entity;
import game.world.entity.update.AngleUpdate;
import game.world.entity.update.EntityUpdate;
import game.world.entity.update.TorchLightUpdate;
import game.world.update.WorldUpdate;
import org.junit.*;

import java.util.ArrayList;

import static game.TestUtil.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Tests the connection between the client and server
 */
public class ClientServerConnectionTest {
	private static int ccNum = 0;
	private static int lobNum = 0;
	private static Server server;
	
	@BeforeClass
	public static void setUp() {
		server = new Server();
		server.start();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// This is fine
		}
	}
	
	@AfterClass
	public static void tearDown() {
		server.stop();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// This is fine
		}
	}
	
	private static ClientConnection getTempConnection() throws NameException, ProtocolException {
		return new ClientConnection("temp" + ccNum++);
	}
	
	private static boolean accepted;
	private static String error;
	private static final Object connectWorldLock = new Object();
	
	private static String getTempLobbyName() {
		return "temp" + lobNum++;
	}
	
	/**
	 * Connects a ClientConnection to a lobby
	 */
	private static void createConnectLobby(ClientConnection cc, String lobName) throws ProtocolException {
		synchronized (connectWorldLock) {
			LobbyInfo info = new LobbyInfo(lobName, Util.DEFAULT_MIN_PLAYERS, Util.DEFAULT_MAX_PLAYERS, -1.0f, new PlayerInfo[0]);
			accepted = false;
			error = "";
			cc.setHandler(new IClientConnectionHandler() {
				@Override
				public void handleLobbyCreateAccept() {
					synchronized (connectWorldLock) {
						accepted = true;
						connectWorldLock.notifyAll();
					}
				}
				
				@Override
				public void handleLobbyCreateReject(String reason) {
					synchronized (connectWorldLock) {
						accepted = false;
						error = reason;
						connectWorldLock.notifyAll();
					}
				}
			});
			cc.sendLobbyCreateRequest(info);
			try {
				connectWorldLock.wait(1000);
			} catch (InterruptedException e) {
				// This is fine
			}
			if (!accepted)
				throw new RuntimeException("Lobby creation rejected: " + error);
		}
	}
	
	@Test
	public void discovery() throws NameException, ProtocolException {
		assertThrows(NameException.class, () -> new ClientDiscovery("").tryDiscover(3));
		assertThrows(NameException.class, () -> new ClientDiscovery("zz").tryDiscover(3));
		assertThrows(NameException.class, () -> new ClientDiscovery("toooooooooooloooooooong").tryDiscover(3));
		assertThrows(NameException.class, () -> new ClientDiscovery("inv alid").tryDiscover(3));
		assertThrows(NameException.class, () -> new ClientDiscovery("INVALID").tryDiscover(3));
		ClientDiscovery d1 = new ClientDiscovery("valid");
		d1.tryDiscover(3);
		// Name already in use
		ClientDiscovery d2 = new ClientDiscovery("valid");
		assertThrows(NameException.class, () -> d2.tryDiscover(3));
		d1.getTCP().close();
		d1.getUDP().close();
	}
	
	private class PacketCacheCCH implements IClientConnectionHandler {
		private final ClientConnection cc;
		
		private WorldStart start = null;
		
		private ArrayList<EntityUpdate> updates = new ArrayList<>();
		
		private boolean passedTorch;
		private boolean passedMessage1;
		private boolean passedMessage2;
		
		public PacketCacheCCH(ClientConnection cc) {
			this.cc = cc;
		}
		
		public synchronized boolean hasPassed() {
			if (updates.size() > 0) {
				for (EntityUpdate u : updates) {
					processEntityUpdate(u);
				}
			}
			updates.clear();
			return passedTorch && passedMessage1 && passedMessage2;
		}
		
		private synchronized void processEntityUpdate(EntityUpdate update) {
			if (update instanceof TorchLightUpdate) {
				if (start.playerId == update.getId()) {
					if (!((TorchLightUpdate) update).isOn())
						passedTorch = true;
				}
			}
		}
		
		@Override
		public void processLobbyUpdate(LobbyInfo info) {
			// Don't care
		}
		
		@Override
		public void handleWorldUpdate(WorldUpdate update) {
			// Don't care
		}
		
		@Override
		public void processAudioEvent(AudioEvent ae) {
			// Don't care
		}
		
		@Override
		public void addEntity(Entity e) {
			// Don't care
		}
		
		@Override
		public synchronized void updateEntity(EntityUpdate update) {
			if (start == null)
				updates.add(update);
			else
				processEntityUpdate(update);
		}
		
		@Override
		public void removeEntity(int id) {
			// Don't care
		}
		
		@Override
		public synchronized void handleWorldStart(WorldStart start) {
			this.start = start;
		}
		
		@Override
		public synchronized void handleMessage(String name, String msg) {
			if (name.equals(cc.getName())) {
				if (msg.equals("testmessage1"))
					passedMessage1 = true;
				else if (msg.equals("testmessage2"))
					passedMessage2 = true;
			}
		}
	}
	
	/**
	 * Tests the PacketCache, and also creating a lobby and starting a world
	 * @see PacketCache
	 */
	@Test
	public void packetCache() throws ProtocolException, NameException, InterruptedException {
		PacketCache c = new PacketCache();
		c.sendStringTcp(Protocol.sendMessageToServer("testmessage1"));
		c.sendStringTcp(Protocol.sendMessageToServer("testmessage2"));
		c.sendStringTcp(Protocol.sendAction(new Action(ActionType.TOGGLE_LIGHT)));
		
		Thread.sleep(100);
		ClientConnection cc = getTempConnection();
		createConnectLobby(cc, getTempLobbyName());
		
		// Setup handler
		PacketCacheCCH cch = new PacketCacheCCH(cc);
		cc.setHandler(cch);
		
		// Ready up and start world
		cc.sendStringTcp(Protocol.sendReadyToggle());
		Thread.sleep(Util.LOBBY_WAIT_SECS*1000 + Util.GAME_START_WAIT_SECS*1000 + 1000);
		
		// Send cache
		c.processCache(cc);
		
		// Wait for a bit, then check if we have passed the test
		Thread.sleep(500);
		assertTrue(cch.hasPassed());
		cc.close();
	}
	
	
}
