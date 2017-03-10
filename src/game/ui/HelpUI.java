package game.ui;

import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.FontBank;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;

public class HelpUI extends UI implements InputPipeMulti{
	private UI nextUI;
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private TextureBank bank;
	
	public HelpUI(UI ui){
		super(ui);
	}
	
	public HelpUI(IClientConnection _conn, AudioManager audio, TextureBank _bank, FontBank _fb) {
		super(_conn, audio, _bank, _fb);
	//	this.bank = _bank;
		nextUI = this;
	}
	
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(IRenderer r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}
