/**
 * 
 */
package game.ui;

import org.lwjgl.glfw.GLFW;

import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public class StartUI extends UI {
	
	public StartUI() {
		
	}
	
	@Override
	public void update(double dt) {
		
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawTexture("xxx", 0, 0, 0);
	}
	
	@Override
	public UI next() {
		return this;
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		String actionStr = "";
		switch (action) {
		case GLFW.GLFW_PRESS  : actionStr = "pressed "; break;
		case GLFW.GLFW_RELEASE: actionStr = "released"; break;
		case GLFW.GLFW_REPEAT : actionStr = "repeated"; break;
		}
		
		String modsStr = "";
		if ((mods & GLFW.GLFW_MOD_SHIFT) != 0)
			modsStr += "SHIFT ";
		if ((mods & GLFW.GLFW_MOD_CONTROL) != 0)
			modsStr += "CTRL ";
		if ((mods & GLFW.GLFW_MOD_ALT) != 0)
			modsStr += "ALT ";
		if ((mods & GLFW.GLFW_MOD_SUPER) != 0)
			modsStr += "SUPER ";
		
		if (!modsStr.equals(""))
			modsStr = "Mods: " + modsStr;
		
		String keyName = GLFW.glfwGetKeyName(key, scancode);
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
