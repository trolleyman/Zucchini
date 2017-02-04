package game.world;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.InputHandler;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.net.DummyConnection;
import game.net.IClientConnection;
import game.net.IClientConnectionHandler;
import game.render.IRenderer;
import game.world.entity.Entity;
import game.world.entity.Player;

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
		Map map = new TestMap();
		
		ArrayList<Entity> serverEntities = new ArrayList<>();
		Player serverPlayer = new Player(new Vector2f(0.5f, 0.5f));
		serverEntities.add(serverPlayer);
		ServerWorld serverWorld = new ServerWorld(map, serverEntities, new ArrayList<>());
		
		DummyConnection connection = new DummyConnection(serverWorld, (Player) serverPlayer.clone());
		
		return new ClientWorld(map, new ArrayList<>(), serverPlayer, connection);
	}
	
	/** The player controlled by the client */
	private Player player;
	/** The connection to the server */
	private IClientConnection connection;
	
	/**
	 * The position of the camera.
	 */
	private Vector2f cameraPos;
	
	/**
	 * How much to scale the world co-ordinates to screen co-ordinates.
	 * 
	 * For example, a box at 1,2 with a zoom of 2 would render to the screen at 2,4.
	 */
	private float cameraZoom = 100;
	
	/**
	 * Constructs a client world
	 * @param _map The map
	 * @param _entities The list of entities to start with
	 * @param _player The player controlled by the client
	 * @param _connection The connection to the server
	 */
	public ClientWorld(Map _map, ArrayList<Entity> _entities, Player _player, IClientConnection _connection) {
		super(_map, _entities);
		this.player = _player;
		this.connection = _connection;
		
		// Ensure that player is an entity in the world
		this.updateEntity(player);
		
		this.cameraPos = this.player.position;
	}
	
	@Override
	protected void updateStep(double dt) {
		this.cameraPos = this.player.position;
		
		// TODO: For debugging. Do not use in production.
		this.player.update(dt);
	}
	
	/**
	 * Renders the world
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		r.getModelViewMatrix()
			.pushMatrix()
			.translate(r.getWidth()/2, r.getHeight()/2, 0.0f)
			.scale(cameraZoom)
			.translate(-cameraPos.x, -cameraPos.y, 0.0f);
		
		// Render map
		this.map.render(r);
		
		// Render entities
		for (Entity e : entities) {
			e.render(r);
		}
		
		r.getModelViewMatrix().popMatrix();
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
		float x = ((float) xpos / cameraZoom) - cameraPos.x;
		float y = ((float) ypos / cameraZoom) - cameraPos.y;
		float angle = (float) Math.atan(x / y);
		connection.sendAction(new AimAction(angle));
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		// Send input to server
		if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1)
			connection.sendAction(new Action(ActionType.SHOOT));
	}
}
