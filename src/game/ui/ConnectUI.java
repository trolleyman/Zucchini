package game.ui;

import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.audio.AudioManager;
import game.net.client.ClientConnection;
import game.net.client.IClientConnection;
import game.render.*;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.ui.component.TextEntryComponent;

import java.util.ArrayList;

/**
 * ConnectUI is responsible for the connection of the user to the server.
 */
public class ConnectUI extends UI implements InputPipeMulti {
	/** The list of objects to redirect input to */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	/** Connection address entry */
	private TextEntryComponent ipEntry;
	/** Name entry */
	private TextEntryComponent nameEntry;
	/** The connect button */
	private ButtonComponent connectButton;
	/** The auto connect button */
	private ButtonComponent autoConnectButton;
	
	private Font font;
	
	/** The next UI to return */
	private UI nextUI = this;
	
	private ImageComponent backgroundImage;
	
	public ConnectUI(UI ui) {
		super(ui);
		setup();
	}
	
	/**
	 * Constructs a new ConnectUI
	 */
	public ConnectUI(IClientConnection _conn, AudioManager _audio, TextureBank _tb, FontBank _fb) {
		super(_conn, _audio, _tb, _fb);
		setup();
	}
	
	private void setup() {
		font = fontBank.getFont("emulogic.ttf");
		
		// Create IP entry
		ipEntry = new TextEntryComponent(font,
				1.0f, (c) -> !Character.isWhitespace(c), this::connect, 20,
				() -> { ipEntry.setEnabled(true); nameEntry.setEnabled(false); },
				0.0f, 0.0f, 0.0f);
		nameEntry = new TextEntryComponent(font,
				1.0f, Util::isValidNameChar, this::connect, 20,
				() -> { ipEntry.setEnabled(false); nameEntry.setEnabled(true); },
				0.0f, 0.0f, 0.0f);
		
		// Create connect Button
		connectButton = new ButtonComponent(
				this::connect, // TODO: Textures
				Align.BL, 100, 100,
				textureBank.getTexture("startDefault.png"),
				textureBank.getTexture("startHover.png"),
				textureBank.getTexture("startPressed.png")
		);
		
		// Create auto connect Button
		autoConnectButton = new ButtonComponent(
				this::autoConnect,
				Align.BL, 100, 100,
				textureBank.getTexture("helpDefault.png"),
				textureBank.getTexture("helpHover.png"),
				textureBank.getTexture("helpPressed.png")
		);
		
		// Create Background Image
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);
		
		// Add buttons to input handlers
		this.inputHandlers.add(ipEntry);
		this.inputHandlers.add(nameEntry);
		this.inputHandlers.add(connectButton);
		this.inputHandlers.add(autoConnectButton);
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	
	@Override
	public void update(double dt) {
		ipEntry.update(dt);
		nameEntry.update(dt);
		connectButton.update(dt);
		autoConnectButton.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		// Render the background image
		backgroundImage.render(r);
		
		// Set the location of the buttons
		float padding = 20.0f;
		float fh = font.getHeight(1.0f);
		ipEntry.setX(padding);
		ipEntry.setY(r.getHeight() - fh - padding - ipEntry.h);
		ipEntry.setWidth(r.getWidth() - padding*2);
		nameEntry.setX(padding);
		nameEntry.setY(ipEntry.getY() - fh - padding - nameEntry.h);
		nameEntry.setWidth(r.getWidth() - padding*2);
		connectButton.setX(padding);
		connectButton.setY(nameEntry.getY() - padding*3 - nameEntry.h);
		autoConnectButton.setX(padding + connectButton.getX() + connectButton.getWidth());
		autoConnectButton.setY(connectButton.getY());
		
		// Render the buttons
		r.drawText(font, "Address", Align.TL, false, padding, r.getHeight() - padding, 1.0f);
		ipEntry.render(r);
		r.drawText(font, "Name", Align.TL, false, padding, ipEntry.getY() - padding, 1.0f);
		nameEntry.render(r);
		connectButton.render(r);
		autoConnectButton.render(r);
	}
	
	public void connect() {
		System.out.println("connect");
	}
	
	public void autoConnect() {
		System.out.println("autoconnect");
	}
	
	@Override
	public UI next() {
		return nextUI;
	}
	
	@Override
	public String toString() {
		return "StartUI";
	}
	
	@Override
	public void destroy() {
		// Nothing to destroy
	}
}
