package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.render.TextureBank;

/**
 * The StartUI is the UI responsible for rendering the starting UI of the program
 * 
 * @author Jack
 */
public class StartUI extends UI implements InputPipeMulti {
	/** The current window width */
	private int windowW;
	/** The current window height */
	private int windowH;
	
	/** The list of objects to redirect input to */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	/** The start button */
	private ButtonComponent startButton;
	/** The exit button */
	private ButtonComponent exitButton;
	/** The next UI to return */
	private UI nextUI = this;
	

	private Font font;
	private ImageComponent backgroundImage;
	
	/**
	 * Constructs a new StartUI
	 * @param tb TextureBank used to get textures for components
	 */
	public StartUI(IClientConnection conn, AudioManager audio, TextureBank tb) {
		super(conn, audio);
		
		font = new Font(Util.getBasePath() + "resources/fonts/terminal2.ttf");
		
		startButton = new ButtonComponent(
			() -> { this.nextUI = new LobbyUI(connection, audio, tb); },
			Align.BL, 100, 100,
			tb.getTexture("buttonDefault.png"),
			tb.getTexture("buttonHover.png"),
			tb.getTexture("buttonPressed.png")
		);
		
		exitButton = new ButtonComponent(
			() -> { this.nextUI = null; },
			Align.BL, 100, 100,
			tb.getTexture("exitButtonDefault.png"),
			tb.getTexture("exitButtonHover.png"),
			tb.getTexture("exitButtonPressed.png")
		);
		
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, tb.getTexture("Start_BG.png"), 0.0f
		);

		this.inputHandlers.add(startButton);
		this.inputHandlers.add(exitButton);
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
		startButton.update(dt);
		exitButton.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
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

	@Override
	public void destroy() {
		// Nothing to destroy
	}
}
