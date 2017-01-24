package game;

import org.lwjgl.Version;

import game.render.Renderer;
import game.ui.StartUI;
import game.ui.UI;

class Client implements Runnable {
	private static final double UPS = 120;
	private static final long NANOS_PER_UPDATE = (long) (1_000_000_000 / UPS);
	
	// The current UI state
	private UI ui;
	
	// The renderer
	private Renderer renderer;
	
	// Previous time in nanoseconds of update.
	long prevTime;
	
	// Number of nanoseconds to process
	long dtPool;
	
	public Client(boolean _fullscreen) {
		System.out.println("LWJGL " + Version.getVersion() + " loaded.");
		
		// Initialize input handler - redirect events to the current ui
		Client client = this;
		InputHandler ih = new InputHandler() {
			@Override
			public void setKeyboardManager(KeyboardManager km) { client.ui.setKeyboardManager(km); }
			@Override
			public void handleKey(int key, int scancode, int action, int mods) { client.ui.handleKey(key, scancode, action, mods); };
			@Override
			public void handleChar(char c) { client.ui.handleChar(c); };
			@Override
			public void handleCursorPos(double xpos, double ypos) { client.ui.handleCursorPos(xpos, ypos); };
			@Override
			public void handleMouseButton(int button, int action, int mods) { client.ui.handleMouseButton(button, action, mods); };
			@Override
			public void handleScroll(double xoffset, double yoffset) { client.ui.handleScroll(xoffset, yoffset); };
		};
		
		// Initialize renderer
		renderer = new Renderer(ih, _fullscreen);
		
		// Initialize UI
		ui = new StartUI(renderer.getKeyboardManager(), renderer.getImageBank());
	}
	
	@Override
	public void run() {
		renderer.show();
		
		loop();
		
		renderer.destroy();
	}
	
	private void loop() {
		prevTime = System.nanoTime();
		dtPool = 0;
		while (!renderer.shouldClose()) {
			loopIter();
		}
	}
	
	private void loopIter() {
		render();
		
		long now = System.nanoTime();
		long dtNanos = now - prevTime;
		prevTime = now;
		dtPool += dtNanos;
		while (dtPool > NANOS_PER_UPDATE) {
			ui.update(((double) NANOS_PER_UPDATE) / 1_000_000_000.0);
			ui = ui.next();
			dtPool -= NANOS_PER_UPDATE;
		}
	}
	
	private void render() {
		renderer.beginFrame();
		ui.render(renderer);
		renderer.endFrame();
	}
	
	public static void main(String[] args) {
		new Client(false).run();
	}
}
