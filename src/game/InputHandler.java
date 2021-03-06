package game;

/**
 * Input handling interface
 * 
 * <p>
 * TODO: handleMouseEnter
 * <br>
 * TODO: Cursor modes: See <a target="_top" href="http://www.glfw.org/docs/latest/input_guide.html#cursor_mode">here</a>.
 * 
 * @author jackm
 */
public interface InputHandler {
	// By default all of the methods are empty
	/**
	 * Called when a key is pressed/released/repeated.
	 * See <a target="_top" href="http://www.glfw.org/docs/latest/input_guide.html#input_key">here</a> for more info.
	 * @param key Key code pressed. GLFW_KEY_*
	 * @param scancode Platform specific scancode
	 * @param action GLFW_PRESSED, GLFW_RELEASED or GLFW_REPEAT
	 * @param mods Modifier bits. See GLFW_MOD_{SHIFT, CONTROL, ALT, SUPER}
	 */
	default void handleKey(int key, int scancode, int action, int mods) {}
	/**
	 * Called when a text is input.
	 * See <a target="_top" href="http://www.glfw.org/docs/latest/input_guide.html#input_text">here</a> for more info.
	 * @param c The character input
	 */
	default void handleChar(char c) {}
	/**
	 * Called when the cursor is moved.
	 * See <a target="_top" href="http://www.glfw.org/docs/latest/input_guide.html#cursor_pos">here</a> for more info.
	 * @param xpos The x co-ordinate relative to the bottom left of the window
	 * @param ypos The y co-ordinate relative to the bottom left of the window
	 */
	default void handleCursorPos(double xpos, double ypos) {}
	/**
	 * Called when a mouse button is pressed/released.
	 * See <a target="_top" href="http://www.glfw.org/docs/latest/input_guide.html#input_mouse_button">here</a> for more info.
	 * @param button The mouse button pressed/released. GLFW_MOUSE_BUTTON_*
	 * @param action GLFW_PRESS or GLFW_RELEASE
	 * @param mods Modifier bits. See GLFW_MOD_{SHIFT, CONTROL, ALT, SUPER}
	 */
	default void handleMouseButton(int button, int action, int mods) {}
	/**
	 * Called when the use scrolls the mouse.
	 * See <a target="_top" href="http://www.glfw.org/docs/latest/input_guide.html#scrolling">here</a> for more info.
	 * @param xoffset The x offset
	 * @param yoffset The y offset
	 */
	default void handleScroll(double xoffset, double yoffset) {}
	/**
	 * Called when the window is resized.
	 * @param w The new window width, in pixels
	 * @param h The new window height, in pixels
	 */
	default void handleResize(int w, int h) {}
}
