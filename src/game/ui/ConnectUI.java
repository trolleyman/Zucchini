package game.ui;

import game.ColorUtil;
import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.audio.AudioManager;
import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.client.ClientConnection;
import game.net.client.ClientDiscovery;
import game.net.client.IClientConnection;
import game.render.*;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.ui.component.TextEntryComponent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * ConnectUI is responsible for the connection of the user to the server.
 */
public class ConnectUI extends UI implements InputPipeMulti {
	private final Object connectLock = new Object();
	private boolean connecting = false;
	
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
	
	private double time = 0.0f;
	
	private Texture loadingTex;
	
	private String error;
	
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
				1.0f, Util::isValidNameChar, this::connect, Util.MAX_NAME_LENGTH,
				() -> { ipEntry.setEnabled(false); nameEntry.setEnabled(true); },
				0.0f, 0.0f, 0.0f);
		nameEntry.setEnabled(false);
		
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
		
		// Create loading texture
		this.loadingTex = textureBank.getTexture("loading.png");
		
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
		time += dt;
		
		ipEntry.update(dt);
		nameEntry.update(dt);
		connectButton.update(dt);
		autoConnectButton.update(dt);
		
		if (connecting) {
			ipEntry.setEnabled(false);
			nameEntry.setEnabled(false);
		} else if (!ipEntry.isEnabled() && !nameEntry.isEnabled()) {
			ipEntry.setEnabled(true);
		}
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
		if (connecting) {
			float angle = (float)(time * 5.0 % (Math.PI * 2));
			r.drawTexture(loadingTex, Align.MM,
					autoConnectButton.getX() + padding + loadingTex.getWidth()/2 + autoConnectButton.getWidth(),
					autoConnectButton.getY() + autoConnectButton.getHeight() - loadingTex.getWidth()/2, angle);
		} else if (error != null) {
			r.drawText(font, "Error: " + error, Align.TL, false, padding,
					autoConnectButton.getY() - padding, 0.5f, ColorUtil.RED);
		}
	}
	
	public void connect() {
		System.out.println("Connecting...");
		error = null;
		new Thread(() -> connectToServer(nameEntry.getString(), ipEntry.getString()), "ConnectUI Connection Starter").start();
	}
	
	public void autoConnect() {
		System.out.println("Autoconnecting...");
		error = null;
		new Thread(() -> connectToServer(nameEntry.getString(), null), "ConnectUI Connection Starter").start();
	}
	
	private String getLastMessage(Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage();
	}
	
	private void connectToServer(String name, String sAddress) {
		try {
			boolean temp;
			synchronized (connectLock) {
				temp = connecting;
				connecting = true;
			}
			if (temp)
				return;
			
			// Connect
			if (!Util.isValidName(name)) {
				error = "Name is not valid";
				return;
			}
			
			if (sAddress == null) {
				// Autoconnect
				try {
					connection = new ClientConnection(name, 3);
				} catch (ProtocolException e) {
					error = "Could not connect to server: " + getLastMessage(e);
					return;
				} catch (NameException e) {
					error = "Name is not valid: " + getLastMessage(e);
					return;
				}
			} else {
				try {
					InetAddress addr = InetAddress.getByName(sAddress);
					connection = new ClientConnection(name, addr);
				} catch (ProtocolException e) {
					error = "Could not connect to server: " + getLastMessage(e);
					return;
				} catch (NameException e) {
					error = "Name is not valid: " + getLastMessage(e);
					return;
				} catch (UnknownHostException e) {
					error = "Host could not be resolved";
					return;
				}
			}
			
			// Connection successful
			nextUI = new StartUI(this);
		} finally {
			synchronized (connectLock) {
				connecting = false;
			}
		}
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
