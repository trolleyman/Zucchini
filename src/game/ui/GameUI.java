package game.ui;

import game.InputHandler;
import game.InputPipe;
import game.render.IRenderer;
import game.world.ClientWorld;
import game.world.World;

public class GameUI extends UI implements InputPipe {
	
	public ClientWorld world;
	
	GameUI(IRenderer renderer, ClientWorld _world) {
		super(renderer);
		this.world = _world;
	}
	
	@Override
	public InputHandler getHandler() {
		return this.world;
	}
	
	@Override
	public void update(double dt) {
		this.world.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		this.world.render(r);
	}
	
	@Override
	public UI next() {
		return this;
	}
	
	@Override
	public String toString() {
		return "GameUI";
	}
}
