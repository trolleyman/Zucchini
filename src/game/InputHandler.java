package game;

/**
 * @author jackm
 */
public interface InputHandler {
	// By default all of the methods are empty
	public default void setKeyboardManager(KeyboardManager km) {};
	public default void handleKey(int key, int scancode, int action, int mods) {};
	public default void handleChar(char c) {};
	public default void handleCursorPos(double xpos, double ypos) {};
	public default void handleMouseButton(int button, int action, int mods) {};
	public default void handleScroll(double xoffset, double yoffset) {};
}
