package game.ui;

import game.Util;
import game.net.server.Server;
import game.Client;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Runs through some use cases for the UI,
 * and ensures that that all buttons lead to the right place
 */
public class UIRunthroughTest {
	private static Server server;
	
	private Client c;
	private Thread t;
	
	private final Object notifyEndResult = new Object();
	private boolean ended;
	private boolean failed;
	private final Object notifyClientLock = new Object();
	
	@BeforeClass
	public static void setUp() throws InterruptedException {
		Thread.sleep(1000);
		server = new Server();
		server.start();
		Thread.sleep(1000);
	}
	
	@AfterClass
	public static void tearDown() {
		server.stop();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// This is fine
		}
	}
	
	private void runClient() {
		c = new Client(false);
		synchronized (notifyClientLock) {
			notifyClientLock.notifyAll();
		}
		c.run();
	}
	
	/**
	 * Setup to be at ConnectUI with the specified strings in the {@link game.ui.component.TextEntryComponent}s.
	 */
	private void setup(String address, String name) throws InterruptedException {
		Thread.sleep(500);
		assertEquals(ConnectUI.class, c.getUI().getClass());
		ConnectUI ui = (ConnectUI) c.getUI();
		
		// Select address & enter
		ui.ipEntry.setEnabled(true);
		ui.nameEntry.setEnabled(false);
		for (char ch : address.toCharArray()) {
			c.handleChar(ch);
		}
		
		// Select name & enter (Tab 3 times to test that aspect of the ConnectUI)
		c.handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
		c.handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		c.handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
		c.handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		c.handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
		c.handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		Thread.sleep(50);
		for (char ch : name.toCharArray()) {
			c.handleChar(ch);
		}
	}
	
	private void connectUsingEnter(String address, String name) throws InterruptedException {
		setup(address, name);
		c.handleKey(GLFW_KEY_ENTER, GLFW_KEY_UNKNOWN, GLFW_PRESS, 0);
		c.handleKey(GLFW_KEY_ENTER, GLFW_KEY_UNKNOWN, GLFW_RELEASE, 0);
		
		Thread.sleep(2000);
		assertTrue(t.isAlive());
		assertEquals(StartUI.class, c.getUI().getClass());
	}
	
	@After
	public void tearDownInstance() throws InterruptedException {
		if (c != null) {
			c.setUI(null);
			Thread.sleep(400);
			assertFalse(t.isAlive());
			c = null;
			t = null;
		}
	}
	
	@Test
	public void gameTest() throws InterruptedException {
		t = new Thread(this::gameTestHelper, "ExitTestHelper");
		t.start();
		
		// Run the client
		runClient();
		
		synchronized (notifyEndResult) {
			if (!ended) {
				notifyEndResult.wait();
			}
		}
		Thread.sleep(500);
		// Ended should be true here
		assertFalse(t.isAlive());
		assertTrue(ended);
		assertFalse(failed);
	}
	
	public void gameTestHelper() {
		synchronized (notifyEndResult) {
			ended = false;
			failed = false;
		}
		
		try {
			synchronized (notifyClientLock) {
				notifyClientLock.wait();
			}
			
			// Connect and go to StartUI
			connectUsingEnter("", "test1");
			
			// StartUI - go to help
			UI ui = c.getUI();
			assertEquals(StartUI.class, ui.getClass());
			synchronized (ui) {
				StartUI startUI = (StartUI) ui;
				startUI.helpButton.onClicked();
			}
			Thread.sleep(200);
			
			// HelpUI - Go back
			ui = c.getUI();
			assertEquals(HelpUI.class, ui.getClass());
			synchronized (ui) {
				HelpUI helpUI = (HelpUI) ui;
				helpUI.backBtn.onClicked();
			}
			Thread.sleep(200);
			
			// StartUI - go to help
			ui = c.getUI();
			assertEquals(StartUI.class, ui.getClass());
			synchronized (ui) {
				StartUI startUI = (StartUI) ui;
				startUI.helpButton.onClicked();
			}
			Thread.sleep(200);
			
			// HelpUI - Go forward 7 times
			for (int i = 0; i < 7; i++) {
				ui = c.getUI();
				assertEquals(HelpUI.class, ui.getClass());
				synchronized (ui) {
					HelpUI helpUI = (HelpUI) ui;
					helpUI.nextBtn.onClicked();
				}
				Thread.sleep(200);
			}
			
			// StartUI - start
			ui = c.getUI();
			assertEquals(StartUI.class, ui.getClass());
			synchronized (ui) {
				StartUI startUI = (StartUI) ui;
				startUI.startButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyUI - back
			ui = c.getUI();
			assertEquals(LobbyUI.class, ui.getClass());
			Thread.sleep(1000); // Wait for lobby list
			int prevLobbiesSize;
			synchronized (ui) {
				LobbyUI lobbyUI = (LobbyUI) ui;
				prevLobbiesSize = lobbyUI.lobbies.size();
				lobbyUI.backButton.onClicked();
			}
			Thread.sleep(200);
			
			// StartUI - start
			ui = c.getUI();
			assertEquals(StartUI.class, ui.getClass());
			synchronized (ui) {
				StartUI startUI = (StartUI) ui;
				startUI.startButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyUI - create
			ui = c.getUI();
			assertEquals(LobbyUI.class, ui.getClass());
			Thread.sleep(1000); // Wait for lobby list
			synchronized (ui) {
				LobbyUI lobbyUI = (LobbyUI) ui;
				assertEquals(prevLobbiesSize, lobbyUI.lobbies.size());
				lobbyUI.createButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyCreateUI - go back
			ui = c.getUI();
			assertEquals(LobbyCreateUI.class, ui.getClass());
			synchronized (ui) {
				LobbyCreateUI lobbyCreateUI = (LobbyCreateUI) ui;
				lobbyCreateUI.backButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyUI - create
			ui = c.getUI();
			assertEquals(LobbyUI.class, ui.getClass());
			Thread.sleep(1000); // Wait for lobby list
			synchronized (ui) {
				LobbyUI lobbyUI = (LobbyUI) ui;
				assertEquals(prevLobbiesSize, lobbyUI.lobbies.size());
				lobbyUI.createButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyCreateUI - enter lobby and create
			ui = c.getUI();
			assertEquals(LobbyCreateUI.class, ui.getClass());
			synchronized (ui) {
				LobbyCreateUI lobbyCreateUI = (LobbyCreateUI) ui;
				for (char ch : "test lobby1512".toCharArray()) {
					ui.handleChar(ch);
				}
				lobbyCreateUI.createButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyWaitUI - ready up & wait, but then leave
			ui = c.getUI();
			assertEquals(LobbyWaitUI.class, ui.getClass());
			Thread.sleep(1000); // Wait for lobby info
			synchronized (ui) { // Ready up
				LobbyWaitUI lobbyWaitUI = (LobbyWaitUI) ui;
				lobbyWaitUI.toggleReadyButton.onClicked();
			}
			Thread.sleep(1000); // Wait for a bit
			synchronized (ui) { // Leave
				LobbyWaitUI lobbyWaitUI = (LobbyWaitUI) ui;
				lobbyWaitUI.leaveButton.onClicked();
			}
			Thread.sleep(500);
			
			// LobbyUI - create
			ui = c.getUI();
			assertEquals(LobbyUI.class, ui.getClass());
			Thread.sleep(1000); // Wait for lobby list
			synchronized (ui) {
				// Ensure that lobby list has same number as last time
				LobbyUI lobbyUI = (LobbyUI) ui;
				assertEquals(prevLobbiesSize, lobbyUI.lobbies.size());
				lobbyUI.createButton.onClicked();
			}
			Thread.sleep(200);
			
			// LobbyCreateUI - enter lobby and create
			ui = c.getUI();
			assertEquals(LobbyCreateUI.class, ui.getClass());
			synchronized (ui) {
				LobbyCreateUI lobbyCreateUI = (LobbyCreateUI) ui;
				for (char ch : "test lobby1241".toCharArray()) {
					ui.handleChar(ch);
				}
				lobbyCreateUI.createButton.onClicked();
			}
			Thread.sleep(500);
			
			// LobbyWaitUI - ready up & wait for world
			ui = c.getUI();
			assertEquals(LobbyWaitUI.class, ui.getClass());
			Thread.sleep(1000); // Wait for lobby info
			synchronized (ui) { // Ready up
				LobbyWaitUI lobbyWaitUI = (LobbyWaitUI) ui;
				lobbyWaitUI.toggleReadyButton.onClicked();
			}
			Thread.sleep(Util.LOBBY_WAIT_SECS*1000 + 500); // Wait for a bit
			
			// GameUI - wait until game starts
			ui = c.getUI();
			assertEquals(GameUI.class, ui.getClass());
			Thread.sleep(Util.GAME_START_WAIT_SECS*1000 + 500); // Wait for game to start
			
			// GameUI - game has started
			ui = c.getUI();
			assertEquals(GameUI.class, ui.getClass());
			synchronized (ui) { // "Hold down" tab
				ui.handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
			}
			// Wait for key to register
			Thread.sleep(200);
			synchronized (ui) { // "Release" tab
				GameUI gameUI = (GameUI) ui;
				assertTrue(gameUI.scoreboardShown);
				String name = ui.getConnection().getName();
				assertTrue(gameUI.world.getScoreboard().getPlayer(name) != null);
				ui.handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
			}
			// Wait for key to register
			Thread.sleep(200);
			synchronized (ui) { // Ensure scoreboard is not shown
				assertFalse(((GameUI)ui).scoreboardShown);
			}
			Thread.sleep(200);
			// Go to EscapeUI
			synchronized (ui) { // Press & release escape
				ui.handleKey(GLFW_KEY_ESCAPE, 0, GLFW_PRESS, 0);
				ui.handleKey(GLFW_KEY_ESCAPE, 0, GLFW_RELEASE, 0);
			}
			// Wait for key to register
			Thread.sleep(200);
			
			// EscapeUI
			ui = c.getUI();
			assertEquals(EscapeUI.class, ui.getClass());
			synchronized (ui) { // Test mute button
				EscapeUI escapeUI = (EscapeUI) ui;
				escapeUI.muteComponent.onClicked();
			}
			Thread.sleep(200);
			synchronized (ui) { // Test mute button
				assertTrue(ui.getAudio().isMuted());
				EscapeUI escapeUI = (EscapeUI) ui;
				escapeUI.muteComponent.onClicked();
			}
			Thread.sleep(200);
			synchronized (ui) { // Test mute button
				assertFalse(ui.getAudio().isMuted());
				// Continue
				EscapeUI escapeUI = (EscapeUI) ui;
				escapeUI.continueBtn.onClicked();
			}
			Thread.sleep(200);
			
			// GameUI
			ui = c.getUI();
			assertEquals(GameUI.class, ui.getClass());
			// Go to EscapeUI
			synchronized (ui) { // Press & release escape
				ui.handleKey(GLFW_KEY_ESCAPE, 0, GLFW_PRESS, 0);
				ui.handleKey(GLFW_KEY_ESCAPE, 0, GLFW_RELEASE, 0);
			}
			Thread.sleep(200);
			
			// EscapeUI - go through help menu
			ui = c.getUI();
			assertEquals(EscapeUI.class, ui.getClass());
			synchronized (ui) {
				EscapeUI escapeUI = (EscapeUI) ui;
				escapeUI.helpBtn.onClicked();
			}
			Thread.sleep(200);
			
			// HelpUI - go back
			ui = c.getUI();
			assertEquals(HelpUI.class, ui.getClass());
			synchronized (ui) {
				HelpUI helpUI = (HelpUI) ui;
				helpUI.backBtn.onClicked();
			}
			Thread.sleep(200);
			
			// EscapeUI - go through help menu again
			ui = c.getUI();
			assertEquals(EscapeUI.class, ui.getClass());
			synchronized (ui) {
				EscapeUI escapeUI = (EscapeUI) ui;
				escapeUI.helpBtn.onClicked();
			}
			Thread.sleep(500);
			
			// HelpUI - go partially through menu
			for (int i = 0; i < 5; i++) {
				ui = c.getUI();
				assertEquals(HelpUI.class, ui.getClass());
				synchronized (ui) {
					HelpUI helpUI = (HelpUI) ui;
					helpUI.nextBtn.onClicked();
				}
				Thread.sleep(200);
			}
			
			// HelpUI - exit back to EscapeUI
			ui = c.getUI();
			assertEquals(HelpUI.class, ui.getClass());
			synchronized (ui) {
				HelpUI helpUI = (HelpUI) ui;
				helpUI.exitBtn.onClicked();
			}
			Thread.sleep(200);
			
			// EscapeUI - exit back to StartUI
			ui = c.getUI();
			assertEquals(EscapeUI.class, ui.getClass());
			synchronized (ui) {
				EscapeUI escapeUI = (EscapeUI) ui;
				escapeUI.quitBtn.onClicked();
			}
			Thread.sleep(200);
			
			// StartUI - exit back to EscapeUI
			ui = c.getUI();
			assertEquals(StartUI.class, ui.getClass());
			synchronized (ui) {
				StartUI startUI = (StartUI) ui;
				startUI.exitButton.onClicked();
			}
			Thread.sleep(200);
			assertEquals(null, c.getUI());
			
			failed = false;
		} catch (Throwable t) {
			synchronized (notifyEndResult) {
				failed = true;
				ended = true;
				t.printStackTrace();
				notifyEndResult.notifyAll();
			}
		} finally {
			// Set passed to false
			synchronized (notifyEndResult) {
				ended = true;
				notifyEndResult.notifyAll();
			}
		}
	}
}
