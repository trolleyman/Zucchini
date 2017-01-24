package game.render;

import java.awt.Color;

import game.InputHandler;
import game.KeyboardManager;

public interface IRenderer {
	public void setInputHandler(InputHandler ui);
	public void setVSync(boolean enable);
	
	public KeyboardManager getKeyboardManager();
	
	public int getWidth();
	public int getHeight();
	
	public void show();
	public void destroy();
	
	public boolean shouldClose();
	
	public void beginFrame();
	public void endFrame();
	
	public void drawImage(Image img, float x, float y);
	public ImageBank getImageBank();
	
	public void drawBox(float x, float y, float w, float h, Color c);
}
