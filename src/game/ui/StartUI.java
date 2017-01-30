/**
 * 
 */
package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.render.IRenderer;
import game.world.ClientWorld;
import game.world.TestMap;
import game.world.World;

/**
 * @author jackm
 *
 */
public class StartUI extends UI implements InputPipeMulti {
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
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
			() -> { this.nextUI = new GameUI(renderer, ClientWorld.createTestWorld()); },
			100, 100,
			renderer.getImageBank().getTexture("buttonDefault.png"),
			renderer.getImageBank().getTexture("buttonHover.png"),
			renderer.getImageBank().getTexture("buttonPressed.png")
		);
		
		exitButton = new UIButton(
				() -> { this.nextUI = null; },
				100, 100,
				renderer.getImageBank().getTexture("exitButtonDefault.png"),
				renderer.getImageBank().getTexture("exitButtonHover.png"),
				renderer.getImageBank().getTexture("exitButtonPressed.png")
			);
				
		this.inputHandlers.add(startButton);
		this.inputHandlers.add(exitButton);
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	
	@Override
	public void update(double dt) {
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		startButton.update(dt);
		exitButton.update(dt);
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
