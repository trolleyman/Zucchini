package game;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import game.render.Renderer;
import game.ui.StartUI;
import game.ui.UI;

class Client implements Runnable {
	// The current UI state
	private UI ui;
	
	// The renderer
	private Renderer renderer;
	
	public Client() {
		System.out.println("LWJGL " + Version.getVersion() + " loaded.");
		
		ui = new StartUI();
		
		// Initialize renderer
		renderer = new Renderer();
	}
	
	@Override
	public void run() {
		renderer.show();
		
		loop();
		
		renderer.destroy();
	}
	
	private void loop() {
		// Set the clear color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		long prevTime = System.nanoTime();
		while ( !renderer.shouldClose() ) {
			renderer.beginFrame();
			ui.render(renderer);
			renderer.endFrame();
			
			long now = System.nanoTime();
			long dtNanos = now - prevTime;
			double dt = ((double) dtNanos) / 1_000_000_000.0;
			prevTime = now;
			
			ui.update(dt);
			ui = ui.next();
		}
	}
	
	public static void main(String[] args) {
		new Client().run();
	}
}
