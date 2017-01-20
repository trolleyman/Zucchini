/**
 * 
 */
package game.ui;

import static org.lwjgl.glfw.GLFW.*;

import game.KeyboardManager;
import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public class StartUI extends UI {
	
	private double x;
	private double y;
	
	public StartUI(KeyboardManager _km) {
		super(_km);
		
		x = 100.0;
		y = 100.0;
	}
	
	@Override
	public void update(double dt) {
		double rate = 200.0;
		double diff = rate * dt;
		if (km.getKeyState(GLFW_KEY_UP) == GLFW_PRESS) {
			y -= diff;
		}
		if (km.getKeyState(GLFW_KEY_DOWN) == GLFW_PRESS) {
			y += diff;
		}
		if (km.getKeyState(GLFW_KEY_LEFT) == GLFW_PRESS) {
			x -= diff;
		}
		if (km.getKeyState(GLFW_KEY_RIGHT) == GLFW_PRESS) {
			x += diff;
		}
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawTexture("test.png", (int)x, (int)y);
	}
	
	@Override
	public UI next() {
		return this;
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		printKey(key, scancode, action, mods);
	}
	
	private void printKey(int key, int scancode, int action, int mods) {
		String actionStr = "";
		switch (action) {
		case GLFW_PRESS  : actionStr = "pressed "; break;
		case GLFW_RELEASE: actionStr = "released"; break;
		case GLFW_REPEAT : actionStr = "repeated"; break;
		}
		
		String modsStr = "";
		if ((mods & GLFW_MOD_SHIFT) != 0)
			modsStr += "SHIFT ";
		if ((mods & GLFW_MOD_CONTROL) != 0)
			modsStr += "CTRL ";
		if ((mods & GLFW_MOD_ALT) != 0)
			modsStr += "ALT ";
		if ((mods & GLFW_MOD_SUPER) != 0)
			modsStr += "SUPER ";
		
		if (!modsStr.equals(""))
			modsStr = "Mods: " + modsStr;
		
		String keyName = glfwGetKeyName(key, scancode);
		if (keyName == null)
			keyName = key + ":" + scancode;
		
		System.out.println(String.format("Key %s: %-8s %s", actionStr, keyName, modsStr));
	}

	@Override
	public void handleChar(char c) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleCursorPos(double _xpos, double _ypos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleScroll(double xoffset, double yoffset) {
		// TODO Auto-generated method stub
		
	}

}
