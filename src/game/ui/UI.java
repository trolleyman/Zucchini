/**
 * 
 */
package game.ui;

import game.InputHandler;

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
