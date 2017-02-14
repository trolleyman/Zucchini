package game.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import java.util.ArrayList;
import game.InputHandler;
import game.InputPipeMulti;
import game.audio.AudioManager;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;

public class MiniMap extends UI implements InputPipeMulti{

	private UI nextUI;
	private ClientWorld world;
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private TextureBank bank;

	
	public MiniMap(AudioManager _audio, TextureBank _bank, ClientWorld _world) {
		super(_audio);
		this.world = _world;
		this.bank = _bank;

		nextUI = this;
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
			System.out.println("escape pressed");
			this.nextUI = new GameUI(audio, bank, world);
		}
	}

	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}

	@Override
	public void update(double dt) {
		this.world.update(dt);
		
	}

	@Override
	public void render(IRenderer r) {
		this.world.render2(r);
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
