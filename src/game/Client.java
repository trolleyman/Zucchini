package game;

import game.audio.AudioManager;
import game.render.Renderer;
import game.ui.ConnectUI;
import game.ui.UI;
import org.lwjgl.Version;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * The main class for the client. It contains a main method that, when run, initializes the client and
 * starts everything running.
 * @author Callum
 */
public class Client implements Runnable, InputHandler {
	/**
	 * Currently running thread
	 */
	private Thread thread;
	
	/**
	 * Lock for {@link Client#queuedEvents}
	 */
	private final Object queuedEventsLock = new Object();
	
	/**
	 * Events that are queued to be consumed currently.
	 * <p>
	 * This is here for synchronization reasons.
	 */
	private ArrayList<Consumer<InputHandler>> queuedEvents = new ArrayList<>();
	
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
	public void run() {
		thread = Thread.currentThread();
		System.out.println("==== UI Start State: " + ui.getClass().getSimpleName() + " ====");
		renderer.show();
		
		try {
			loop();
		} finally {
			renderer.destroy();
			audio.cleanup();
			thread = null;
		}
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
			} else if (ui.getConnection() != null) {
				ui.getConnection().close();
			}
		}
		ui = next;
	}
	
	private synchronized void render() {
		renderer.beginFrame();
		ui.render(renderer);
		renderer.endFrame();
		// Process queued events
		synchronized (queuedEventsLock) {
			for (Consumer<InputHandler> e : queuedEvents) {
				e.accept(ui);
			}
			queuedEvents.clear();
		}
	}
	
	public static void main(String[] args) {
		new Client(false).run();
		System.exit(0);
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		if (Thread.currentThread() != thread) {
			synchronized (queuedEventsLock) {
				queuedEvents.add((ih) -> ih.handleKey(key, scancode, action, mods));
			}
		} else {
			ui.handleKey(key, scancode, action, mods);
		}
	}
	
	@Override
	public void handleChar(char c) {
		if (Thread.currentThread() != thread) {
			synchronized (queuedEventsLock) {
				queuedEvents.add((ih) -> ih.handleChar(c));
			}
		} else {
			ui.handleChar(c);
		}
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		if (Thread.currentThread() != thread) {
			synchronized (queuedEventsLock) {
				queuedEvents.add((ih) -> ih.handleCursorPos(xpos, ypos));
			}
		} else {
			ui.handleCursorPos(xpos, ypos);
		}
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		if (Thread.currentThread() != thread) {
			synchronized (queuedEventsLock) {
				queuedEvents.add((ih) -> ih.handleMouseButton(button, action, mods));
			}
		} else {
			ui.handleMouseButton(button, action, mods);
		}
	}
	
	@Override
	public void handleScroll(double xoffset, double yoffset) {
		if (Thread.currentThread() != thread) {
			synchronized (queuedEventsLock) {
				queuedEvents.add((ih) -> ih.handleScroll(xoffset, yoffset));
			}
		} else {
			ui.handleScroll(xoffset, yoffset);
		}
	}
	
	@Override
	public void handleResize(int w, int h) {
		if (Thread.currentThread() != thread) {
			synchronized (queuedEventsLock) {
				queuedEvents.add((ih) -> ih.handleResize(w, h));
			}
		} else {
			ui.handleResize(w, h);
		}
	}
}
