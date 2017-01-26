package game.render;

import java.awt.Color;

import org.joml.MatrixStackf;

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
	
	public MatrixStackf getModelViewMatrix();
	
	public default void drawImage(Texture img, float x, float y) {
		this.drawImage(img, x, y, img.getWidth(), img.getHeight());
	}
	public void drawImage(Texture img, float x, float y, float w, float h);
	public TextureBank getImageBank();
	
	public void drawBox(float x, float y, float w, float h, Color c);
}
