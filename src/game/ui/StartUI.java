/**
 * 
 */
package game.ui;

import game.KeyboardManager;
import game.render.IRenderer;
import game.render.TextureBank;

/**
 * @author jackm
 *
 */
public class StartUI extends UI {
	
	private UIComponent button;
	private UI nextUI = this;
	
	public StartUI(KeyboardManager _km, TextureBank ib) {
		super(_km);
		
		button = new UIButton(
			() -> { this.nextUI = new GameUI(_km, null); },
			100, 100,
			ib.getTexture("buttonDefault.png"),
			ib.getTexture("buttonHover.png"),
			ib.getTexture("buttonPressed.png")
		);
		this.inputHandlers.add(button);
	}
	
	@Override
	public void update(double dt) {
		button.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		button.render(r);
	}
	
	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public String toString() {
		return "StartUI";
	}
}
