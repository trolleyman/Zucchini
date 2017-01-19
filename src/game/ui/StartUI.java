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
		System.out.println("Key event: " + key + ", " + scancode + ", " + action + ", " + mods);
		
		String actionStr = "";
		switch (action) {
		case GLFW.GLFW_PRESS  : actionStr = "pressed" ; break;
		case GLFW.GLFW_RELEASE: actionStr = "released"; break;
		case GLFW.GLFW_PRESS: actionStr = "pressed"; break;
		case GLFW.GLFW_PRESS: actionStr = "pressed"; break;
		}
		System.out.println("Key " + actionStr + ": " + GLFW.glfwGetKeyName(key, scancode) + ". Modifiers: " + modsStr);
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
