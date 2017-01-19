package game;
/**
 * 
 */

/**
 * @author jackm
 *
 */
public interface InputHandler {

	void handleKey(int key, int scancode, int action, int mods);
	void handleChar(char c);
	void handleMouseButton(int button, int action, int mods);
	void handleScroll(double xoffset, double yoffset);

	
}
