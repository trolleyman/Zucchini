package game.world;

import game.ColorUtil;
import game.InputHandler;
import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.audio.AudioManager;
import game.audio.ClientAudioManager;
import game.audio.event.AudioEvent;
import game.exception.ProtocolException;
import game.net.*;
import game.net.client.IClientConnection;
import game.net.client.IClientConnectionHandler;
import game.render.IRenderer;
import game.world.entity.*;
import game.world.entity.weapon.Handgun;
import game.world.map.Map;
import game.world.update.EntityUpdate;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The world located on the client
 * 
 * @author Callum
 */
public class ClientWorld extends World implements InputHandler, IClientConnectionHandler {
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
	private float cameraZoom = 150;
	
	/** Cached window width */
	private float windowW;
	/** Cached window height */
	private float windowH;
	
	/** dt pool */
	private double dtPool;
	
	private Action actionNorth  = new Action(ActionType.END_MOVE_NORTH);
	private Action actionSouth  = new Action(ActionType.END_MOVE_SOUTH);
	private Action actionEast   = new Action(ActionType.END_MOVE_EAST );
	private Action actionWest   = new Action(ActionType.END_MOVE_WEST );
	private AimAction actionAim = new AimAction(0.0f);
	private Action actionUse    = new Action(ActionType.END_USE);
	
	/** This is the line of sight buffer. This is meant to be null. */
	private FloatBuffer losBuf = MemoryUtil.memAllocFloat(16);
	
	/** Audio Manager */
	private AudioManager audio;
	/** Client audio manager. It can handle AudioEvent's */
	private ClientAudioManager clientAudio;
	
	/** Client UpdateArgs structure */
	private transient UpdateArgs clientUpdateArgs = new UpdateArgs(0.0, null, null, null);
	
	/**
	 * Constructs a client world
	 * @param map The map
	 * @param bank The entity bank
	 * @param _playerID The player controlled by the client
	 * @param _connection The connection to the server
	 * @param _audio The audio manager
	 */
	public ClientWorld(Map map, EntityBank bank, int _playerID, AudioManager _audio, IClientConnection _connection) {
		super(map, bank);
		this.playerID = _playerID;
		this.audio = _audio;
		this.clientAudio = new ClientAudioManager(audio);
		this.connection = _connection;
		connection.setHandler(this);
		
		this.updateStep(0.0f);
	}
	
	@Override
	protected void updateStep(double dt) {
		Player p = getPlayer();
		if (p != null) {
			this.cameraPos.set(p.position);
			audio.updateListenerPosition(p.position);
		}
		else           System.err.println("Warning: Player does not exist");
		
		// Send server data
		dtPool += dt;
		while (dtPool > Util.DT_PER_SNAPSHOT_UPDATE) {
			dtPool -= Util.DT_PER_SNAPSHOT_UPDATE;
			// Send input
			try {
				connection.sendAction(actionNorth);
				connection.sendAction(actionSouth);
				connection.sendAction(actionEast);
				connection.sendAction(actionWest);
				connection.sendAction(actionAim);
				connection.sendAction(actionUse);
			} catch (ProtocolException e) {
				// Ignore for now
				e.printStackTrace();
			}
			
			this.bank.processCacheClient();
			
			clientUpdateArgs.dt = Util.DT_PER_SNAPSHOT_UPDATE;
			clientUpdateArgs.bank = this.bank;
			clientUpdateArgs.map = this.map;
			clientUpdateArgs.audio = this.audio;
			
			for (Entity e : this.bank.entities)
				e.clientUpdate(clientUpdateArgs);
		}
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
		
		// Render line of sight
		losBuf = map.getLineOfSight(cameraPos, Player.LINE_OF_SIGHT_MAX, losBuf);
		r.drawTriangleFan(losBuf, 0, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));
		
		if (Util.isDebugRenderMode()) {
			float x = losBuf.limit() <= 1 ? 0.0f : losBuf.get(0);
			float y = losBuf.limit() <= 1 ? 0.0f : losBuf.get(1);
			for (int i = 2; i < losBuf.limit() - 3; i += 2) {
				float nx = losBuf.get(i);
				float ny = losBuf.get(i + 1);
				r.drawLine(x, y, nx, ny, ColorUtil.PINK, 1.0f);
			}
		}
		
		// Render map
		this.map.render(r);
		
		// Draw stencil
		r.enableStencilDraw(1);
		r.drawTriangleFan(losBuf, 0, 0, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

		// Disable stencil draw
		r.disableStencilDraw();
		r.enableStencil(1);
		
		// Render entities
		for (Entity e : this.bank.entities) {
			e.render(r);
		}
		
		r.disableStencil();
		
		r.getModelViewMatrix().popMatrix();
	}
	
	/**
	 * Returns the Playey object. NB: This may return null in some cases
	 */
	public Player getPlayer() {
		Entity e = this.bank.getEntity(playerID);
		if (e == null || !(e instanceof Player))
			return null;
		else
			return (Player) e;
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
			case GLFW_KEY_W: actionNorth.setType(ActionType.BEGIN_MOVE_NORTH); break;
			case GLFW_KEY_S: actionSouth.setType(ActionType.BEGIN_MOVE_SOUTH); break;
			case GLFW_KEY_D: actionEast .setType(ActionType.BEGIN_MOVE_EAST ); break;
			case GLFW_KEY_A: actionWest .setType(ActionType.BEGIN_MOVE_WEST ); break;
			case GLFW_KEY_E:
				try {
					connection.sendAction(new Action(ActionType.PICKUP));
				} catch (ProtocolException e) {
					e.printStackTrace();
				}
				break;
			}
		} else if (action == GLFW_RELEASE) { // End move
			switch (key) {
			case GLFW_KEY_W: actionNorth.setType(ActionType.END_MOVE_NORTH); break;
			case GLFW_KEY_S: actionSouth.setType(ActionType.END_MOVE_SOUTH); break;
			case GLFW_KEY_D: actionEast .setType(ActionType.END_MOVE_EAST ); break;
			case GLFW_KEY_A: actionWest .setType(ActionType.END_MOVE_WEST ); break;
			}
		}
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		// Send input to server
		float angle = (float) Util.getAngle(windowW/2, windowH/2, xpos, ypos);
		actionAim.setAngle(angle);
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		// Send input to server
		if (button == GLFW_MOUSE_BUTTON_1)
			if (action == GLFW_PRESS)
				actionUse.setType(ActionType.BEGIN_USE);
			else if (action == GLFW_RELEASE)
				actionUse.setType(ActionType.END_USE);
	}

	@Override
	public void addEntity(Entity e) {
		this.bank.addEntityCached(e);
	}
	
	@Override
	public void updateEntity(EntityUpdate update) {
		this.bank.updateEntityCached(update);
	}
	
	@Override
	public void removeEntity(int id) {
		this.bank.removeEntityCached(id);
	}

	public void destroy() {
		this.connection.close();
	}

	@Override
	public void processAudioEvent(AudioEvent ae) {
		this.clientAudio.processAudioEvent(ae);
	}
	
	/** 
	 * render2 - renders the client world but does not zoom in
	 * @param r The renderer
	 * 
	 * @author Abby Wiggins
	 */
	public void render2(IRenderer r) {
		// Set model view matrix
		r.getModelViewMatrix()
			.pushMatrix()
			.translate(100, 100, 0.0f)
			.scale(50);
			//.translate(-cameraPos.x, -cameraPos.y, 0.0f);
		this.map.render(r);
		
		// Render entities
		for (Entity e : this.bank.entities) {
			e.render(r);
		}
		
		r.getModelViewMatrix().popMatrix();
	}
}
