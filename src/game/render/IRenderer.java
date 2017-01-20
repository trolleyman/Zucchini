package game.render;

import game.InputHandler;
import game.KeyboardManager;

public interface IRenderer {
	public void setInputHandler(InputHandler _ui);
	public KeyboardManager getKeyboardManager();
	
	public void show();
	public void destroy();
	
	public boolean shouldClose();
	
	public void beginFrame();
	public void endFrame();
	
	public void drawTexture(String name, int x, int y);
}
