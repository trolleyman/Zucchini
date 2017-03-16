package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.Align;
import game.render.FontBank;
import game.render.IRenderer;
import game.render.TextureBank;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.world.ClientWorld;

public class HelpUI extends UI implements InputPipeMulti{
	private UI nextUI;
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private TextureBank bank;
	private ButtonComponent backBtn;
	private float winWidth;
	private float winHeight;
	private ImageComponent backgroundImage;
	
	public HelpUI(UI ui){
		super(ui);
		start();
	}
	
	public HelpUI(IClientConnection _conn, AudioManager audio, TextureBank _bank, FontBank _fb) {
		super(_conn, audio, _bank, _fb);
	//	this.bank = _bank;
		nextUI = this;
		start();
	}
	
	private void start(){
		backBtn = new ButtonComponent(null, Align.TL, 0, 0,
				textureBank.getTexture("temparrow.png"),
				textureBank.getTexture("temparrow.png"),
				textureBank.getTexture("temparrow.png")
		);
		
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);
		
		this.inputHandlers.add(backBtn);
	}
	
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}

	@Override
	public void handleResize(int w, int h) {
		this.winWidth = w;
		this.winHeight = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
		backBtn.update(dt);
	}

	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
		
		backBtn.setX(0);
		backBtn.setY((int) winHeight - 200);
		backBtn.render(r);
	}

	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		//Nothing to destroy
	}

	@Override
	public String toString() {
		return "HelpUI";
	}
}
