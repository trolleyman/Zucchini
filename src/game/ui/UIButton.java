package game.ui;

import static org.lwjgl.glfw.GLFW.*;

import game.KeyboardManager;
import game.render.IRenderer;
import game.render.Texture;

public class UIButton extends UIComponent {
	
	private Runnable callback;
	
	private Texture defaultImage;
	private Texture hoverImage;
	private Texture pressedImage;
	
	private Texture currentImage;
	
	private float mx;
	private float my;
	
	private float x;
	private float y;
	
	private boolean pressed = false;
	private boolean released = false;
		
	/**
	 * Constructs a button
	 * @param _callback The callback that is run when the button is clicked
	 * @param _x The x-coordinate of the button
	 * @param _y The y-coordinate of the button
	 * @param _defaultImage The default image of the button
	 * @param _hoverImage The image drawn when the mouse hovers over the button
	 * @param _pressedImage The image drawn when the button is pressed down
	 */
	public UIButton(Runnable _callback, float _x, float _y, Texture _defaultImage, Texture _hoverImage, Texture _pressedImage) {
		this.callback = _callback;
		
		this.defaultImage = _defaultImage;
		this.hoverImage = _hoverImage;
		this.pressedImage = _pressedImage;
		
		this.currentImage = defaultImage;
		
		this.x = _x;
		this.y = _y;
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		this.mx = (float) xpos;
		this.my = (float) ypos;
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1) {
			this.pressed = true;
		}
		
		if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_1) {
			this.released = true;
		}
	}
	
	private boolean isMouseOnButton() {
		return mx >= x && mx < x + currentImage.getWidth()
			&& my >= y && my < y + currentImage.getHeight();
	}
	
	@Override
	public void update(double dt) {
		if (this.released) {
			this.pressed = false;
			this.released = false;
			if (isMouseOnButton())
				this.callback.run();
		}
	}

	@Override
	public void render(IRenderer r) {
		if (!isMouseOnButton()) {
			currentImage = defaultImage;
		} else if (!this.pressed) {
			currentImage = hoverImage;
		} else {
			currentImage = pressedImage;
		}
		
		r.drawImage(currentImage, x, y);
	}
}
