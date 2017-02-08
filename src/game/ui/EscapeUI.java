package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.render.Align;
import game.render.IRenderer;

/**
 * 
 * @author Abby Wiggins
 *
 */

public class EscapeUI extends UI implements InputPipeMulti{
	
	private IRenderer renderer;
	
	private ButtonComponent fileBtn;
	private ButtonComponent helpBtn;
	private ButtonComponent audioBtn;
	private ButtonComponent continueBtn;
	private ButtonComponent quitBtn;
	private int buttonWidth;
	private int buttonHeight;
	
	private float winWidth;
	private float winHeight;
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();

	public EscapeUI(IRenderer _renderer){
		super();
		this.renderer = _renderer;
		buttonHeight = 20;
		buttonWidth = 100;
		
		start(); //java convention to keep constructor under 10 lines
	}
	
	public void start(){
		fileBtn = new ButtonComponent(null,
				Align.BL, buttonWidth, buttonHeight,
				renderer.getImageBank().getTexture("file.png"),
				renderer.getImageBank().getTexture("file2.png"),
				renderer.getImageBank().getTexture("file2.png")
		);
		
		helpBtn = new ButtonComponent(null,
				Align.BL, buttonWidth, buttonHeight,
				renderer.getImageBank().getTexture("help.png"),
				renderer.getImageBank().getTexture("help2.png"),
				renderer.getImageBank().getTexture("help2.png")
		);
		
		audioBtn = new ButtonComponent(null,
				Align.BL, buttonWidth, buttonHeight,
				renderer.getImageBank().getTexture("audio.png"),
				renderer.getImageBank().getTexture("audio2.png"),
				renderer.getImageBank().getTexture("audio2.png")
		);
		
		quitBtn = new ButtonComponent(null,
				Align.BL, buttonWidth, buttonHeight,
				renderer.getImageBank().getTexture("quit.png"),
				renderer.getImageBank().getTexture("quit2.png"),
				renderer.getImageBank().getTexture("quit2.png")
		);
		
		continueBtn = new ButtonComponent(null,
				Align.BL, buttonWidth, buttonHeight,
				renderer.getImageBank().getTexture("continue.png"),
				renderer.getImageBank().getTexture("continue2.png"),
				renderer.getImageBank().getTexture("continue2.png")
		);
		
		this.inputHandlers.add(fileBtn);
		this.inputHandlers.add(helpBtn);
		this.inputHandlers.add(audioBtn);
		this.inputHandlers.add(quitBtn);
		this.inputHandlers.add(continueBtn);
		
	}
	
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}

	@Override
	public void update(double dt) {
		winWidth = renderer.getWidth();
		winHeight = renderer.getHeight();
		fileBtn.update(dt);
		helpBtn.update(dt);
		audioBtn.update(dt);
		quitBtn.update(dt);
		continueBtn.update(dt);
	}

	@Override
	public void render(IRenderer r) {
		fileBtn.setX(0);
		fileBtn.setY((int) winHeight);
		helpBtn.setX(0);
		helpBtn.setY((int) winHeight - buttonHeight);
		audioBtn.setX(0);
		audioBtn.setY((int) winHeight - (2*buttonHeight));;
		quitBtn.setX(0);
		quitBtn.setY((int) winHeight - (3*buttonHeight));
		continueBtn.setX(0);
		continueBtn.setY((int) winHeight - (4*buttonHeight));
		
		fileBtn.render(r);
		helpBtn.render(r);
		audioBtn.render(r);
		quitBtn.render(r);
		continueBtn.render(r);
		
	}

	@Override
	public UI next() {
		// TODO Auto-generated method stub
		return null;
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
