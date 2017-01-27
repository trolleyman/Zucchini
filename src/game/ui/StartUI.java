/**
 * 
 */
package game.ui;

import game.KeyboardManager;
import game.render.IRenderer;
import game.render.Renderer;
import game.render.TextureBank;

/**
 * @author jackm
 *
 */
public class StartUI extends UI {
	
	private UIButton button;
	private UI nextUI = this;
	float windowW;
	float windowH;
	
	
	public StartUI(IRenderer renderer) {
		super(renderer);
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		
		button = new UIButton(
			() -> { this.nextUI = new GameUI(renderer, null); },
			100, 100,
			renderer.getImageBank().getTexture("buttonDefault.png"),
			renderer.getImageBank().getTexture("buttonHover.png"),
			renderer.getImageBank().getTexture("buttonPressed.png")
		);
		this.inputHandlers.add(button);
	}
	
	@Override
	public void update(double dt) {
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		button.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		button.setX((int) (windowW/2.0 - button.getWidth()/2.0));
		button.setY((int) (windowH/2.0 - button.getHeight()/2.0));
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
