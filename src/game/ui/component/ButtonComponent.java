package game.ui.component;

import static org.lwjgl.glfw.GLFW.*;

import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;

/**
 * The UIButton is a UIComponent that acts as a button.
 * It renders differently depending on what the mouse is doing.
 * 
 * @author Callum
 */
public class ButtonComponent extends AbstractButtonComponent {
	/** The function that is called when the button is clicked */
	private Runnable callback;
	
	/** The texture that is rendered by default */
	private Texture defaultTexture;
	/** The texture that is rendered when the button is hovered over */
	private Texture hoverTexture;
	/** The texture that is rendered when the button is pressed */
	private Texture pressedTexture;
	
	/** The current texture */
	private Texture currentTexture;
	
	/**
	 * Constructs a button
	 * @param _callback The callback that is run when the button is clicked
	 * @param _a The alignment point of the button
	 * @param _x The x-coordinate of the button
	 * @param _y The y-coordinate of the button
	 * @param _defaultTexture The default image of the button
	 * @param _hoverTexture The image drawn when the mouse hovers over the button
	 * @param _pressedTexture The image drawn when the button is pressed down
	 */
	public ButtonComponent(Runnable _callback, Align _a, float _x, float _y, Texture _defaultTexture, Texture _hoverTexture, Texture _pressedTexture) {
		super(_a, _x, _y);
		this.callback = _callback;
		
		this.defaultTexture = _defaultTexture;
		this.hoverTexture = _hoverTexture;
		this.pressedTexture = _pressedTexture;
		
		this.currentTexture = this.defaultTexture;
	}
	
	@Override
	protected void onDefault() {
		this.currentTexture = defaultTexture;
	}
	
	@Override
	protected void onHover() {
		this.currentTexture = hoverTexture;
	}
	
	@Override
	protected void onPressed() {
		this.currentTexture = pressedTexture;
	}
	
	@Override
	public void onClicked() {
		this.callback.run();
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawTexture(currentTexture, a, x, y);
	}
	
	public void render(IRenderer r, float w, float h) {
		r.drawTexture(currentTexture, a, x, y, w, h);
	}
	
	@Override
	public float getWidth() {
		return currentTexture.getWidth();
	}
	
	@Override
	public float getHeight() {
		return currentTexture.getHeight();
	}
}
