package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.render.IRenderer;
import game.world.ClientWorld;

public class LobbyUI extends UI implements InputPipeMulti {
	
	/** The renderer. Used for getting the window width and height */
	private IRenderer renderer;
	
	/** The list of objects to redirect input to */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	/** The start button */
	private ButtonComponent joinButton;
	/** The exit button */
	private ButtonComponent backButton;
	/** The background image */
	private ImageComponent backgroundImage;
	/** The next UI to return */
	private UI nextUI = this;
	
	/** The window width */
	private float windowW;
	/** The window height */
	private float windowH;

	public LobbyUI(IRenderer _renderer) {
		
		super();
		this.renderer = _renderer;
		
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		
		joinButton = new ButtonComponent(
			() -> { this.nextUI = new GameUI(ClientWorld.createTestWorld()); },
			100, 100,
			renderer.getImageBank().getTexture("joinDefault.png"),
			renderer.getImageBank().getTexture("joinHover.png"),
			renderer.getImageBank().getTexture("joinPressed.png")
		);
			
		backButton = new ButtonComponent(
			() -> { this.nextUI = new StartUI(renderer); },
			100, 100,
			renderer.getImageBank().getTexture("backDefault.png"),
			renderer.getImageBank().getTexture("backHover.png"),
			renderer.getImageBank().getTexture("backPressed.png")
		);
			
		backgroundImage = new ImageComponent(
			0, 0, renderer.getImageBank().getTexture("Start_BG.png")
		);
		
		this.inputHandlers.add(joinButton);
		this.inputHandlers.add(backButton);
		
	}

	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}

	@Override
	public void update(double dt) {
		windowW = renderer.getWidth();
		windowH = renderer.getHeight();
		joinButton.update(dt);
		backButton.update(dt);
		
	}

	@Override
	public void render(IRenderer r) {
		joinButton.setX((int) (windowW/2.0 - backButton.getWidth()/2.0));
		joinButton.setY((int) (windowH/2.0 - backButton.getHeight()/2.0) + 140);
		backButton.setX((int) (windowW/2.0 - backButton.getWidth()/2.0));
		backButton.setY((int) (windowH/2.0 - backButton.getHeight()/2.0));
		backgroundImage.render(r);
		joinButton.render(r);
		backButton.render(r);
		
	}

	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public String toString() {
		return "LobbyUI";
	}

}
