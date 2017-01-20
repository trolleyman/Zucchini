/**
 * 
 */
package game.ui;

import game.InputHandler;
import game.KeyboardManager;
import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public abstract class UI implements InputHandler {
	protected KeyboardManager km;
	
	public UI(KeyboardManager _km) {
		this.km = _km;
	}
	
	@Override
	public void setKeyboardManager(KeyboardManager _km) {
		this.km = _km;
	}
	public abstract void update(double dt);
	public abstract void render(IRenderer r);
	public abstract UI next();
}
