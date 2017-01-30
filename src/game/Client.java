package game;

import org.lwjgl.Version;

import game.render.Renderer;
import game.ui.StartUI;
import game.ui.UI;

class Client implements Runnable {
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
			public void handleCursorPos(double xpos, double ypos) { client.ui.handleCursorPos(xpos, renderer.getHeight() - ypos); };
			@Override
			public void handleMouseButton(int button, int action, int mods) { client.ui.handleMouseButton(button, action, mods); };
			@Override
			public void handleScroll(double xoffset, double yoffset) { client.ui.handleScroll(xoffset, yoffset); };
		};
		
		// Initialize renderer
		renderer = new Renderer(ih, _fullscreen);
		
		// Initialize UI
		ui = new StartUI(renderer);
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
		System.out.println("==== UI Start State: " + ui.toString() + " ====");
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
		if (next != ui)
			if (next != null)
				System.out.println("==== UI State Change: " + ui.toString() + " => " + next.toString() + " ====");
		
		ui = next;
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
