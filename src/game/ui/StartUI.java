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
	
	private double w = 256.0;
	private double h = 256.0;
	
	private double speed = 200.0;
	private double xSpeed;
	private double ySpeed;
	
	private double windowW = 1000.0;
	private double windowH = 1000.0;
	
	private boolean overButton;
	private int mx = 0;
	private int my = 0;
	
	public StartUI(KeyboardManager _km) {
		super(_km);
		
		x = 100.0;
		y = 100.0;
		xSpeed = speed;
		ySpeed = speed;
	}
	
	@Override
	public void update(double dt) {
		x += xSpeed * dt;
		y += ySpeed * dt;
		
		if (x < 0.0) {
			x = 0.0;
			xSpeed = speed;
		}
		if (x > windowW - w) {
			x = windowW - w;
			xSpeed = -speed;
		}
		if (y < 0) {
			y = 0.0;
			ySpeed = speed;
		}
		if (y > windowH - h) {
			y = windowH - h;
			ySpeed = -speed;
		}
		
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawImage("test.png", (int)x, (int)y);
		w = r.getImage("test.png").getWidth();
		h = r.getImage("test.png").getHeight();
		
		windowW = r.getWidth();
		windowH = r.getHeight();
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
	public void handleChar(char c) {}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		if ((xpos == (x+w)) && (ypos == (y+h))) {
			overButton = true;
		} else {
			overButton = false;
		}
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		
	}

	@Override
	public void handleScroll(double xoffset, double yoffset) {}

}
