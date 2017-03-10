package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.render.Align;
import game.render.IRenderer;
import game.ui.component.ButtonComponent;
import game.world.ClientWorld;

/**
 * 
 * @author Abby Wiggins
 *
 */

public class EscapeUI extends UI implements InputPipeMulti {
	
	private ButtonComponent fileBtn;
	private ButtonComponent helpBtn;
	private ButtonComponent audioBtn;
	private ButtonComponent continueBtn;
	private ButtonComponent quitBtn;
	private int buttonWidth;
	private int buttonHeight;
	private ClientWorld world;
	
	private boolean destroy = false;
	private UI nextUI;
	
	private float winWidth;
	private float winHeight;
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	public EscapeUI(UI _ui, ClientWorld _world) {
		super(_ui);
		nextUI = this;
		this.world = _world;

		buttonHeight = 200;
		buttonWidth = 200;
		
		start(); //java convention to keep constructor under 10 lines
	}
	
	public void start() {
		fileBtn = new ButtonComponent(null,
				Align.BL, 0, 0,
				textureBank.getTexture("file.png"),
				textureBank.getTexture("file2.png"),
				textureBank.getTexture("file2.png")
		);
		
		helpBtn = new ButtonComponent(null,
				Align.BL, 0, 0,
				textureBank.getTexture("help.png"),
				textureBank.getTexture("help2.png"),
				textureBank.getTexture("help2.png")
		);
		
		audioBtn = new ButtonComponent(null,
				Align.BL, 0, 0,
				textureBank.getTexture("audio.png"),
				textureBank.getTexture("audio2.png"),
				textureBank.getTexture("audio2.png")
		);
		
		quitBtn = new ButtonComponent(
				() -> {
					this.destroy = true;
					this.nextUI = new StartUI(this);
				},
				Align.BL, 0, 0,
				textureBank.getTexture("quit.png"),
				textureBank.getTexture("quit2.png"),
				textureBank.getTexture("quit2.png")
		);
		
		continueBtn = new ButtonComponent(
				() -> this.nextUI = new GameUI(this, world),
				Align.BL, 0, 0,
				textureBank.getTexture("continue.png"),
				textureBank.getTexture("continue2.png"),
				textureBank.getTexture("continue2.png")
		);
		
		this.inputHandlers.add(fileBtn);
		this.inputHandlers.add(helpBtn);
		this.inputHandlers.add(audioBtn);
		this.inputHandlers.add(quitBtn);
		this.inputHandlers.add(continueBtn);
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	

	@Override
	public void handleResize(int w, int h) {
		this.winWidth = w;
		this.winHeight = h;
		InputPipeMulti.super.handleResize(w, h);
		float btnSize = audioBtn.getHeight();
		System.out.println(btnSize);
	}

	@Override
	public void update(double dt) {
		
		fileBtn.update(dt);
		helpBtn.update(dt);
		audioBtn.update(dt);
		quitBtn.update(dt);
		continueBtn.update(dt);
	}

	@Override
	public void render(IRenderer r) {
		float height = winHeight - 100;
		fileBtn.setX(0);
		fileBtn.setY((int) height);
		helpBtn.setX(0);
		helpBtn.setY((int) height - (buttonHeight));
		audioBtn.setX(0);
		audioBtn.setY((int) height - (2*buttonHeight));;
		quitBtn.setX(0);
		quitBtn.setY((int) height - (3*buttonHeight));
		continueBtn.setX(0);
		continueBtn.setY((int) height - (4*buttonHeight));
		
		fileBtn.render(r);
		helpBtn.render(r);
		audioBtn.render(r);
		quitBtn.render(r);
		continueBtn.render(r);
		
	}

	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public String toString() {
		return "Escape UI";
	}

	@Override
	public void destroy() {
		if (destroy)
			this.world.destroy();
	}
}
