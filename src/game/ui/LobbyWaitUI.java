package game.ui;

import game.*;
import game.exception.ProtocolException;
import game.net.Message;
import game.net.WorldStart;
import game.net.client.IClientConnectionHandler;
import game.render.*;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.world.ClientWorld;
import game.world.EntityBank;
import org.joml.Vector4f;

import java.util.*;

public class LobbyWaitUI extends UI implements InputPipeMulti {
	private static final float PADDING = 50.0f;
	private static final float INTERNAL_PADDING = 30.0f;
	
	private final String lobbyName;
	
	private final Object messageLogLock = new Object();
	private final ArrayList<Message> messageLog = new ArrayList<>();
	
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
	private ButtonComponent leaveButton;
	
	private ButtonComponent backButton;
	
	private final ImageComponent backgroundImage;
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> errorInputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> emptyInputHandlers = new ArrayList<>();

	/**
	 * Constructs a LobbyWaitUI
	 * @param _ui The UI superclass
	 * @param _lobbyName The lobby name
	 * @param sendJoinRequest Send the join request (boolean)
	 */
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

		// Create Toggle Ready Button
		this.toggleReadyButton = new ButtonComponent(
				this::toggleReady, Align.BL, PADDING, PADDING, defaultTex, hoverTex, pressedTex);

		// Create Leave Button
		this.leaveButton = new ButtonComponent(
				() -> { try {
					connection.sendLobbyLeaveRequest();
				} catch (ProtocolException e) {
					connection.close();
				} },
				Align.BR, 100, 100,
				textureBank.getTexture("leaveDefault.png"),
				textureBank.getTexture("leaveHover.png"),
				textureBank.getTexture("leavePressed.png")
		);

		// Create Back Button
		backButton = new ButtonComponent(
				() -> this.nextUI = new LobbyUI(this),
				Align.BL, 100, 100,
				textureBank.getTexture("backDefault.png"),
				textureBank.getTexture("backHover.png"),
				textureBank.getTexture("backPressed.png")
		);
		
		// Create Background Image
		backgroundImage = new ImageComponent(
				Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);

		// Add input handlers
		this.inputHandlers.add(this.toggleReadyButton);
		this.inputHandlers.add(this.leaveButton);
		this.errorInputHandlers.add(this.backButton);
		
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
				ClientWorld world = new ClientWorld(start.map, new EntityBank(), start.playerId, audio, connection, lobbyName, messageLog);
				nextUI = new GameUI(that, world);
			}
			
			@Override
			public void handleLobbyLeaveNotify() {
				// Leave the current lobby - go back to the lobby list screen
				nextUI = new LobbyUI(that);
			}
			
			@Override
			public void handleMessage(String name, String msg) {
				synchronized (messageLogLock) {
					messageLog.add(new Message(name, msg));
				}
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
		else if (error != null)
			return errorInputHandlers;
		else
			return emptyInputHandlers;
	}

	// Toggle whether the player is ready
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
		int prevSecs = -1;
		if (lobbyInfo != null)
			prevSecs = (int) Math.floor(lobbyInfo.countdownTime);
		synchronized (lobbyInfoLock) {
			if (newLobbyInfo != null) {
				// Replace lobbyInfo with the new lobbyInfo
				lobbyInfo = newLobbyInfo;
				newLobbyInfo = null;
			} else {
				// Decrement current lobbyInfo countdown time
				if (lobbyInfo != null && lobbyInfo.countdownTime != -1) {
					lobbyInfo.countdownTime -= dt;
					if (lobbyInfo.countdownTime < 0.0)
						lobbyInfo.countdownTime = 0.0;
				}
			}
		}
		int newSecs = -1;
		if (lobbyInfo != null)
			newSecs = (int) Math.floor(lobbyInfo.countdownTime);
		
		// If passed an integer amount of seconds
		if (newSecs < prevSecs) {
			// TODO: Maybe sound a beep for every second
			System.out.println("[Lobby]: Game starting in " + prevSecs + "...");
		}
		
		this.toggleReadyButton.update(dt);
		this.leaveButton.update(dt);
		this.backButton.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		backgroundImage.render(r);
		if (accepted && lobbyInfo != null) {
			// Draw lobby view ui screen
			r.drawText(font, lobbyName,
					Align.TL, false, PADDING, r.getHeight() - PADDING, 1.0f);
			
			r.drawText(font, lobbyInfo.players.length + "/" + lobbyInfo.maxPlayers,
					Align.TR, false, r.getWidth() - PADDING, r.getHeight() - PADDING, 1.0f);
			
			// Draw countdown time
			if (lobbyInfo != null && lobbyInfo.countdownTime != -1) {
				Vector4f col;
				if (lobbyInfo.countdownTime < 1.0f)
					col = ColorUtil.GREEN;
				else if (lobbyInfo.countdownTime < 3.0f)
					col = ColorUtil.GREEN;
				else
					col = ColorUtil.GREEN;
				
				String s = String.format("%.3f", lobbyInfo.countdownTime);
				r.drawText(font, s, Align.BL, false,
						r.getWidth()/2 - font.getWidth("9.999", 1.0f)/2,
						PADDING/2 + toggleReadyButton.getY() + toggleReadyButton.getHeight(), 1.0f, col);
			}
			
			// Draw players
			float x1 = PADDING + font.getWidth("_:", 1.0f);
			float x2 = x1 + INTERNAL_PADDING;
			float y1 = r.getHeight() - PADDING - font.getHeight(1.0f) - INTERNAL_PADDING;
			
			for (int i = 0; i < lobbyInfo.players.length; i++) {
				PlayerInfo playerInfo = lobbyInfo.players[i];
				
				Texture tex;
				if (playerInfo.ready) tex = readyTex;
				else tex = unreadyTex;
				
				float y2 = y1 - font.getHeight(1.0f) / 2.0f;
				
				r.drawText(font, playerInfo.team + ":",
						Align.TR, false, x1, y1, 1.0f);
				r.drawText(font, playerInfo.name,
						Align.TL, false, x2, y1, 1.0f);
				
				r.drawTexture(tex, Align.MR, r.getWidth() - PADDING, y2);
				
				y1 -= font.getHeight(1.0f);
				y1 -= INTERNAL_PADDING;
			}
			
			toggleReadyButton.setX(r.getWidth()/2 + PADDING/2);
			toggleReadyButton.setY(PADDING);
			toggleReadyButton.render(r);
			leaveButton.setX(r.getWidth()/2 - PADDING/2);
			leaveButton.setY(PADDING);
			leaveButton.render(r);
			
		} else if (error != null) {
			// Error has occured
			float scale = 0.5f;
			String s = "Could not connect to lobby " + lobbyName + ":";
			r.drawText(font, s, Align.MM, true, r.getWidth()/2, r.getHeight()/2, scale, ColorUtil.RED);
			r.drawText(font, error, Align.MM, true, r.getWidth()/2, r.getHeight()/2 - font.getHeight(scale), scale, ColorUtil.RED);
			
			backButton.setX(20.0f);
			backButton.setY(20.0f);
			backButton.render(r);
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
		// Nothing to destroy
	}
}
