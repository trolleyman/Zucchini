/**
 * 
 */
package game.ui;

import game.InputHandler;
import game.render.IRenderer;

/**
 * The UI is the root class of all UIs
 * 
 * @author jackm
 */
public abstract class UI implements InputHandler {
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
	
	/**
	 * Called when the UI is destroyed
	 */
	public abstract void destroy();
	
	/** Inheriting classes must implement {@link java.lang.Object#toString toString()} */
	@Override
	public abstract String toString();
}
