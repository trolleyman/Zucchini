package game.net.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class is to show that by default, IClientConnectionHandler doesn't throw exceptions
 */
public class IClientConnectionHandlerTest {
	private IClientConnectionHandler cch;
	
	@Before
	public void setUp() {
		cch = new IClientConnectionHandler() {};
	}
	
	@Test
	public void addEntity() throws Exception {
		cch.addEntity(null);
	}
	
	@Test
	public void updateEntity() throws Exception {
		cch.updateEntity(null);
	}
	
	@Test
	public void removeEntity() throws Exception {
		cch.removeEntity(0);
	}
	
	@Test
	public void processAudioEvent() throws Exception {
		cch.processAudioEvent(null);
	}
	
	@Test
	public void processLobbyUpdate() throws Exception {
		cch.processLobbyUpdate(null);
	}
	
	@Test
	public void handleLobbyJoinAccept() throws Exception {
		cch.handleLobbyJoinAccept();
	}
	
	@Test
	public void handleLobbyJoinReject() throws Exception {
		cch.handleLobbyJoinReject("");
	}
	
	@Test
	public void handleLobbyCreateAccept() throws Exception {
		cch.handleLobbyCreateAccept();
	}
	
	@Test
	public void handleLobbyCreateReject() throws Exception {
		cch.handleLobbyCreateReject("");
	}
	
	@Test
	public void handleWorldStart() throws Exception {
		cch.handleWorldStart(null);
	}
	
	@Test
	public void handleLobbyLeaveNotify() throws Exception {
		cch.handleLobbyLeaveNotify();
	}
	
	@Test
	public void handleWorldUpdate() throws Exception {
		cch.handleWorldUpdate(null);
	}
	
	@Test
	public void handleMessage() throws Exception {
		cch.handleMessage("", "");
	}
}