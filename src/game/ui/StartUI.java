/**
 * 
 */
package game.ui;

import game.KeyboardManager;
import game.Util;
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
	private double mx = 0;
	private double my = 0;
	
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
		
		if ((x < mx && mx < (x+w)) && (y < my && my < (y+h))) {
			overButton = true;
		} else {
			overButton = false;
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
		Util.printKey(key, scancode, action, mods);
	}
	
	@Override
	public void handleChar(char c) {}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		
		mx = xpos;
		my = ypos;
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		
	}

	@Override
	public void handleScroll(double xoffset, double yoffset) {}
	
}
