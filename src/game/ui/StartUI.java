/**
 * 
 */
package game.ui;

import java.awt.Color;

import game.KeyboardManager;
import game.Util;
import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public class StartUI extends UI {
	
	private double testBoxX;
	private double testBoxY;
	
	private double testBoxW = 256.0;
	private double testBoxH = 256.0;
	
	private double speed = 200.0;
	private double xSpeed;
	private double ySpeed;
	
	private double windowW = 1000.0;
	private double windowH = 1000.0;
	
	private boolean overButton;
	private double mX = 0;
	private double mY = 0;
	
	public StartUI(KeyboardManager _km) {
		super(_km);
		
		testBoxX = 100.0;
		testBoxY = 100.0;
		xSpeed = speed;
		ySpeed = speed;
	}
	
	@Override
	public void update(double dt) {
		testBoxX += xSpeed * dt;
		testBoxY += ySpeed * dt;
		
		if (testBoxX < 0.0) {
			testBoxX = 0.0;
			xSpeed = speed;
		}
		if (testBoxX > windowW - testBoxW) {
			testBoxX = windowW - testBoxW;
			xSpeed = -speed;
		}
		if (testBoxY < 0) {
			testBoxY = 0.0;
			ySpeed = speed;
		}
		if (testBoxY > windowH - testBoxH) {
			testBoxY = windowH - testBoxH;
			ySpeed = -speed;
		}
		
		if ((testBoxX < mX && mX < (testBoxX+testBoxW)) && (testBoxY < mY && mY < (testBoxY+testBoxH))) {
			overButton = true;
		} else {
			overButton = false;
		}
	}
	
	@Override
	public void render(IRenderer r) {
		Color c;
		if (this.overButton)
			c = Color.RED;
		else
			c = Color.WHITE;
		
		r.drawBox((float)testBoxX, (float)testBoxY, (float)testBoxW, (float)testBoxH, c);
		
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
		mX = xpos;
		mY = ypos;
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		
	}

	@Override
	public void handleScroll(double xoffset, double yoffset) {}
	
}
