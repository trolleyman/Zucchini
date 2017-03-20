package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.ui.component.ButtonComponent;
import game.ui.component.MuteComponent;
import game.ui.component.VolumeComponent;
import game.world.ClientWorld;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Abby Wiggins
 */

public class EscapeUI extends UI implements InputPipeMulti {
	private static final Vector4f ESCAPE_COLOR = new Vector4f(0.1f, 0.1f, 0.1f, 0.7f);
	private static final float BUTTON_PADDING = 8;
	
	private ButtonComponent continueBtn;
	private ButtonComponent helpBtn;
	private ButtonComponent quitBtn;
	
	/** The mute toggle button */
	private MuteComponent muteComponent;
	/** The volume slider */
	private VolumeComponent volumeComponent;
	
	private ClientWorld world;
	
	private boolean destroy = false;
	private UI nextUI;
	
	private float windowW;
	private float windowH;
	
	private boolean help;
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private Font font;

	/**
	 * Constructs a new EscapeUI
	 * @param _ui The UI superclass
	 * @param _world The client world
	 */
	public EscapeUI(UI _ui, ClientWorld _world) {
		super(_ui);
		nextUI = this;
		
		this.world = _world;
		help = false;
		font = fontBank.getFont("emulogic.ttf");

		start(); //java convention to keep constructor under 10 lines
	}

	/**
	 * Helper function for the constructor
	 */
	public void start() {
		continueBtn = new ButtonComponent(
				() -> this.nextUI = new GameUI(this, world),
				Align.BL, 0, 0,
				textureBank.getTexture("continuebtn.png"),
				textureBank.getTexture("continueclicked.png"),
				textureBank.getTexture("continueclicked.png")
		);
		
		helpBtn = new ButtonComponent(
				() -> this.nextUI = new HelpUI(this, () -> new EscapeUI(this, world)),
				Align.BL, 0, 0,
				textureBank.getTexture("helpbtn.png"),
				textureBank.getTexture("helpclicked.png"),
				textureBank.getTexture("helpclicked.png")
		);
		
		quitBtn = new ButtonComponent(
				() -> {
					this.destroy = true;
					this.nextUI = new StartUI(this);
				},
				Align.BL, 0, 0,
				textureBank.getTexture("quitbtn.png"),
				textureBank.getTexture("quitclicked.png"),
				textureBank.getTexture("quitclicked.png")
		);
		
		// Create Mute Button
		muteComponent = new MuteComponent(Align.BL, 100, 100, audio, textureBank);
		
		// Create Volume Slider
		volumeComponent = new VolumeComponent(20.0f, 20.0f, audio);

		// Add input handlers
		this.inputHandlers.add(continueBtn);
		this.inputHandlers.add(helpBtn);
		this.inputHandlers.add(quitBtn);
		this.inputHandlers.add(muteComponent);
		this.inputHandlers.add(volumeComponent);
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		if (action == GLFW_PRESS && key == GLFW_KEY_ESCAPE) {
			this.nextUI = new GameUI(this, world);
		}
	}
	
	@Override
	public void handleResize(int w, int h) {
		this.windowW = w;
		this.windowH = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
		if (this.nextUI != this)
			this.nextUI = this;
		
		continueBtn.update(dt);
		helpBtn.update(dt);
		quitBtn.update(dt);
		
		// Volume stuff
		muteComponent.update(dt);
		volumeComponent.update(dt);
		
		// Update world
		world.update(dt);
	}

	@Override
	public void render(IRenderer r) {
		world.render(r);
		
		r.drawBox(Align.BL, 0.0f, 0.0f, windowW, windowH, ESCAPE_COLOR);
		
		continueBtn.setX(r.getWidth()/2 - continueBtn.getWidth()/2);
		continueBtn.setY(r.getHeight()/2 + helpBtn.getHeight()/2 + BUTTON_PADDING);
		helpBtn.setX(r.getWidth()/2 - helpBtn.getWidth()/2);
		helpBtn.setY(r.getHeight()/2 - helpBtn.getHeight()/2);
		quitBtn.setX(r.getWidth()/2 - quitBtn.getWidth()/2);
		quitBtn.setY(helpBtn.getY() - BUTTON_PADDING - quitBtn.getHeight());
		
		muteComponent.setX(20.0f);
		muteComponent.setY(windowH - muteComponent.getHeight() - 20.0f);
		volumeComponent.setX(muteComponent.getX() + muteComponent.getWidth()/2 - VolumeComponent.WIDTH/2);
		volumeComponent.setY(muteComponent.getY() - VolumeComponent.HEIGHT - 20.0f);
		
		continueBtn.render(r);
		helpBtn.render(r);
		quitBtn.render(r);
		
		muteComponent.render(r);
		volumeComponent.render(r);
		
	/*	if(help){
			r.drawText(font, "This will cause you to quit your game.", Align.BL, false, windowW/3, windowH-100, 0.6f);
			r.drawText(font, "Do you wish to continue?", Align.TL, false, windowW/3, windowH - 200, 0.6f);
			
			ButtonComponent leaveBtn = new ButtonComponent(
					() -> {
						this.destroy = true;
						this.nextUI = new HelpUI(this);
					},
					Align.BL, windowW/3, windowH/2,
					textureBank.getTexture("quitbtn.png"),
					textureBank.getTexture("quitclicked.png"),
					textureBank.getTexture("quitclicked.png")
			);
			
			leaveBtn.render(r);
			
			ButtonComponent backBtn = new ButtonComponent(
					() -> this.nextUI = new GameUI(this, world),
					Align.TL, windowW/3 + 500, windowH/2,
					textureBank.getTexture("backDefault.png"),
					textureBank.getTexture("backHover.png"),
					textureBank.getTexture("backPressed.png")
			);
			
			backBtn.render(r);
			
			this.inputHandlers.add(leaveBtn);
			this.inputHandlers.add(backBtn);
			
			
		}*/
		
	}
	

	
	@Override
	public UI next() {
		return nextUI;
	}
	
	@Override
	public void destroy() {
		if (destroy)
			this.world.destroy();
	}
}
