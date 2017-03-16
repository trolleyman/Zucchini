package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.*;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;

/**
 * The StartUI is the UI responsible for rendering the starting UI of the program
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
	/** The help button */
	private ButtonComponent helpButton;
	/** The exit button */
	private ButtonComponent exitButton;
	/** The next UI to return */
	private UI nextUI = this;
	
	private ImageComponent backgroundImage;
	
	public StartUI(UI ui) {
		super(ui);
		setup();
	}
	
	private void setup() {
		// Create Start Button
		startButton = new ButtonComponent(
				() -> this.nextUI = new LobbyUI(this),
				Align.BL, 100, 100,
				textureBank.getTexture("startDefault.png"),
				textureBank.getTexture("startHover.png"),
				textureBank.getTexture("startPressed.png")
		);
		
		// Create Help Button
		helpButton = new ButtonComponent(
				() -> this.nextUI = new HelpUI(this),
				Align.BL, 100, 100,
				textureBank.getTexture("helpDefault.png"),
				textureBank.getTexture("helpHover.png"),
				textureBank.getTexture("helpPressed.png")
		);
		
		// Create Exit Button
		exitButton = new ButtonComponent(
				() -> { this.nextUI = null; },
				Align.BL, 100, 100,
				textureBank.getTexture("exitButtonDefault.png"),
				textureBank.getTexture("exitButtonHover.png"),
				textureBank.getTexture("exitButtonPressed.png")
		);
		
		// Create Background Image
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);
		
		// Add buttons to input handlers
		this.inputHandlers.add(startButton);
		this.inputHandlers.add(helpButton);
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
		helpButton.update(dt);
		exitButton.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		// Render the background image
		backgroundImage.render(r);

		// Set the location of the buttons
		startButton.setX((int) (windowW/2.0 - startButton.getWidth()/2.0));
		startButton.setY((int) (windowH/2.0 - startButton.getHeight()/2.0));
		helpButton.setX((int) (windowW/2.0 - startButton.getWidth()/2.0));
		helpButton.setY((int) (windowH/2.0 - startButton.getHeight()/2.0 - 150));
		exitButton.setX((int) (windowW - (exitButton.getWidth()) - 20.0));
		exitButton.setY((int) (windowH - (exitButton.getHeight()) - 20.0));

		// Render the buttons
		startButton.render(r);
		helpButton.render(r);
		exitButton.render(r);
	}
	
	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		// Nothing to destroy
	}
}
