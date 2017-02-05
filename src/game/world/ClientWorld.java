package game.world;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.InputHandler;
import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.net.DummyConnection;
import game.net.IClientConnection;
import game.net.IClientConnectionHandler;
import game.render.IRenderer;
import game.world.entity.Entity;
import game.world.entity.Handgun;
import game.world.entity.Player;
import game.world.map.Map;
import game.world.map.TestMap;

/**
 * The world located on the client
 * 
 * @author Callum
 */
public class ClientWorld extends World implements InputHandler, IClientConnectionHandler {
	/**
	 * Creates a test single player world
	 */
	public static ClientWorld createTestWorld() {
		// Create map
		Map map = Map.createTestMap();
		
		// Create entity bank and add entities
		EntityBank serverBank = new EntityBank();
		int weaponID = serverBank.updateEntity(new Handgun(new Vector2f(0.5f, 0.5f)));
		int playerID = serverBank.updateEntity(new Player(new Vector2f(0.5f, 0.5f), weaponID));
		
		// Create server world
		ServerWorld serverWorld = new ServerWorld(map, serverBank, new ArrayList<>());
		
		// Create connection
		DummyConnection connection = new DummyConnection(serverWorld, playerID);
		
		// Create client
		ClientWorld clientWorld = new ClientWorld(map, new EntityBank(), playerID, connection);
		
		// Start server thread
		Thread t = new Thread(connection);
		t.setName("Connection Handler");
		t.start();
		
		// Return client world
		return clientWorld;
	}
	
	/** The ID of the player */
	private int playerID;
	/** The connection to the server */
	private IClientConnection connection;
	
	/**
	 * The position of the camera.
	 */
	private Vector2f cameraPos = new Vector2f();
	
	/**
	 * How much to scale the world co-ordinates to screen co-ordinates.
	 * 
	 * For example, a box at 1,2 with a zoom of 2 would render to the screen at 2,4.
	 */
	private float cameraZoom = 100;
	
	/** Cached window width */
	private float windowW;
	/** Cached window height */
	private float windowH;
	
	/**
	 * Constructs a client world
	 * @param map The map
	 * @param bank The entity bank
	 * @param _playerID The player controlled by the client
	 * @param _connection The connection to the server
	 */
	public ClientWorld(Map map, EntityBank bank, int _playerID, IClientConnection _connection) {
		super(map, bank);
		this.playerID = _playerID;
		this.connection = _connection;
		connection.setHandler(this);
		
		this.updateStep(0.0f);
	}
	
	@Override
	protected void updateStep(double dt) {
		Entity e = this.bank.getEntity(this.playerID);
		if (e != null && e instanceof Player)
			this.cameraPos.set(e.position);
		else
			System.err.println("Warning: Player does not exist");
	}
	
	/**
	 * Renders the world
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		// Set model view matrix
		r.getModelViewMatrix()
			.pushMatrix()
			.translate(r.getWidth()/2, r.getHeight()/2, 0.0f)
			.scale(cameraZoom)
			.translate(-cameraPos.x, -cameraPos.y, 0.0f);
		
		// Render map
		this.map.render(r);
		
		// Render entities
		for (Entity e : this.bank.entities) {
			e.render(r);
		}
		
		r.getModelViewMatrix().popMatrix();
	}
	
	@Override
	public void handleResize(int w, int h) {
		this.windowW = w;
		this.windowH = h;
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		// Send input to server
		if (action == GLFW_PRESS) { // Begin move
			switch (key) {
			case GLFW_KEY_W: connection.sendAction(new Action(ActionType.BEGIN_MOVE_NORTH)); break;
			case GLFW_KEY_S: connection.sendAction(new Action(ActionType.BEGIN_MOVE_SOUTH)); break;
			case GLFW_KEY_D: connection.sendAction(new Action(ActionType.BEGIN_MOVE_EAST )); break;
			case GLFW_KEY_A: connection.sendAction(new Action(ActionType.BEGIN_MOVE_WEST )); break;
			}
		} else if (action == GLFW_RELEASE) { // End move
			switch (key) {
			case GLFW_KEY_W: connection.sendAction(new Action(ActionType.END_MOVE_NORTH)); break;
			case GLFW_KEY_S: connection.sendAction(new Action(ActionType.END_MOVE_SOUTH)); break;
			case GLFW_KEY_D: connection.sendAction(new Action(ActionType.END_MOVE_EAST )); break;
			case GLFW_KEY_A: connection.sendAction(new Action(ActionType.END_MOVE_WEST )); break;
			}
		}
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		// Send input to server
		float angle = (float) Util.getAngle(windowW/2, windowH/2, xpos, ypos);
		connection.sendAction(new AimAction(angle));
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		// Send input to server
		if (button == GLFW_MOUSE_BUTTON_1)
			if (action == GLFW_PRESS)
				connection.sendAction(new Action(ActionType.BEGIN_SHOOT));
			else if (action == GLFW_RELEASE)
				connection.sendAction(new Action(ActionType.END_SHOOT));
	}

	@Override
	public void updateEntity(Entity e) {
		this.bank.updateEntityCached(e);
	}

	@Override
	public void removeEntity(int id) {
		this.bank.removeEntityCached(id);
	}

	public void destroy() {
		this.connection.close();
	}
}
