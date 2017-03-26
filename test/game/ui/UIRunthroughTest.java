package game.ui;

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
			c.getHandler().handleChar(ch);
		}
		
		// Select name & enter (Tab 3 times to test that aspect of the ConnectUI)
		c.getHandler().handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
		c.getHandler().handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		c.getHandler().handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
		c.getHandler().handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		c.getHandler().handleKey(GLFW_KEY_TAB, 0, GLFW_PRESS, 0);
		c.getHandler().handleKey(GLFW_KEY_TAB, 0, GLFW_RELEASE, 0);
		Thread.sleep(50);
		for (char ch : name.toCharArray()) {
			c.getHandler().handleChar(ch);
		}
	}
	
	private void connectUsingEnter(String address, String name) throws InterruptedException {
		setup(address, name);
		c.getHandler().handleKey(GLFW_KEY_ENTER, GLFW_KEY_UNKNOWN, GLFW_PRESS, 0);
		c.getHandler().handleKey(GLFW_KEY_ENTER, GLFW_KEY_UNKNOWN, GLFW_RELEASE, 0);
		
		Thread.sleep(100);
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
}
