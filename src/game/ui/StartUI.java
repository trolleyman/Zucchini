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
	
	private UIButton startButton;
	private UIButton exitButton;
	private UI nextUI = this;
	float windowW;
	float windowH;
	
	
	public StartUI(IRenderer renderer) {
		super(renderer);
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		
		startButton = new UIButton(
			() -> { this.nextUI = new GameUI(renderer, null); },
			100, 100,
			renderer.getImageBank().getTexture("buttonDefault.png"),
			renderer.getImageBank().getTexture("buttonHover.png"),
			renderer.getImageBank().getTexture("buttonPressed.png")
		);
		
		exitButton = new UIButton(
				() -> { /* TODO Exit */ },
				100, 100,
				renderer.getImageBank().getTexture("exitButtonDefault.png"),
				renderer.getImageBank().getTexture("exitButtonHover.png"),
				renderer.getImageBank().getTexture("exitButtonPressed.png")
			);
		
		//renderer.drawImage(renderer.getImageBank().getTexture("startBackground.png"), 500, 400);
		
		this.inputHandlers.add(exitButton);
		this.inputHandlers.add(startButton);
	}
	
	@Override
	public void update(double dt) {
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		startButton.update(dt);
		startButton.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		startButton.setX((int) (windowW/2.0 - startButton.getWidth()/2.0));
		startButton.setY((int) (windowH/2.0 - startButton.getHeight()/2.0));
		exitButton.setX((int) (windowW - (exitButton.getWidth()) - 20.0));
		exitButton.setY((int) (windowH - (exitButton.getHeight()) - 20.0));
		startButton.render(r);
		exitButton.render(r);
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
