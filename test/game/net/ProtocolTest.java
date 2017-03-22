package game.net;

import game.LobbyInfo;
import game.PlayerInfo;
import game.action.Action;
import game.action.AimAction;
import game.audio.event.AudioEvent;
import game.audio.event.AudioStopEvent;
import game.exception.ProtocolException;
import game.world.entity.Entity;
import game.world.entity.HumanPlayer;
import game.world.entity.update.EntityUpdate;
import game.world.entity.update.HealthUpdate;
import game.world.map.Map;
import game.world.map.Wall;
import game.world.update.StartTimeWorldUpdate;
import game.world.update.WorldUpdate;
import org.joml.Vector2f;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProtocolTest {
	@Test
	public void sendTcpConnectionRequest() throws ProtocolException {
		String msg = Protocol.sendTcpConnectionRequest("test123", 456);
		assertTrue(Protocol.isTcpConnectionRequest(msg));
		Tuple<String, Integer> t = Protocol.parseTcpConnectionRequest(msg);
		assertEquals("test123", t.getFirst());
		assertEquals(456, (int)t.getSecond());
	}
	
	@Test
	public void sendTcpConnectionResponseAccept() throws ProtocolException {
		String msg = Protocol.sendTcpConnectionResponseAccept();
		assertTrue(Protocol.isTcpConnectionResponseAccept(msg));
	}
	
	@Test
	public void sendTcpConnectionResponseReject() throws ProtocolException {
		String msg = Protocol.sendTcpConnectionResponseReject("reason");
		assertTrue(Protocol.isTcpConnectionResponseReject(msg));
		assertEquals("reason", Protocol.parseTcpConnectionResponseReject(msg));
	}
	
	@Test
	public void sendAction() throws ProtocolException {
		AimAction a1 = new AimAction(10.0f);
		String msg = Protocol.sendAction(a1);
		assertTrue(Protocol.isAction(msg));
		Action a2 = Protocol.parseAction(msg);
		assertEquals(a2.getType(), a1.getType());
		assertTrue(a2 instanceof AimAction);
		assertEquals(((AimAction)a2).getAngle(), a1.getAngle(), 0.0001f);
	}
	
	@Test
	public void sendAddEntity() throws ProtocolException {
		HumanPlayer e1 = new HumanPlayer(1, new Vector2f(2.0f, 3.0f), "", null);
		String msg = Protocol.sendAddEntity(e1);
		assertTrue(Protocol.isAddEntity(msg));
		Entity e2 = Protocol.parseAddEntity(msg);
		assertTrue(e2 instanceof HumanPlayer);
		HumanPlayer e3 = (HumanPlayer) e2;
		assertEquals(e1.getId(), e3.getId());
		assertEquals(e1.getName(), e3.getName());
		assertEquals(e1.getHeldItem(), e3.getHeldItem());
	}
	
	@Test
	public void sendUpdateEntity() throws ProtocolException {
		HealthUpdate u1 = new HealthUpdate(2, 10.0f);
		String msg = Protocol.sendUpdateEntity(u1);
		assertTrue(Protocol.isUpdateEntity(msg));
		EntityUpdate u2 = Protocol.parseUpdateEntity(msg);
		assertTrue(u2 instanceof HealthUpdate);
		HealthUpdate u3 = (HealthUpdate) u2;
		assertEquals(u1.getId(), u3.getId());
		assertEquals(u1.getHealth(), u3.getHealth(), 0.00001);
	}
	
	@Test
	public void sendRemoveEntity() throws ProtocolException {
		int id1 = 10;
		String msg = Protocol.sendRemoveEntity(id1);
		assertTrue(Protocol.isRemoveEntity(msg));
		int id2 = Protocol.parseRemoveEntity(msg);
		assertEquals(id1, id2);
	}
	
	@Test
	public void sendAudioEvent() throws ProtocolException {
		AudioStopEvent e1 = new AudioStopEvent(10);
		String msg = Protocol.sendAudioEvent(e1);
		assertTrue(Protocol.isAudioEvent(msg));
		AudioEvent e2 = Protocol.parseAudioEvent(msg);
		assertTrue(e2 instanceof AudioStopEvent);
		AudioStopEvent e3 = (AudioStopEvent) e2;
		assertEquals(e1.id, e3.id);
	}
	
	@Test
	public void sendLobbiesRequest() throws ProtocolException {
		String msg = Protocol.sendLobbiesRequest();
		assertTrue(Protocol.isLobbiesRequest(msg));
	}
	
	private void assertLobbyInfoEquals(LobbyInfo l1, LobbyInfo l2) {
		assertEquals(l1.lobbyName, l2.lobbyName);
		assertEquals(l1.minPlayers, l2.minPlayers);
		assertEquals(l1.maxPlayers, l2.maxPlayers);
		assertEquals(l1.countdownTime, l2.countdownTime, 0.00001);
		
		if (l1.players == null && l2.players == null)
			return;
		
		assertEquals(l1.players.length, l2.players.length);
		for (int j = 0; j < l1.players.length; j++) {
			PlayerInfo p1 = l1.players[j];
			PlayerInfo p2 = l2.players[j];
			assertEquals(p1.name, p2.name);
			assertEquals(p1.team, p2.team);
			assertEquals(p1.ready, p2.ready);
		}
	}
	
	@Test
	public void sendLobbiesResponse() throws ProtocolException {
		ArrayList<LobbyInfo> ls1 = new ArrayList<>();
		ls1.add(new LobbyInfo("test1", 2, 5, 1.0f, new PlayerInfo[]{
				new PlayerInfo("p1", 1, false),
				new PlayerInfo("p2", 2, true),
				new PlayerInfo("p3", 3, true),
		}));
		ls1.add(new LobbyInfo("test2", 3, 6, 2.0f, new PlayerInfo[]{
				new PlayerInfo("p4", 10, true),
				new PlayerInfo("p5", 2, false),
		}));
		String msg = Protocol.sendLobbiesResponse(ls1);
		assertTrue(Protocol.isLobbiesResponse(msg));
		ArrayList<LobbyInfo> ls2 = Protocol.parseLobbiesResponse(msg);
		assertEquals(ls1.size(), ls2.size());
		for (int i = 0; i < ls1.size(); i++) {
			LobbyInfo l1 = ls1.get(i);
			LobbyInfo l2 = ls2.get(i);
			assertLobbyInfoEquals(l1, l2);
		}
	}
	
	@Test
	public void sendFullUpdateRequest() throws ProtocolException {
		String msg = Protocol.sendFullUpdateRequest();
		assertTrue(Protocol.isFullUpdateRequest(msg));
	}
	
	@Test
	public void sendDiscoveryRequest() throws ProtocolException {
		String msg = Protocol.sendDiscoveryRequest();
		assertTrue(Protocol.isDiscoveryRequest(msg));
	}
	
	@Test
	public void sendDiscoveryResponse() throws ProtocolException {
		String msg = Protocol.sendDiscoveryResponse();
		assertTrue(Protocol.isDiscoveryResponse(msg));
	}
	
	@Test
	public void sendLobbyJoinRequest() throws ProtocolException {
		String msg = Protocol.sendLobbyJoinRequest("test12");
		assertTrue(Protocol.isLobbyJoinRequest(msg));
		assertEquals("test12", Protocol.parseLobbyJoinRequest(msg));
	}
	
	@Test
	public void sendLobbyJoinAccept() throws ProtocolException {
		String msg = Protocol.sendLobbyJoinAccept();
		assertTrue(Protocol.isLobbyJoinAccept(msg));
	}
	
	@Test
	public void sendLobbyJoinReject() throws ProtocolException {
		String msg = Protocol.sendLobbyJoinReject("reason123");
		assertTrue(Protocol.isLobbyJoinReject(msg));
		assertEquals("reason123", Protocol.parseLobbyJoinReject(msg));
	}
	
	@Test
	public void sendLobbyUpdate() throws ProtocolException {
		LobbyInfo l1 = new LobbyInfo("testtt", 1, 5, 11.0f, new PlayerInfo[]{
				new PlayerInfo("p10", 10, true),
				new PlayerInfo("p11", 11, true),
				new PlayerInfo("p12", 12, true)
		});
		String msg = Protocol.sendLobbyUpdate(l1);
		assertTrue(Protocol.isLobbyUpdate(msg));
		LobbyInfo l2 = Protocol.parseLobbyUpdate(msg);
		assertLobbyInfoEquals(l1, l2);
	}
	
	@Test
	public void sendLobbyLeaveRequest() throws ProtocolException {
		String msg = Protocol.sendLobbyLeaveRequest();
		assertTrue(Protocol.isLobbyLeaveRequest(msg));
	}
	
	@Test
	public void sendLobbyLeaveNotify() throws ProtocolException {
		String msg = Protocol.sendLobbyLeaveNotify();
		assertTrue(Protocol.isLobbyLeaveNotify(msg));
	}
	
	@Test
	public void sendLobbyCreateRequest() throws ProtocolException {
		LobbyInfo l1 = new LobbyInfo("lobby123", 6, 7, 13.0f, null);
		String msg = Protocol.sendLobbyCreateRequest(l1);
		assertTrue(Protocol.isLobbyCreateRequest(msg));
		assertLobbyInfoEquals(l1, Protocol.parseLobbyCreateRequest(msg));
	}
	
	@Test
	public void sendLobbyCreateAccept() throws ProtocolException {
		String msg = Protocol.sendLobbyCreateAccept();
		assertTrue(Protocol.isLobbyCreateAccept(msg));
	}
	
	@Test
	public void sendLobbyCreateReject() throws ProtocolException {
		String msg = Protocol.sendLobbyCreateReject("reason123");
		assertTrue(Protocol.isLobbyCreateReject(msg));
		assertEquals("reason123", Protocol.parseLobbyCreateReject(msg));
	}
	
	@Test
	public void sendReadyToggle() throws ProtocolException {
		String msg = Protocol.sendReadyToggle();
		assertTrue(Protocol.isReadyToggle(msg));
	}
	
	@Test
	public void sendWorldStart() throws ProtocolException {
		ArrayList<Wall> walls = new ArrayList<>();
		walls.add(new Wall(0.0f, 1.0f, 2.0f, 3.0f));
		WorldStart ws1 = new WorldStart(new Map(walls, 10.0f, 1), 10);
		String msg = Protocol.sendWorldStart(ws1);
		assertTrue(Protocol.isWorldStart(msg));
		WorldStart ws2 = Protocol.parseWorldStart(msg);
		assertEquals(ws1.playerId, ws2.playerId);
		assertEquals(ws1.map.getRect(), ws2.map.getRect());
		assertEquals(ws1.map.walls.size(), ws2.map.walls.size());
		
		for (int i = 0; i < ws1.map.walls.size(); i++) {
			Wall w1 = ws1.map.walls.get(i);
			Wall w2 = ws2.map.walls.get(i);
			assertEquals(w1.p0, w2.p0);
			assertEquals(w1.p1, w2.p1);
		}
	}
	
	@Test
	public void sendWorldUpdate() throws ProtocolException {
		StartTimeWorldUpdate u1 = new StartTimeWorldUpdate(23.0f);
		String msg = Protocol.sendWorldUpdate(u1);
		assertTrue(Protocol.isWorldUpdate(msg));
		WorldUpdate u2 = Protocol.parseWorldUpdate(msg);
		assertTrue(u2 instanceof StartTimeWorldUpdate);
		StartTimeWorldUpdate u3 = (StartTimeWorldUpdate) u2;
		assertEquals(u1.getStartTime(), u3.getStartTime(), 0.0001);
	}
	
	@Test
	public void sendMessageToServer() throws ProtocolException {
		String msg = Protocol.sendMessageToServer("aaa");
		assertTrue(Protocol.isMessageToServer(msg));
		assertEquals("aaa", Protocol.parseMessageToServer(msg));
	}
	
	@Test
	public void sendMessageToClient() throws ProtocolException {
		String msg = Protocol.sendMessageToClient("name", "aaa2");
		assertTrue(Protocol.isMessageToClient(msg));
		Tuple<String, String> cmsg = Protocol.parseMessageToClient(msg);
		assertEquals("name", cmsg.getFirst());
		assertEquals("aaa2", cmsg.getSecond());
	}
}