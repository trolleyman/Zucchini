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
	public abstract void update(double dt);
	public abstract void render(IRenderer r);
	public abstract UI next();
}
