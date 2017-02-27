package game.ui;

import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.audio.AudioManager;
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
	private TextureBank tb;
	
	/** Test buttons for the lobbies */
	private ArrayList<TextButtonComponent> lobbies = new ArrayList<>();
	
	public LobbyUI(AudioManager audio, TextureBank tb) {
		super(audio);
		
		f = new Font(Util.getBasePath() + "resources/fonts/emulogic.ttf");
		this.tb = tb;
		
		joinButton = new ButtonComponent(
			() -> { this.nextUI = new GameUI(audio, tb, ClientWorld.createTestWorld(audio)); },
			Align.BL, 100, 100,
			tb.getTexture("joinDefault.png"),
			tb.getTexture("joinHover.png"),
			tb.getTexture("joinPressed.png")
		);
		
		backButton = new ButtonComponent(
			() -> { this.nextUI = new StartUI(audio, tb); },
			Align.BL, 100, 100,
			tb.getTexture("backDefault.png"),
			tb.getTexture("backHover.png"),
			tb.getTexture("backPressed.png")
		);
		
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, tb.getTexture("Start_BG.png"), 0.0f
		);
		
		this.inputHandlers.add(joinButton);
		this.inputHandlers.add(backButton);

		for (int i = 0; i < 4; i++) {
			final int l = i;
			TextButtonComponent lobby = new TextButtonComponent(
					() -> {lobbySelect(l);}, Align.BL, 300, 300, f, "Lobby" + i +" - 0/16 Players", 0.5f);
			lobbies.add(lobby);
			this.inputHandlers.add(lobbies.get(i));
		}
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
			this.nextUI = new StartUI(audio, tb);
		}
	}
	
	@Override
	public void update(double dt) {
		joinButton.update(dt);
		backButton.update(dt);
		for (int i=0; i < lobbies.size(); i++) {
			lobbies.get(i).update(dt);
		}
	}

	@Override
	public void render(IRenderer r) {
		joinButton.setX((int) (windowW/2.0 - joinButton.getWidth()/2.0));
		joinButton.setY((int) (windowH/2.0 - joinButton.getHeight()/2.0) + 140);
		backButton.setX((int) (windowW/2.0 - backButton.getWidth()/2.0));
		backButton.setY((int) (windowH/2.0 - backButton.getHeight()/2.0));

		backgroundImage.render(r);
		joinButton.render(r);
		backButton.render(r);

		for (int i=0; i < lobbies.size(); i++) {
			lobbies.get(i).setX((int) (windowW/2.0 - lobbies.get(i).getWidth()/2.0));
			lobbies.get(i).setY((int) (windowH/2.0 - lobbies.get(i).getHeight()/2.0) - 60 - (i+1)*lobby_spacing);
			lobbies.get(i).render(r);
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
