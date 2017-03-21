package game.ui;

import game.*;
import game.exception.ProtocolException;
import game.net.client.IClientConnectionHandler;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.ui.component.*;

import java.util.ArrayList;

public class LobbyCreateUI extends UI implements InputPipeMulti {
	
	private static final float PADDING = 50.0f;
	private static final float INTERNAL_PADDING = 30.0f;
	
	private UI nextUI = this;
	
	private final TextEntryComponent entry;
	private final ButtonComponent backButton;
	private final ButtonComponent createButton;
	
	private final ImageComponent backgroundImage;
	
	private ArrayList<UIComponent> components;
	
	private double time = 0.0;
	private boolean loading = false;
	private String error = null;

	/**
	 * Constructs a LobbyCreateUI
	 * @param _ui The UI superclass
	 */
	public LobbyCreateUI(UI _ui) {
		super(_ui);
		
		Font font = fontBank.getFont("emulogic.ttf");
		entry = new TextEntryComponent(
				font, 1.0f,
				Util::isValidLobbyNameChar,
				this::submit,
				Util.MAX_LOBBY_NAME_LENGTH,
				() -> {},
				Character::toLowerCase,
				PADDING, PADDING + font.getHeight(1.0f) + 10.0f, 150
		);
		
		backButton = new ButtonComponent(
				() -> this.nextUI = new LobbyUI(this),
				Align.TL, 100, 100,
				textureBank.getTexture("backDefault.png"),
				textureBank.getTexture("backHover.png"),
				textureBank.getTexture("backPressed.png")
		);
		
		createButton = new ButtonComponent(
				this::submit, Align.TL, 0.0f, 0.0f,
				textureBank.getTexture("createDefault.png"),
				textureBank.getTexture("createHover.png"),
				textureBank.getTexture("createPressed.png")
		);
		
		components = new ArrayList<>();
		components.add(entry);
		components.add(createButton);
		components.add(backButton);
		
		// Create Background Image
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);
		
		UI that = this;
		connection.setHandler(new IClientConnectionHandler() {
			@Override
			public void handleLobbyCreateAccept() {
				System.out.println("Lobby created.");
				nextUI = new LobbyWaitUI(that, entry.getString(), false);
				loading = false;
				error = null;
				entry.setEnabled(true);
			}
			
			@Override
			public void handleLobbyCreateReject(String reason) {
				loading = false;
				error = "Error: " + reason;
				entry.setEnabled(true);
			}
		});
	}

	/**
	 * Submit the current lobby if the name is valid
	 */
	private void submit() {
		String s = entry.getString();
		if (!Util.isValidLobbyName(s)) {
			error = "Error: Lobby name is invalid";
			return;
		}
		try {
			loading = true;
			error = null;
			entry.setEnabled(false);
			connection.sendLobbyCreateRequest(new LobbyInfo(s, Util.DEFAULT_MIN_PLAYERS, Util.DEFAULT_MAX_PLAYERS, -1.0, new PlayerInfo[0]));
		} catch (ProtocolException e) {
			loading = false;
			error = "Error: " + e;
			entry.setEnabled(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<InputHandler> getHandlers() {
		// This cast is fine since all UIComponent's are InputHandlers
		return (ArrayList) components;
	}
	
	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
		
		Font f = fontBank.getFont("emulogic.ttf");
		r.drawText(f, "Create Lobby:", Align.TL, false, PADDING, r.getHeight() - PADDING, 1.0f);
		
		entry.setX(PADDING);
		entry.setY(r.getHeight() - PADDING - INTERNAL_PADDING - 10.0f - f.getHeight(1.0f) - f.getHeight(1.0f));
		entry.setWidth(r.getWidth() - PADDING * 2);
		
		backButton.setX(PADDING);
		backButton.setY(entry.getY() - INTERNAL_PADDING);
		
		createButton.setX(backButton.getX() + backButton.getWidth() + INTERNAL_PADDING);
		createButton.setY(backButton.getY());
		
		for (UIComponent c : components) {
			c.render(r);
		}
		
		// Render loading/error
		if (loading) {
			float angle = (float)(time * 5.0 % (Math.PI * 2));
			r.drawTexture(r.getTextureBank().getTexture("loading.png"), Align.MM,
					r.getWidth()/2,
					backButton.getY() - backButton.getHeight() - 60.0f, angle);
		} else if (error != null) {
			Font font = r.getFontBank().getFont("emulogic.ttf");
			r.drawText(font, error, Align.TL, false,
					PADDING,
					backButton.getY() - backButton.getHeight() - INTERNAL_PADDING, 0.5f, ColorUtil.RED);
		}
	}
	
	@Override
	public void update(double dt) {
		time += dt;
		for (UIComponent c : components) {
			c.update(dt);
		}
	}
	
	@Override
	public UI next() {
		return nextUI;
	}
	
	@Override
	public void destroy() {
		// Nothing to destroy
	}
}
