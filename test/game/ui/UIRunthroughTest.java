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
	
	@BeforeClass
	public static void setUp() {
		server = new Server();
		server.start();
		try {
			Thread.sleep(100);
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
	
	private final Object notifyClientLock = new Object();
	
	private void setupClient() {
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
		t = new Thread(this::setupClient, "Client");
		t.start();
		synchronized (notifyClientLock) {
			notifyClientLock.wait();
		}
		
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
		
		Thread.sleep(300);
		assertTrue(t.isAlive());
		assertEquals(StartUI.class, c.getUI().getClass());
	}
	
	@After
	public void tearDownInstance() throws InterruptedException {
		if (c != null) {
			c.setUI(null);
			Thread.sleep(100);
			assertFalse(t.isAlive());
			c = null;
			t = null;
		}
	}
	
	@Test
	public void exitTest() throws InterruptedException {
		// Connect and go to StartUI
		connectUsingEnter("localhost", "test0");
		
		StartUI ui = (StartUI) c.getUI();
		synchronized (ui) {
			// Exit application
			ui.exitButton.onClicked();
		}
		Thread.sleep(100);
		assertEquals(null, c.getUI());
		assertFalse(t.isAlive());
	}
	
	@Test
	public void gameTest() throws InterruptedException {
		// Connect and go to StartUI
		connectUsingEnter("localhost", "test0");
		
		// StartUI - go to help
		UI ui = c.getUI();
		assertEquals(StartUI.class, ui.getClass());
		synchronized (ui) {
			StartUI startUI = (StartUI) ui;
			startUI.helpButton.onClicked();
		}
		Thread.sleep(100);
		
		// HelpUI - Go back
		ui = c.getUI();
		assertEquals(HelpUI.class, ui.getClass());
		synchronized (ui) {
			HelpUI helpUI = (HelpUI) ui;
			helpUI.backBtn.onClicked();
		}
		Thread.sleep(100);
		
		// StartUI - go to help
		ui = c.getUI();
		assertEquals(StartUI.class, ui.getClass());
		synchronized (ui) {
			StartUI startUI = (StartUI) ui;
			startUI.helpButton.onClicked();
		}
		Thread.sleep(100);
		
		// HelpUI - Go forward 7 times
		for (int i = 0; i < 7; i++) {
			ui = c.getUI();
			assertEquals(HelpUI.class, ui.getClass());
			synchronized (ui) {
				HelpUI helpUI = (HelpUI) ui;
				helpUI.nextBtn.onClicked();
			}
			Thread.sleep(100);
		}
		
		// StartUI - start
		ui = c.getUI();
		assertEquals(StartUI.class, ui.getClass());
		synchronized (ui) {
			StartUI startUI = (StartUI) ui;
			startUI.startButton.onClicked();
		}
		Thread.sleep(100);
		
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
		
		// StartUI - start
		ui = c.getUI();
		assertEquals(StartUI.class, ui.getClass());
		synchronized (ui) {
			StartUI startUI = (StartUI) ui;
			startUI.startButton.onClicked();
		}
		Thread.sleep(100);
		
		// LobbyUI - create
		ui = c.getUI();
		assertEquals(LobbyUI.class, ui.getClass());
		Thread.sleep(1000); // Wait for lobby list
		synchronized (ui) {
			LobbyUI lobbyUI = (LobbyUI) ui;
			assertEquals(prevLobbiesSize, lobbyUI.lobbies.size());
			lobbyUI.createButton.onClicked();
		}
		
		// LobbyCreateUI - go back
		Thread.sleep(200);
		ui = c.getUI();
		assertEquals(LobbyCreateUI.class, ui.getClass());
		synchronized (ui) {
			LobbyCreateUI lobbyCreateUI = (LobbyCreateUI) ui;
			lobbyCreateUI.backButton.onClicked();
		}
		
		// LobbyUI - create
		ui = c.getUI();
		assertEquals(LobbyUI.class, ui.getClass());
		Thread.sleep(1000); // Wait for lobby list
		synchronized (ui) {
			LobbyUI lobbyUI = (LobbyUI) ui;
			assertEquals(prevLobbiesSize, lobbyUI.lobbies.size());
			lobbyUI.createButton.onClicked();
		}
		
		// LobbyCreateUI - enter lobby and create
		Thread.sleep(200);
		ui = c.getUI();
		assertEquals(LobbyCreateUI.class, ui.getClass());
		synchronized (ui) {
			LobbyCreateUI lobbyCreateUI = (LobbyCreateUI) ui;
			for (char ch : "test lobby1512".toCharArray()) {
				ui.handleChar(ch);
			}
			lobbyCreateUI.createButton.onClicked();
		}
		
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
		
		// LobbyUI - create
		Thread.sleep(500);
		ui = c.getUI();
		assertEquals(LobbyUI.class, ui.getClass());
		Thread.sleep(1000); // Wait for lobby list
		synchronized (ui) {
			// Ensure that lobby list has same number as last time
			LobbyUI lobbyUI = (LobbyUI) ui;
			assertEquals(prevLobbiesSize, lobbyUI.lobbies.size());
			lobbyUI.createButton.onClicked();
		}
		
		// LobbyCreateUI - enter lobby and create
		Thread.sleep(200);
		ui = c.getUI();
		assertEquals(LobbyCreateUI.class, ui.getClass());
		synchronized (ui) {
			LobbyCreateUI lobbyCreateUI = (LobbyCreateUI) ui;
			for (char ch : "test lobby1241".toCharArray()) {
				ui.handleChar(ch);
			}
			lobbyCreateUI.createButton.onClicked();
		}
		
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
		Thread.sleep(100);
		synchronized (ui) { // "Release" tab
			GameUI gameUI = (GameUI) ui;
			assertTrue(gameUI.scoreboardShown);
			String name = ui.getConnection().getName();
			assertTrue(gameUI.world.getScoreboard().getPlayer(name) != null);
			ui.handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		}
		// Wait for key to register
		Thread.sleep(100);
		synchronized (ui) { // Ensure scoreboard is not shown
			assertFalse(((GameUI)ui).scoreboardShown);
		}
		
		// Exit
	}
}
