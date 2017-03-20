/**
 * 
 */
package game.ui;

import game.InputHandler;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.FontBank;
import game.render.IRenderer;
import game.render.TextureBank;

/**
 * The UI is the root class of all UIs
 * @author jackm
 */
public abstract class UI implements InputHandler {
	/** This is the connection to the server */
	protected IClientConnection connection;
	protected AudioManager audio;
	protected TextureBank textureBank;
	protected FontBank fontBank;

	/**
	 * Constructs a UI using an existing UI
	 * @param ui The ui
	 */
	public UI(UI ui) {
		this.connection = ui.connection;
		this.audio = ui.audio;
		this.textureBank = ui.textureBank;
		this.fontBank = ui.fontBank;
	}

	/**
	 * Constructs a UI using all of the components
	 * @param _connection The client connection to the server
	 * @param _audio The audio manager
	 * @param _textureBank The texture bank
	 * @param _fontBank The font bank
	 */
	public UI(IClientConnection _connection, AudioManager _audio, TextureBank _textureBank, FontBank _fontBank) {
		this.connection = _connection;
		this.audio = _audio;
		this.textureBank = _textureBank;
		this.fontBank = _fontBank;
	}

	/**
	 * Updates the UI
	 * @param dt The number of seconds passed since the last update
	 */
	public abstract void update(double dt);
	
	/**
	 * Renders the UI onto the screen
	 * @param r The Renderer object
	 */
	public abstract void render(IRenderer r);
	
	/**
	 * Returns the next UI state to be in
	 */
	public abstract UI next();
	
	/**
	 * Called when the UI is destroyed so that it will not be used again.
	 */
	public abstract void destroy();
}
