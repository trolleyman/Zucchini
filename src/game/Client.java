package game;

import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.client.ClientConnection;
import game.net.client.IClientConnection;
import game.render.FontBank;
import org.lwjgl.Version;

import game.audio.AudioManager;
import game.render.Renderer;
import game.ui.StartUI;
import game.ui.UI;

/**
 * The main class for the client. It contains a main method that, when run, initializes the client and
 * starts everything running.
 * 
 * @author Callum
 */
class Client implements Runnable, InputPipe {
	/**
	 * The current UI state
	 */
	private UI ui;
	
	private Renderer renderer;
	
	private AudioManager audio;
	
	private IClientConnection connection;
	
	/**
	 * Previous time in nanoseconds of update.
	 */
	private long prevTime;
	
	public Client(boolean _fullscreen, String name) {
		System.out.println("LWJGL " + Version.getVersion() + " loaded.");
		
		// Initialize connection to server
		try {
			connection = new ClientConnection(name);
		} catch (ProtocolException | NameException e) {
			System.err.println("Error: Could not connect to server:");
			e.printStackTrace();
			System.exit(1); // Currently just exit if we can't connect to the server
		}
		
		// Initialize renderer
		renderer = new Renderer(this, _fullscreen);
		
		// Initialize audio manager
		try {
			audio = new AudioManager();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		// Intiialize FontBank
		FontBank fontBank = new FontBank();
		
		// Initialize UI
		ui = new StartUI(connection, audio, renderer.getTextureBank(), fontBank);
	}
	
	@Override
	public InputHandler getHandler() {
		return this.ui;
	}
	
	@Override
	public void run() {
		System.out.println("==== UI Start State: " + ui.toString() + " ====");
		renderer.show();
		
		loop();
		
		renderer.destroy();
		audio.cleanup();
	}
	
	private void loop() {
		prevTime = System.nanoTime();
		while (!renderer.shouldClose() && ui != null) {
			loopIter();
		}
		System.out.println("Exiting...");
	}
	
	private void loopIter() {
		render();
		
		long now = System.nanoTime();
		long dtNanos = now - prevTime;
		prevTime = now;
		ui.update(dtNanos / (double) Util.NANOS_PER_SECOND);
		UI next = ui.next();
		if (next != ui && next != null) {
			System.out.println("==== UI State Change: " + ui.toString() + " => " + next.toString() + " ====");
			next.handleResize(renderer.getWidth(), renderer.getHeight());
			next.handleCursorPos(renderer.getMouseX(), renderer.getMouseY());
		}
		ui = next;
	}
	
	private void render() {
		renderer.beginFrame();
		ui.render(renderer);
		renderer.endFrame();
	}
	
	public static void main(String[] args) {
		String name = "default";
		if (args.length >= 1)
			name = args[0];
		new Client(false, name).run();
		System.exit(0);
	}
}
