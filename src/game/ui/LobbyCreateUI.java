package game.ui;

import game.*;
import game.exception.ProtocolException;
import game.net.client.IClientConnectionHandler;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.ui.component.TextEntryComponent;
import game.ui.component.UIComponent;

import java.util.ArrayList;

public class LobbyCreateUI extends UI implements InputPipeMulti {
	
	private static final float PADDING = 50.0f;
	
	private UI nextUI = this;
	
	private final TextEntryComponent entry;
	
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
			
		components = new ArrayList<>();
		components.add(entry);
		
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
		try {
			connection.sendLobbyCreateRequest(new LobbyInfo(entry.getString(), 2, 4, 0.0, new PlayerInfo[0]));
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
		entry.setX(PADDING);
		entry.setY(r.getHeight() - PADDING - 50.0f);
		entry.setWidth(r.getWidth() - PADDING * 2);
		
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
		
	}
	
	@Override
	public String toString() {
		return "LobbyCreateUI";
	}
}
