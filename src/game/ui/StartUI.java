/**
 * 
 */
package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.render.Align;
import game.render.IRenderer;
import game.world.ClientWorld;
import game.world.TestMap;
import game.world.World;

/**
 * The StartUI is the UI responsible for rendering the starting UI of the program
 * 
 * @author Jack
 */
public class StartUI extends UI implements InputPipeMulti {
	
	/** The renderer. Used for getting the window width and height */
	private IRenderer renderer;
	
	/** The list of objects to redirect input to */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	/** The start button */
	private ButtonComponent startButton;
	/** The exit button */
	private ButtonComponent exitButton;
	/** The background image */
	private ImageComponent backgroundImage;
	/** The next UI to return */
	private UI nextUI = this;
	
	/** The window width */
	private float windowW;
	/** The window height */
	private float windowH;

	private float testRot;
	
	/**
	 * Constructs a new StartUI with a specified renderer to gather the window width and height
	 * @param _renderer The renderer
	 */
	public StartUI(IRenderer _renderer) {
		super();
		this.renderer = _renderer;
		
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		
		startButton = new ButtonComponent(
			() -> { this.nextUI = new LobbyUI(renderer); },
			Align.BL, 100, 100,
			renderer.getImageBank().getTexture("buttonDefault.png"),
			renderer.getImageBank().getTexture("buttonHover.png"),
			renderer.getImageBank().getTexture("buttonPressed.png")
		);
		
		exitButton = new ButtonComponent(
			() -> { this.nextUI = null; },
			Align.BL, 100, 100,
			renderer.getImageBank().getTexture("exitButtonDefault.png"),
			renderer.getImageBank().getTexture("exitButtonHover.png"),
			renderer.getImageBank().getTexture("exitButtonPressed.png")
		);
		
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, renderer.getImageBank().getTexture("Start_BG.png"), 0.0f
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
		testRot += 3.0f * dt;
	}
	
	@Override
	public void render(IRenderer r) {
		startButton.setX((int) (windowW/2.0 - startButton.getWidth()/2.0));
		startButton.setY((int) (windowH/2.0 - startButton.getHeight()/2.0));
		exitButton.setX((int) (windowW - (exitButton.getWidth()) - 20.0));
		exitButton.setY((int) (windowH - (exitButton.getHeight()) - 20.0));
		backgroundImage.render(r);
		startButton.render(r);
		exitButton.render(r);
		r.drawTexture(r.getImageBank().getTexture("test.png"), Align.MM, 200, 200, testRot);
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
