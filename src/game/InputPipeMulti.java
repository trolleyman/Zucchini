package game;

import java.util.ArrayList;

/**
 * An InputPipe redirects input to all handlers specified by the getHandlers() method.
 * 
 * @author Callum
 */
public interface InputPipeMulti extends InputHandler {
	public ArrayList<InputHandler> getHandlers();
	
	@Override
	public default void handleKey(int key, int scancode, int action, int mods) {
		for (InputHandler ih : getHandlers())
			ih.handleKey(key, scancode, action, mods);
	}
	@Override
	public default void handleChar(char c) {
		for (InputHandler ih : getHandlers())
			ih.handleChar(c);
	}
	@Override
	public default void handleCursorPos(double xpos, double ypos) {
		for (InputHandler ih : getHandlers())
			ih.handleCursorPos(xpos, ypos);
	}
	@Override
	public default void handleMouseButton(int button, int action, int mods) {
		for (InputHandler ih : getHandlers())
			ih.handleMouseButton(button, action, mods);
	}
	@Override
	public default void handleScroll(double xoffset, double yoffset) {
		for (InputHandler ih : getHandlers())
			ih.handleScroll(xoffset, yoffset);
	}
}
