package game.ui;

import game.*;
import game.audio.AudioManager;
import game.net.client.IClientConnection;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.render.TextureBank;
import game.world.ClientWorld;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class LobbyUI extends UI implements InputPipeMulti {
	/** The current window width */
	private int windowW;
	/** The current window height */
	private int windowH;
	
	/** The list of objects to redirect input to */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();

	/** The font used for lobby buttons */
	private Font f;
	private float lobby_spacing = 80;
	
	/** The start button */
	private ButtonComponent joinButton;
	/** The exit button */
	private ButtonComponent backButton;
	/** The background image */
	private ImageComponent backgroundImage;
	/** The next UI to return */
	private UI nextUI = this;
	/** The texture bank that stores the images */
	private TextureBank tb;
	
	/** Test lobbies for button generation */
	private ArrayList<LobbyInfo> lobbies = new ArrayList<>();
	// private LobbyInfo lobby0 = new LobbyInfo("lobby1", 16, new PlayerInfo[0]);
	// private LobbyInfo lobby1 = new LobbyInfo("fish", 16, new PlayerInfo[0]);
	// private LobbyInfo lobby2 = new LobbyInfo("zucchini", 12, new PlayerInfo[0]);
	private ArrayList<TextButtonComponent> lobby_buttons = new ArrayList<>();
	
	public LobbyUI(IClientConnection conn, AudioManager audio, TextureBank tb) {
		super(conn, audio);
		

		f = new Font(Util.getResourcesDir() + "/fonts/emulogic.ttf");
		this.tb = tb;

		// Create Join Button
		joinButton = new ButtonComponent(
			() -> { this.nextUI = new LobbyWaitUI(connection, audio, tb, "TestLobby1"); },
			Align.BL, 100, 100,
			tb.getTexture("joinDefault.png"),
			tb.getTexture("joinHover.png"),
			tb.getTexture("joinPressed.png")
		);

		// Create Back Button
		backButton = new ButtonComponent(
			() -> { this.nextUI = new StartUI(connection, audio, tb); },
			Align.BL, 100, 100,
			tb.getTexture("backDefault.png"),
			tb.getTexture("backHover.png"),
			tb.getTexture("backPressed.png")
		);

		// Create Background Image
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, tb.getTexture("Start_BG.png"), 0.0f
		);

		// Add buttons to input handlers


		// Add test lobbies
		// lobbies.add(lobby0);
		// lobbies.add(lobby1);
		// lobbies.add(lobby2);

		connection.getLobbies((lobs) -> {
			refresh(lobs);
		}, (err) -> {

		});

		// Add the buttons for the test lobbies

	}

	/**
	 * Is called when any of the lobbies are selected
	 * TODO: Add joining the selected lobby when JOIN BUTTON is pressed
	 * @param lobby
	 */
	private void lobbySelect(int lobby) {
		for (int i=0; i < lobby_buttons.size(); i++) {
			lobby_buttons.get(i).setSelected(false);
		}
		lobby_buttons.get(lobby).setSelected(true);
	}

	private void refresh(ArrayList<LobbyInfo> lobs) {
		lobbies = lobs;
		lobby_buttons.clear();
		this.inputHandlers.clear();
		for (int i = 0; i < lobbies.size(); i++) {
			final int l = i;
			TextButtonComponent lobbyButton = new TextButtonComponent(
					() -> {lobbySelect(l);}, Align.BL, 300, 300, f, 0.5f, lobbies.get(i));
			lobby_buttons.add(lobbyButton);
			this.inputHandlers.add(lobby_buttons.get(i));
		}
		this.inputHandlers.add(joinButton);
		this.inputHandlers.add(backButton);
	}

	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	
	@Override
	public void handleResize(int w, int h) {
		this.windowW = w;
		this.windowH = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		// Allows escape to be pressed to return to previous menu
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
		 	System.out.println("escape pressed");
			this.nextUI = new StartUI(connection, audio, tb);
		}
	}
	
	@Override
	public void update(double dt) {
		// Run the update method for all of the buttons
		joinButton.update(dt);
		backButton.update(dt);
		for (int i=0; i < lobby_buttons.size(); i++) {
			lobby_buttons.get(i).update(dt);
		}
	}

	@Override
	public void render(IRenderer r) {
		//Set locations of the primary menu buttons
		joinButton.setX((int) (windowW/2.0 - joinButton.getWidth()/2.0));
		joinButton.setY((int) (windowH/2.0 - joinButton.getHeight()/2.0) + 140);
		backButton.setX((int) (windowW/2.0 - backButton.getWidth()/2.0));
		backButton.setY((int) (windowH/2.0 - backButton.getHeight()/2.0));

		//Render these and the background image
		backgroundImage.render(r);
		joinButton.render(r);
		backButton.render(r);

		// Set location of and render each of the lobby buttons
		for (int i=0; i < lobby_buttons.size(); i++) {
			lobby_buttons.get(i).setX((int) (windowW/2.0 - lobby_buttons.get(i).getWidth()/2.0));
			lobby_buttons.get(i).setY((int) (windowH/2.0 - lobby_buttons.get(i).getHeight()/2.0) - 60 - (i+1)*lobby_spacing);
			lobby_buttons.get(i).render(r);
		}
	}

	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public String toString() {
		return "LobbyUI";
	}

	@Override
	public void destroy() {
		// Nothing to destroy
	}

}
