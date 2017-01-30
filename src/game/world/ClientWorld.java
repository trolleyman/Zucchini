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
import game.render.IRenderer;
import game.world.entity.Entity;
import game.world.entity.Player;

public class ClientWorld extends World implements InputHandler {
	public static ClientWorld createTestWorld() {
		Map map = new TestMap();
		
		ArrayList<Entity> entities = new ArrayList<>();
		
		Player player = new Player(new Vector2f(0.5f, 0.5f));
		
		DummyConnection connection = new DummyConnection(player);
		
		return new ClientWorld(map, entities, player, connection);
	}
	
	private Player player;
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
	
	public ClientWorld(Map _map, ArrayList<Entity> _entities, Player _player, IClientConnection _connection) {
		super(_map, _entities);
		this.player = _player;
		this.connection = _connection;
		
		// Ensure that player is an entity in the world
		this.addEntity(player);
		
		this.cameraPos = this.player.position;
	}
	
	@Override
	protected void updateStep(double dt) {
		this.cameraPos = this.player.position;
		
		// TODO: For debugging. Do not use in production.
		this.player.update(dt);
	}
	
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
		if (action == GLFW_PRESS) { // Begin move
			switch (key) {
			case GLFW_KEY_W: connection.sendAction(new Action(ActionType.BEGIN_MOVE_NORTH)); break;
			case GLFW_KEY_S: connection.sendAction(new Action(ActionType.BEGIN_MOVE_SOUTH)); break;
			case GLFW_KEY_D: connection.sendAction(new Action(ActionType.BEGIN_MOVE_EAST)); break;
			case GLFW_KEY_A: connection.sendAction(new Action(ActionType.BEGIN_MOVE_WEST)); break;
			}
		} else if (action == GLFW_RELEASE) { // End move
			switch (key) {
			case GLFW_KEY_W: connection.sendAction(new Action(ActionType.END_MOVE_NORTH)); break;
			case GLFW_KEY_S: connection.sendAction(new Action(ActionType.END_MOVE_SOUTH)); break;
			case GLFW_KEY_D: connection.sendAction(new Action(ActionType.END_MOVE_EAST)); break;
			case GLFW_KEY_A: connection.sendAction(new Action(ActionType.END_MOVE_WEST)); break;
			}
		}
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		float x = ((float) xpos / cameraZoom) - cameraPos.x;
		float y = ((float) ypos / cameraZoom) - cameraPos.y;
		float angle = (float) Math.atan(x / y);
		connection.sendAction(new AimAction(angle));
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		connection.sendAction(new Action(ActionType.SHOOT));
	}
}
