package game;

import game.audio.AudioManager;
import game.net.IClientConnection;
import game.networking.client.ClientConnection;
import game.render.Renderer;
import game.ui.StartUI;
import game.ui.UI;
import org.lwjgl.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
	
	public Client(boolean _fullscreen) {
		System.out.println("LWJGL " + Version.getVersion() + " loaded.");
		
		// Initialize connection to server
		try {
			System.out.print("Enter your name: ");
			System.out.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String name = reader.readLine();
			reader.close();
			
			connection = new ClientConnection(name);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
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
		
		// Initialize UI
		ui = new StartUI(audio, renderer.getImageBank());
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
		new Client(false).run();
		System.exit(0);
	}
}
