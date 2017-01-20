package game;

/**
 * @author jackm
 *
 */
public interface InputHandler {
	public void setKeyboardManager(KeyboardManager km);
	public void handleKey(int key, int scancode, int action, int mods);
	public void handleChar(char c);
	public void handleCursorPos(double xpos, double ypos);
	public void handleMouseButton(int button, int action, int mods);
	public void handleScroll(double xoffset, double yoffset);
}
