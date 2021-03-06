package game.ui;

import java.util.ArrayList;

import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.*;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.ui.component.MuteComponent;
import game.ui.component.VolumeComponent;

import static org.lwjgl.glfw.GLFW.*;

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
	protected ButtonComponent startButton;
	/** The help button */
	protected ButtonComponent helpButton;
	/** The exit button */
	protected ButtonComponent exitButton;
	/** The mute toggle button */
	protected MuteComponent muteComponent;
	/** The volume slider */
	private VolumeComponent volumeComponent;
	
	/** The next UI to return */
	private UI nextUI = this;
	private ImageComponent backgroundImage;

	/**
	 * Constructs a StartUI
	 * @param ui The UI superclass
	 */
	public StartUI(UI ui) {
		super(ui);
		setup();
	}

	/**
	 * Helper function for constructor
	 */
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
				() -> this.nextUI = new HelpUI(this, () -> new StartUI(this)),
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
		
		// Create Mute Button
		muteComponent = new MuteComponent(Align.BL, 100, 100, audio, textureBank);
		
		// Create Volume Slider
		volumeComponent = new VolumeComponent(20.0f, 20.0f, audio);
		
		// Create Background Image
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);
		
		// Add buttons to input handlers
		this.inputHandlers.add(startButton);
		this.inputHandlers.add(helpButton);
		this.inputHandlers.add(exitButton);
		this.inputHandlers.add(muteComponent);
		this.inputHandlers.add(volumeComponent);
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
		muteComponent.update(dt);
		volumeComponent.update(dt);
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
		muteComponent.setX(20.0f);
		muteComponent.setY(windowH - muteComponent.getHeight() - 20.0f);
		volumeComponent.setX(muteComponent.getX() + muteComponent.getWidth()/2 - VolumeComponent.WIDTH/2);
		volumeComponent.setY(muteComponent.getY() - VolumeComponent.HEIGHT - 20.0f);
		
		// Render the buttons
		startButton.render(r);
		helpButton.render(r);
		exitButton.render(r);
		muteComponent.render(r);
		volumeComponent.render(r);
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
