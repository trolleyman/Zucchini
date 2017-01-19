/**
 * 
 */
package game.ui;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleChar(char c) {
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
