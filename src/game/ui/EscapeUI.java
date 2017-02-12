package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.render.Align;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;

/**
 * 
 * @author Abby Wiggins
 *
 */

public class EscapeUI extends UI implements InputPipeMulti{
	
	private TextureBank bank;
	
	private ButtonComponent fileBtn;
	private ButtonComponent helpBtn;
	private ButtonComponent audioBtn;
	private ButtonComponent continueBtn;
	private ButtonComponent quitBtn;
	private int buttonWidth;
	private int buttonHeight;
	private ClientWorld world;
	
	private UI nextUI;
	
	private float winWidth;
	private float winHeight;
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();

	public EscapeUI(AudioManager audio, TextureBank _bank, ClientWorld _world) {
		super(audio);
		this.bank = _bank;
		nextUI = this;
		this.world = _world;

		buttonHeight = 200;
		buttonWidth = 200;
		
		start(); //java convention to keep constructor under 10 lines
	}
	
	public void start(){
		fileBtn = new ButtonComponent(null,
				Align.BL, 0, 0,
				bank.getTexture("file.png"),
				bank.getTexture("file2.png"),
				bank.getTexture("file2.png")
		);
		
		helpBtn = new ButtonComponent(null,
				Align.BL, 0, 0,
				bank.getTexture("help.png"),
				bank.getTexture("help2.png"),
				bank.getTexture("help2.png")
		);
		
		audioBtn = new ButtonComponent(null,
				Align.BL, 0, 0,
				bank.getTexture("audio.png"),
				bank.getTexture("audio2.png"),
				bank.getTexture("audio2.png")
		);
		
		quitBtn = new ButtonComponent(
				() -> { this.nextUI = new StartUI(audio, bank); },
				Align.BL, 0, 0,
				bank.getTexture("quit.png"),
				bank.getTexture("quit2.png"),
				bank.getTexture("quit2.png")
		);
		
		continueBtn = new ButtonComponent(
				() -> { this.nextUI = new GameUI(audio, bank, world); },
				Align.BL, 0, 0,
				bank.getTexture("continue.png"),
				bank.getTexture("continue2.png"),
				bank.getTexture("continue2.png")
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
		int btnSize = audioBtn.getHeight();
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
		// TODO Auto-generated method stub
		return nextUI;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Escape UI";
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
