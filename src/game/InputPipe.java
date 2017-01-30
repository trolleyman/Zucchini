package game;

/**
 * An InputPipe redirects input to the handler specified by the getHandler() method.
 * 
 * @author Callum
 */
public interface InputPipe extends InputHandler {
	public InputHandler getHandler();
	
	@Override
	public default void setKeyboardManager(KeyboardManager km) {
		getHandler().setKeyboardManager(km);
	}
	@Override
	public default void handleKey(int key, int scancode, int action, int mods) {
		getHandler().handleKey(key, scancode, action, mods);
	}
	@Override
	public default void handleChar(char c) {
		getHandler().handleChar(c);
	}
	@Override
	public default void handleCursorPos(double xpos, double ypos) {
		getHandler().handleCursorPos(xpos, ypos);
	}
	@Override
	public default void handleMouseButton(int button, int action, int mods) {
		getHandler().handleMouseButton(button, action, mods);
	}
	@Override
	public default void handleScroll(double xoffset, double yoffset) {
		getHandler().handleScroll(xoffset, yoffset);
	}
}
