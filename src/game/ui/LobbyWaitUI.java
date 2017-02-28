package game.ui;

import game.LobbyInfo;
import game.Util;
import game.audio.AudioManager;
import game.exception.GameException;
import game.exception.LobbyJoinException;
import game.exception.ProtocolException;
import game.net.client.IClientConnection;
import game.net.client.IClientConnectionHandler;
import game.render.*;

public class LobbyWaitUI extends UI {
	
	private final String lobbyName;
	
	/** If this is not null, an error has occured */
	private String error = null;
	/** If the client has been accepted to the lobby */
	private boolean accepted = false;
	/** The current info of the lobby */
	private LobbyInfo lobbyInfo = null;
	
	private Font font = new Font(Util.getResourcesDir() + "/fonts/terminal2.ttf");
	
	private double time = 0.0f;
	private Texture loadingTex;
	
	private UI nextUI;
	
	public LobbyWaitUI(IClientConnection connection, AudioManager audio, TextureBank tb, String lobbyName) {
		super(connection, audio);
		
		connection.setHandler(new IClientConnectionHandler() {
			@Override
			public void processLobbyUpdate(LobbyInfo info) {
				lobbyInfo = info;
			}
			
			@Override
			public void handleLobbyJoinAccept() {
				accepted = true;
			}
			
			@Override
			public void handleLobbyJoinReject(String reason) {
				accepted = false;
				error = reason;
			}
		});
		
		this.nextUI = this;
		
		this.loadingTex = tb.getTexture("loading.png");
		
		this.lobbyName = lobbyName;
		
		try {
			connection.sendLobbyJoinRequest(this.lobbyName);
		} catch (ProtocolException e) {
			this.error = e.toString();
		}
	}
	
	@Override
	public void update(double dt) {
		this.time += dt;
	}
	
	@Override
	public void render(IRenderer r) {
		if (accepted) {
			// Draw lobby view ui screen
			String s = "Connected to lobby " + lobbyName + ".";
			r.drawText(font, s, Align.MM, true, r.getWidth()/2, r.getHeight()/2, 1.0f);
		} else if (error != null) {
			// Error has occured
			String s = "Could not connect to lobby " + lobbyName + ": " + error;
			r.drawText(font, s, Align.MM, true, r.getWidth()/2, r.getHeight()/2, 1.0f);
		} else {
			// Loading
			float angle = (float)(time * 5.0 % (Math.PI * 2));
			r.drawTexture(loadingTex, Align.MM, r.getWidth()/2.0f, r.getHeight()/2.0f, angle);
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
		return "LobbyWaitUI";
	}
}
