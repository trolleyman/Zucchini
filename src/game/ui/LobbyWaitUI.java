package game.ui;

import game.*;
import game.audio.AudioManager;
import game.exception.GameException;
import game.exception.LobbyJoinException;
import game.exception.ProtocolException;
import game.net.WorldStart;
import game.net.client.IClientConnection;
import game.net.client.IClientConnectionHandler;
import game.render.*;
import game.world.ClientWorld;
import game.world.EntityBank;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class LobbyWaitUI extends UI implements InputPipeMulti {
	private static final float PADDING = 30.0f;
	
	private final String lobbyName;
	
	/** If this is not null, an error has occured */
	private String error = null;
	/** If the client has been accepted to the lobby */
	private boolean accepted = false;
	/** The current info of the lobby */
	private LobbyInfo lobbyInfo = null;
	
	private LobbyInfo newLobbyInfo = null;
	
	private boolean sendLobbyLeave = false;
	
	private double time = 0.0f;
	
	private Texture loadingTex;
	private Texture readyTex;
	private Texture unreadyTex;
	
	private UI nextUI;
	
	private Font font;
	
	private ButtonComponent toggleReadyButton;
	
	private ArrayList<InputHandler> emptyInputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	public LobbyWaitUI(UI _ui, String lobbyName) {
		super(_ui);
		
		this.nextUI = this;
		
		font = fontBank.getFont("emulogic.ttf");
		
		this.loadingTex = textureBank.getTexture("loading.png");
		this.readyTex   = textureBank.getTexture("ready.png");
		this.unreadyTex = textureBank.getTexture("unready.png");
		
		this.lobbyName = lobbyName;
		
		Texture defaultTex = textureBank.getTexture("toggleReadyDefault.png");
		Texture hoverTex = textureBank.getTexture("toggleReadyHover.png");
		Texture pressedTex = textureBank.getTexture("toggleReadyPressed.png");
		
		this.toggleReadyButton = new ButtonComponent(
				this::toggleReady, Align.BL, PADDING, PADDING, defaultTex, hoverTex, pressedTex);
		
		this.inputHandlers.add(this.toggleReadyButton);
		this.inputHandlers.add(new InputHandler() {
			@Override
			public void handleKey(int key, int scancode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_Q) {
					// Send lobby leave request - this will be button later
					sendLobbyLeave = true;
				}
			}
		});
		
		LobbyWaitUI that = this;
		connection.setHandler(new IClientConnectionHandler() {
			@Override
			public void processLobbyUpdate(LobbyInfo info) {
				Arrays.sort(info.players, Comparator.comparingInt((i) -> i.team));
				newLobbyInfo = info;
			}
			
			@Override
			public void handleLobbyJoinAccept() {
				System.out.println("[Lobby]: Accepted client");
				accepted = true;
			}
			
			@Override
			public void handleLobbyJoinReject(String reason) {
				System.out.println("[Lobby]: Rejected client: " + reason);
				accepted = false;
				error = reason;
			}
			
			@Override
			public void handleWorldStart(WorldStart start) {
				ClientWorld world = new ClientWorld(start.map, new EntityBank(), start.playerId, audio, connection);
				nextUI = new GameUI(that, world);
			}
			
			@Override
			public void handleLobbyLeaveNotify() {
				// Leave the current lobby - go back to the lobby list screen
				nextUI = new LobbyUI(that);
			}
		});
		
		try {
			connection.sendLobbyJoinRequest(this.lobbyName);
		} catch (ProtocolException e) {
			this.error = e.toString();
		}
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		if (accepted && lobbyInfo != null)
			return inputHandlers;
		else
			return emptyInputHandlers;
	}
	
	private void toggleReady() {
		try {
			connection.sendToggleReady();
		} catch (ProtocolException e) {
			System.err.println("Error: Cannot toggle ready status: " + e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(double dt) {
		this.time += dt;
		lobbyInfo = newLobbyInfo;
		this.toggleReadyButton.update(dt);
		
		if (this.sendLobbyLeave) {
			try {
				connection.sendLobbyLeaveRequest();
			} catch (ProtocolException e) {
				connection.close();
			}
		}
	}
	
	@Override
	public void render(IRenderer r) {
		if (accepted && lobbyInfo != null) {
			// Draw lobby view ui screen
			r.drawText(font, lobbyName + "      " + lobbyInfo.players.length + "/" + lobbyInfo.maxPlayers,
					Align.TL, false, PADDING, r.getHeight() - PADDING, 1.0f);
			
			float y = r.getHeight() - PADDING - font.getHeight(1.0f) - 5.0f;
			for (int i = 0; i < lobbyInfo.players.length; i++) {
				PlayerInfo playerInfo = lobbyInfo.players[i];
				
				r.drawText(font, playerInfo.team + ":",
						Align.TR, false, PADDING + 30.0f, y, 1.0f);
				r.drawText(font, playerInfo.name,
						Align.TL, false, PADDING + 30.0f, y, 1.0f);
				if (playerInfo.ready)
					r.drawTexture(readyTex, Align.TR, r.getWidth() - PADDING, y);
				else
					r.drawTexture(unreadyTex, Align.TR, r.getWidth() - PADDING, y);
				
				y -= font.getHeight(1.0f) + 5.0f;
			}
			
			toggleReadyButton.setX(r.getWidth()/2 - toggleReadyButton.getWidth()/2);
			toggleReadyButton.setY(PADDING);
			toggleReadyButton.render(r);
			
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
