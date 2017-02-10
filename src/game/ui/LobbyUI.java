package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.render.Align;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;

public class LobbyUI extends UI implements InputPipeMulti {
	/** The current window width */
	private int windowW;
	/** The current window height */
	private int windowH;
	
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

	public LobbyUI(TextureBank tb) {
		super();
		
		joinButton = new ButtonComponent(
			() -> { this.nextUI = new GameUI(tb, ClientWorld.createTestWorld()); },
			Align.BL, 100, 100,
			tb.getTexture("joinDefault.png"),
			tb.getTexture("joinHover.png"),
			tb.getTexture("joinPressed.png")
		);
		
		backButton = new ButtonComponent(
			() -> { this.nextUI = new StartUI(tb); },
			Align.BL, 100, 100,
			tb.getTexture("backDefault.png"),
			tb.getTexture("backHover.png"),
			tb.getTexture("backPressed.png")
		);
		
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, tb.getTexture("Start_BG.png"), 0.0f
		);
		
		this.inputHandlers.add(joinButton);
		this.inputHandlers.add(backButton);
	}

	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	
	@Override
	public void handleResize(int w, int h) {
		this.windowW = w;
		this.windowH = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
		joinButton.update(dt);
		backButton.update(dt);
	}

	@Override
	public void render(IRenderer r) {
		joinButton.setX((int) (windowW/2.0 - joinButton.getWidth()/2.0));
		joinButton.setY((int) (windowH/2.0 - joinButton.getHeight()/2.0) + 140);
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

	@Override
	public void destroy() {
		// Nothing to destroy
	}

}
