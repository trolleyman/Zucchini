/**
 * 
 */
package game.ui;

import game.InputHandler;
import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public abstract class UI implements InputHandler {
	protected IRenderer renderer;
		
	public UI(IRenderer renderer) {
		this.renderer = renderer;
	}
	
	/**
	 * Updates the UI
	 * @param dt The number of seconds passed since the last update
	 */
	public abstract void update(double dt);
	
	/**
	 * Renders the UI onto the screen
	 * @param r The Renderer object
	 */
	public abstract void render(IRenderer r);
	
	/**
	 * Returns the next UI state to be in
	 */
	public abstract UI next();
	
	// Inheriting classes must implement toString
	@Override
	public abstract String toString();
}
