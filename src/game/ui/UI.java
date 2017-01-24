/**
 * 
 */
package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.KeyboardManager;
import game.Util;
import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public abstract class UI implements InputHandler {
	protected KeyboardManager km;
	
	protected ArrayList<InputHandler> inputHandlers;
	
	public UI(KeyboardManager _km) {
		this.km = _km;
		this.inputHandlers = new ArrayList<>();
	}
	
	@Override
	public void setKeyboardManager(KeyboardManager _km) {
		this.km = _km;
	}
	public abstract void update(double dt);
	public abstract void render(IRenderer r);
	public abstract UI next();
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		Util.printKey(key, scancode, action, mods);
		for (InputHandler ih : inputHandlers)
			ih.handleKey(key, scancode, action, mods);
	}
	
	@Override
	public void handleChar(char c) {
		for (InputHandler ih : inputHandlers)
			ih.handleChar(c);
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		for (InputHandler ih : inputHandlers)
			ih.handleCursorPos(xpos, ypos);
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		for (InputHandler ih : inputHandlers)
			ih.handleMouseButton(button, action, mods);
	}

	@Override
	public void handleScroll(double xoffset, double yoffset) {
		for (InputHandler ih : inputHandlers)
			ih.handleScroll(xoffset, yoffset);
	}
}
