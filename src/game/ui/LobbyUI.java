package game.ui;

import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
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
	
	private Font f;
	
	/** The start button */
	private ButtonComponent joinButton;
	/** The exit button */
	private ButtonComponent backButton;
	/** The background image */
	private ImageComponent backgroundImage;
	/** The next UI to return */
	private UI nextUI = this;
	private TextureBank tb;
	
	
	private TextButtonComponent lobby0;
	private TextButtonComponent lobby1;
	private TextButtonComponent lobby2;
	private TextButtonComponent lobby3;
	private ArrayList<TextButtonComponent> lobbies = new ArrayList<>();
	
	public LobbyUI(IClientConnection conn, AudioManager audio, TextureBank tb) {
		super(conn, audio);
		
		f = new Font(Util.getResourcesDir() + "/fonts/terminal2.ttf");
		this.tb = tb;
		
		joinButton = new ButtonComponent(
			() -> { this.nextUI = new LobbyWaitUI(connection, audio, tb, "TestLobby1"); },
			Align.BL, 100, 100,
			tb.getTexture("joinDefault.png"),
			tb.getTexture("joinHover.png"),
			tb.getTexture("joinPressed.png")
		);
		
		backButton = new ButtonComponent(
			() -> { this.nextUI = new StartUI(connection, audio, tb); },
			Align.BL, 100, 100,
			tb.getTexture("backDefault.png"),
			tb.getTexture("backHover.png"),
			tb.getTexture("backPressed.png")
		);
		
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, tb.getTexture("Start_BG.png"), 0.0f
		);
		
		lobby0 = new TextButtonComponent(
				() -> {lobbySelect(0);}, 300, 300, f, "Lobby1 - 0/16 Players", 1);
		lobby1 = new TextButtonComponent(
				() -> {lobbySelect(1);}, 300, 300, f, "Lobby2 - 0/16 Players", 1);
		lobby2 = new TextButtonComponent(
				() -> {lobbySelect(2);}, 300, 300, f, "Lobby3 - 0/16 Players", 1);
		lobby3 = new TextButtonComponent(
				() -> {lobbySelect(3);}, 300, 300, f, "Lobby4 - 0/16 Players", 1);
		
		this.inputHandlers.add(joinButton);
		this.inputHandlers.add(backButton);
		this.inputHandlers.add(lobby0);
		this.inputHandlers.add(lobby1);
		this.inputHandlers.add(lobby2);
		this.inputHandlers.add(lobby3);
		
		lobbies.add(lobby0);
		lobbies.add(lobby1);
		lobbies.add(lobby2);
		lobbies.add(lobby3);
	}

	private void lobbySelect(int lobby) {
		for (int i=0; i < lobbies.size(); i++) {
			lobbies.get(i).setSelected(false);
		}
		lobbies.get(lobby).setSelected(true);
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
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
		 	System.out.println("escape pressed");
			this.nextUI = new StartUI(connection, audio, tb);
		}
	}
	
	@Override
	public void update(double dt) {
		joinButton.update(dt);
		backButton.update(dt);
		lobby0.update(dt);
		lobby1.update(dt);
		lobby2.update(dt);
		lobby3.update(dt);
	}

	@Override
	public void render(IRenderer r) {
		joinButton.setX((int) (windowW/2.0 - joinButton.getWidth()/2.0));
		joinButton.setY((int) (windowH/2.0 - joinButton.getHeight()/2.0) + 140);
		backButton.setX((int) (windowW/2.0 - backButton.getWidth()/2.0));
		backButton.setY((int) (windowH/2.0 - backButton.getHeight()/2.0));
		lobby0.setX((int) (windowW/2.0 - lobby0.getWidth()/2.0));
		lobby0.setY((int) (windowH/2.0 - lobby0.getHeight()/2.0) - 140);
		
		lobby1.setX((int) (windowW/2.0 - lobby1.getWidth()/2.0));
		lobby1.setY((int) (windowH/2.0 - lobby1.getHeight()/2.0) - 220);
		
		lobby2.setX((int) (windowW/2.0 - lobby2.getWidth()/2.0));
		lobby2.setY((int) (windowH/2.0 - lobby2.getHeight()/2.0) - 300);
		
		lobby3.setX((int) (windowW/2.0 - lobby3.getWidth()/2.0));
		lobby3.setY((int) (windowH/2.0 - lobby3.getHeight()/2.0) - 380);
		
		backgroundImage.render(r);
		joinButton.render(r);
		backButton.render(r);
		lobby0.render(r);
		lobby1.render(r);
		lobby2.render(r);
		lobby3.render(r);
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
