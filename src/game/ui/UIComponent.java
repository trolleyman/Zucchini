package game.ui;

import game.InputHandler;
import game.render.IRenderer;

/**
 * A UIComponent is the root class for components of the UI. For example, buttons, text fields,
 * labels etc.
 * 
 * @author Callum
 */
public abstract class UIComponent implements InputHandler {
	/**
	 * Called on every update
	 * @param dt The number of seconds since the last update
	 */
	public abstract void update(double dt);
	/**
	 * Called to render the component
	 * @param r The renderer
	 */
	public abstract void render(IRenderer r);
}
