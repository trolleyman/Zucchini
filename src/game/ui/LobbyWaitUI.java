package game.ui;

import game.*;
import game.exception.ProtocolException;
import game.net.WorldStart;
import game.net.client.IClientConnectionHandler;
import game.render.*;
import game.ui.component.ButtonComponent;
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
	private boolean accepted;
	
	/** Lobby Info Lock */
	private final Object lobbyInfoLock = new Object();
	/** The current info of the lobby */
	private LobbyInfo lobbyInfo = null;
	
	private LobbyInfo newLobbyInfo = null;
	
	private double time = 0.0f;
	
	private Texture loadingTex;
	private Texture readyTex;
	private Texture unreadyTex;
	
	private UI nextUI;
	
	private Font font;
	
	private ButtonComponent toggleReadyButton;
	
	private ArrayList<InputHandler> emptyInputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	public LobbyWaitUI(UI _ui, String _lobbyName, boolean sendJoinRequest) {
		super(_ui);
		
		this.lobbyName = _lobbyName;
		this.nextUI = this;
		
		// Accepted is true when we don't send a join request
		this.accepted = !sendJoinRequest;
		
		font = fontBank.getFont("emulogic.ttf");
		
		this.loadingTex = textureBank.getTexture("loading.png");
		this.readyTex   = textureBank.getTexture("ready.png");
		this.unreadyTex = textureBank.getTexture("unready.png");
		
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
					try {
						connection.sendLobbyLeaveRequest();
					} catch (ProtocolException e) {
						connection.close();
					}
				}
			}
		});
		
		LobbyWaitUI that = this;
		connection.setHandler(new IClientConnectionHandler() {
			@Override
			public void processLobbyUpdate(LobbyInfo info) {
				Arrays.sort(info.players, Comparator.comparingInt((i) -> i.team));
				synchronized (lobbyInfoLock) {
					newLobbyInfo = info;
				}
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
		
		if (sendJoinRequest) {
			try {
				connection.sendLobbyJoinRequest(this.lobbyName);
			} catch (ProtocolException e) {
				this.error = e.toString();
			}
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
	
	private boolean passed(double thresh, double old, double nw) {
		return old > thresh && nw < thresh;
	}
	
	private boolean p5 = false;
	private boolean p4 = false;
	private boolean p3 = false;
	private boolean p2 = false;
	private boolean p1 = false;
	
	@Override
	public void update(double dt) {
		this.time += dt;
		synchronized (lobbyInfoLock) {
			if (newLobbyInfo != null) {
				if (newLobbyInfo.countdownTime == -1) {
					p5 = false;
					p4 = false;
					p3 = false;
					p2 = false;
					p1 = false;
				} else {
					if (!p5 && newLobbyInfo.countdownTime <= 5.0) {
						System.out.println("Lobby: Game starts in 5...");
						p5 = true;
					}
					if (!p4 && newLobbyInfo.countdownTime <= 4.0) {
						System.out.println("Lobby: Game starts in 4...");
						p4 = true;
					}
					if (!p3 && newLobbyInfo.countdownTime <= 3.0) {
						System.out.println("Lobby: Game starts in 3...");
						p3 = true;
					}
					if (!p2 && newLobbyInfo.countdownTime <= 2.0) {
						System.out.println("Lobby: Game starts in 2...");
						p2 = true;
					}
					if (!p1 && newLobbyInfo.countdownTime <= 1.0) {
						System.out.println("Lobby: Game starts in 1...");
						p1 = true;
					}
				}
			}
			lobbyInfo = newLobbyInfo;
		}
		this.toggleReadyButton.update(dt);
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