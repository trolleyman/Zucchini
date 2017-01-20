package game.render;

import game.InputHandler;
import game.KeyboardManager;

public interface IRenderer {
	public void setInputHandler(InputHandler ui);
	public void setResizeCallback(IResizeCallback resizeCallback);
	public KeyboardManager getKeyboardManager();
	
	public int getWidth();
	public int getHeight();
	
	public void show();
	public void destroy();
	
	public boolean shouldClose();
	
	public void beginFrame();
	public void endFrame();
	
	public void drawImage(String name, int x, int y);
	public Image getImage(String name);
}
