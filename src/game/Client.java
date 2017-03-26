package game;

import game.audio.AudioManager;
import game.render.Renderer;
import game.ui.ConnectUI;
import game.ui.UI;
import org.lwjgl.Version;

/**
 * The main class for the client. It contains a main method that, when run, initializes the client and
 * starts everything running.
 * @author Callum
 */
public class Client implements Runnable, InputPipe {
	/**
	 * The current UI state
	 */
	private transient UI ui;
	
	/**
	 * The renderer
	 */
	private Renderer renderer;
	
	/**
	 * The audio manager
	 */
	private AudioManager audio;
	
	/**
	 * Previous time in nanoseconds of update.
	 */
	private long prevTime;
	
	public Client(boolean _fullscreen) {
		System.out.println("LWJGL " + Version.getVersion() + " loaded.\n");
		
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
		ui = new ConnectUI(null, audio, renderer.getTextureBank(), renderer.getFontBank());
	}
	
	/**
	 * This **MUST** only be used for testing.
	 * <p>
	 * This gets the current UI used by the client
	 */
	public synchronized UI getUI() {
		return ui;
	}
	
	/**
	 * This **MUST** only be used for testing.
	 * <p>
	 * This sets the current UI used by the client to the one specified
	 */
	public synchronized void setUI(UI ui) {
		this.ui = ui;
	}
	
	@Override
	public InputHandler getHandler() {
		return this.ui;
	}
	
	@Override
	public void run() {
		System.out.println("==== UI Start State: " + ui.getClass().getSimpleName() + " ====");
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
	
	private synchronized void loopIter() {
		if (ui == null)
			return;
		
		render();
		
		long now = System.nanoTime();
		long dtNanos = now - prevTime;
		prevTime = now;
		ui.update(dtNanos / (double) Util.NANOS_PER_SECOND);
		UI next = ui.next();
		if (next != ui) {
			ui.destroy();
			if (next != null) {
				System.out.println("==== UI State Change: " + ui.getClass().getSimpleName() + " => " + next.getClass().getSimpleName() + " ====");
				next.handleResize(renderer.getWidth(), renderer.getHeight());
				next.handleCursorPos(renderer.getMouseX(), renderer.getMouseY());
			}
		}
		ui = next;
	}
	
	private synchronized void render() {
		renderer.beginFrame();
		ui.render(renderer);
		renderer.endFrame();
	}
	
	public static void main(String[] args) {
		new Client(false).run();
		System.exit(0);
	}
}
