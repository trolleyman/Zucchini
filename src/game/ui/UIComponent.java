package game.ui;

import game.InputHandler;
import game.render.IRenderer;

public abstract class UIComponent implements InputHandler {
	public abstract void update(double dt);
	public abstract void render(IRenderer r);
}
