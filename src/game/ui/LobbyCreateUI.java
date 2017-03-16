package game.ui;

import game.*;
import game.exception.ProtocolException;
import game.net.client.IClientConnectionHandler;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.ui.component.ButtonComponent;
import game.ui.component.TextButtonComponent;
import game.ui.component.TextEntryComponent;
import game.ui.component.UIComponent;

import java.util.ArrayList;

public class LobbyCreateUI extends UI implements InputPipeMulti {
	
	private static final float PADDING = 50.0f;
	private static final float INTERNAL_PADDING = 30.0f;
	
	private UI nextUI = this;
	
	private final TextEntryComponent entry;
	private final ButtonComponent submitButton;
	private final ButtonComponent backButton;
	
	private ArrayList<UIComponent> components;
	
	public LobbyCreateUI(UI _ui) {
		super(_ui);
		
		Font font = fontBank.getFont("emulogic.ttf");
		entry = new TextEntryComponent(
				font, 1.0f,
				Util::isValidLobbyNameChar,
				this::submit,
				Util.MAX_LOBBY_NAME_LENGTH,
				PADDING, PADDING + font.getHeight(1.0f) + 10.0f, 150
		);
		
		submitButton = new ButtonComponent(
				this::submit, Align.TL, 0.0f, 0.0f,
				textureBank.getTexture("joinDefault.png"),
				textureBank.getTexture("joinHover.png"),
				textureBank.getTexture("joinPressed.png")
		);
		
		backButton = new ButtonComponent(
				() -> this.nextUI = new LobbyUI(this),
				Align.TL, 100, 100,
				textureBank.getTexture("backDefault.png"),
				textureBank.getTexture("backHover.png"),
				textureBank.getTexture("backPressed.png")
		);
		
		components = new ArrayList<>();
		components.add(entry);
		components.add(submitButton);
		components.add(backButton);
		
		UI that = this;
		connection.setHandler(new IClientConnectionHandler() {
			@Override
			public void handleLobbyCreateAccept() {
				System.out.println("Lobby created.");
				nextUI = new LobbyWaitUI(that, entry.getString(), false);
				entry.setEnabled(true);
			}
			
			@Override
			public void handleLobbyCreateReject(String reason) {
				// TODO: Provide some more feedback to the user
				System.out.println("Error while creating a lobby: " + reason);
				entry.setEnabled(true);
			}
		});
	}
	
	private void submit() {
		String s = entry.getString();
		if (!Util.isValidLobbyName(s)) {
			// TODO: Provide some feedback to the user
			System.out.println("LobbyCreateUI: Lobby name is invalid: " + s);
			return;
		}
		try {
			connection.sendLobbyCreateRequest(new LobbyInfo(s, Util.DEFAULT_MIN_PLAYERS, Util.DEFAULT_MAX_PLAYERS, -1.0, new PlayerInfo[0]));
			entry.setEnabled(false);
		} catch (ProtocolException e) {
			e.printStackTrace();
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
		Font f = fontBank.getFont("emulogic.ttf");
		r.drawText(f, "Create Lobby:", Align.TL, false, PADDING, r.getHeight() - PADDING, 1.0f);
		
		entry.setX(PADDING);
		entry.setY(r.getHeight() - PADDING - INTERNAL_PADDING - 10.0f - f.getHeight(1.0f) - f.getHeight(1.0f));
		entry.setWidth(r.getWidth() - PADDING * 2);
		
		submitButton.setX(PADDING);
		submitButton.setY(entry.getY() - INTERNAL_PADDING);
		
		backButton.setX(submitButton.getX() + submitButton.getWidth() + INTERNAL_PADDING);
		backButton.setY(submitButton.getY());
		
		for (UIComponent c : components) {
			c.render(r);
		}
	}
	
	@Override
	public void update(double dt) {
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
