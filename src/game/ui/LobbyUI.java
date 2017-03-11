package game.ui;

import game.*;
import game.render.Align;
import game.render.IRenderer;
import game.ui.component.ButtonComponent;
import game.ui.component.ImageComponent;
import game.ui.component.TextButtonComponent;

import java.awt.*;
import java.security.PrivilegedActionException;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class LobbyUI extends UI implements InputPipeMulti
{
	/** The current window width */
	private int windowW;
	/** The current window height */
	private int windowH;

	/** The list of objects to redirect input to */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();

	private float lobby_spacing = 80;

	/** The start button */
	private ButtonComponent joinButton;
	/** The exit button */
	private ButtonComponent backButton;
	/** The refresh lobby list button */
	private ButtonComponent refreshButton;
	/** The next lobby page button */
	private ButtonComponent nextButton;
	/** The create lobby button */
	private ButtonComponent createButton;
	/** The background image */
	private ImageComponent backgroundImage;
	/** The next UI to return */
	private UI nextUI = this;
	/** Lobbies to be rendered, starting at this index (4 per page) */
	private int lobbiesToRender = 0;

	/** Test lobbies for button generation */
	private ArrayList<LobbyInfo> lobbies = new ArrayList<>();
	private LobbyInfo currentLobby = null;

	private ArrayList<TextButtonComponent> lobby_buttons = new ArrayList<>();

	public LobbyUI(UI _ui)
	{
		super(_ui);

		// Create Join Button
		joinButton = new ButtonComponent(
			() -> {
				if (currentLobby != null)
					this.nextUI = new LobbyWaitUI(this, currentLobby.getLobbyName(), true);
			},
			Align.BL, 100, 100,
			textureBank.getTexture("joinDefault.png"),
			textureBank.getTexture("joinHover.png"),
			textureBank.getTexture("joinPressed.png")
		);

		// Create Create Lobby Button
		createButton = new ButtonComponent(
				() -> {
					this.nextUI = new LobbyCreateUI(this);
				},
				Align.BL, 100, 100,
				textureBank.getTexture("createDefault.png"),
				textureBank.getTexture("createHover.png"),
				textureBank.getTexture("createPressed.png")
		);

		// Create Back Button
		backButton = new ButtonComponent(
			() -> this.nextUI = new StartUI(this),
			Align.BL, 100, 100,
			textureBank.getTexture("backDefault.png"),
			textureBank.getTexture("backHover.png"),
			textureBank.getTexture("backPressed.png")
		);

		// Create Refresh Button
		refreshButton = new ButtonComponent(
				() -> connection.getLobbies(this::refresh, (err) -> System.err.println("Error retrieving lobbies: " + err)),
				Align.BL, 100, 100,
				textureBank.getTexture("refreshDefault.png"),
				textureBank.getTexture("refreshHover.png"),
				textureBank.getTexture("refreshPressed.png")
		);

		// Create Next Button
		nextButton = new ButtonComponent(
				() -> this.nextPage(),
				Align.BL, 100, 100,
				textureBank.getTexture("nextDefault.png"),
				textureBank.getTexture("nextHover.png"),
				textureBank.getTexture("nextPressed.png")
		);
		
		// Create Background Image
		backgroundImage = new ImageComponent(
			Align.BL, 0, 0, textureBank.getTexture("Start_BG.png"), 0.0f
		);

		connection.getLobbies(this::refresh, (err) -> System.err.println("Error retrieving lobbies: " + err));
	}

	/**
	 * Is called when any of the lobbies are selected
	 * 
	 * @param lobby
	 */
	private void lobbySelect(int lobby)
	{
		for (int i = 0; i < lobby_buttons.size(); i++)
		{
			lobby_buttons.get(i).setSelected(false);
		}
		lobby_buttons.get(lobby).setSelected(true);
		currentLobby = lobbies.get(lobby);
	}

	private void refresh(ArrayList<LobbyInfo> lobs) {
		synchronized (this) {
			lobbies = lobs;
			lobby_buttons.clear();
			this.inputHandlers.clear();
			for (int i = 0; i < lobbies.size(); i++) {
				final int l = i;
				TextButtonComponent lobbyButton = new TextButtonComponent(
						() -> lobbySelect(l), 300, 300, fontBank.getFont("emulogic.ttf"), 0.5f, lobbies.get(i));
				lobby_buttons.add(lobbyButton);
				this.inputHandlers.add(lobby_buttons.get(i));
			}
			this.inputHandlers.add(joinButton);
			this.inputHandlers.add(createButton);
			this.inputHandlers.add(backButton);
			this.inputHandlers.add(refreshButton);
			this.inputHandlers.add(nextButton);
		}
	}

	public void nextPage() {
		if (lobbiesToRender + 4 < lobbies.size()) {
			lobbiesToRender = lobbiesToRender + 4;
		} else {
			lobbiesToRender = 0;
		}
	}

	@Override
	public ArrayList<InputHandler> getHandlers() {
		synchronized (this) {
			return new ArrayList<>(this.inputHandlers);
		}
	}

	@Override
	public void handleResize(int w, int h)
	{
		this.windowW = w;
		this.windowH = h;
		InputPipeMulti.super.handleResize(w, h);
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{

		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		// Allows escape to be pressed to return to previous menu
		if (action == GLFW_PRESS) {
			if (key == GLFW_KEY_ESCAPE) {
				System.out.println("escape pressed");
				this.nextUI = new StartUI(this);
			} else if (key == GLFW_KEY_C) {
				System.out.println("c pressed");
				this.nextUI = new LobbyCreateUI(this);
			}
		}
	}

	@Override
	public void update(double dt)
	{
		// Run the update method for all of the buttons
		joinButton.update(dt);
		createButton.update(dt);
		backButton.update(dt);
		refreshButton.update(dt);
		nextButton.update(dt);

		for (int i = 0; i < lobby_buttons.size(); i++)
		{
			lobby_buttons.get(i).update(dt);
		}
	}

	@Override
	public void render(IRenderer r)
	{
		// Set locations of the primary menu buttons
		joinButton.setX((int) (windowW / 2.0 - joinButton.getWidth() / 2.0));
		joinButton.setY((int) (windowH / 2.0 - joinButton.getHeight() / 2.0) + 300);

		createButton.setX((int) (windowW / 2.0 - createButton.getWidth() / 2.0));
		createButton.setY((int) (windowH / 2.0 - createButton.getHeight() / 2.0) + 150);

		backButton.setX((int) (windowW / 2.0 - backButton.getWidth() / 2.0));
		backButton.setY((int) (windowH / 2.0 - backButton.getHeight() / 2.0));

		refreshButton.setX((int) (windowW / 2.0 + backButton.getWidth() / 2.0) + 160);
		refreshButton.setY((int) (windowH / 2.0 - refreshButton.getHeight() / 2.0) - 160);

		nextButton.setX((int) (windowW / 2.0 + backButton.getWidth() / 2.0) + 160);
		nextButton.setY((int) (windowH / 2.0 - nextButton.getHeight() / 2.0) - 280);


		// Render these and the background image
		backgroundImage.render(r);
		joinButton.render(r);
		createButton.render(r);
		backButton.render(r);
		refreshButton.render(r);
		nextButton.render(r);


		int n = 0;
		// Set location of and render each of the lobby buttons
		for (int i = lobbiesToRender; i < lobby_buttons.size() && i < (lobbiesToRender+4); i++) {
			lobby_buttons.get(i).setX((int) (windowW / 2.0 - lobby_buttons.get(i).getWidth() / 2.0));
			lobby_buttons.get(i).setY((int) (windowH / 2.0 - lobby_buttons.get(i).getHeight() / 2.0) - 60 - (n + 1) * lobby_spacing);
			lobby_buttons.get(i).render(r);
			n++;
		}
	}

	@Override
	public UI next()
	{
		return nextUI;
	}

	@Override
	public String toString()
	{
		return "LobbyUI";
	}

	@Override
	public void destroy()
	{
		// Nothing to destroy
	}
}
