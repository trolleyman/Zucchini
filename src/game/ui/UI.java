/**
 * 
 */
package game.ui;

import game.InputHandler;
import game.render.IRenderer;

/**
 * @author jackm
 *
 */
public abstract class UI implements InputHandler {

	void update(double dt) {
		
	}
	
	void render(IRenderer r) {
		
	}
	
	UI next() {
		return null;
	}
	
}
