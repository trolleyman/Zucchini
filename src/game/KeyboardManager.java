package game;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardManager {
	
	private long window;
	
	public KeyboardManager(long _window) {
		this.window = _window;
	}
	
	public int getKeyState(int key) {
		return glfwGetKey(window, key);
	}
}
