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
import game.net.Message;
import game.net.PacketCache;
import game.net.client.IClientConnection;
import game.net.client.IClientConnectionHandler;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.world.entity.*;
import game.world.map.Map;
import game.world.entity.update.EntityUpdate;
import game.world.update.WorldUpdate;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The world located on the client
 * 
 * @author Callum
 */
public class ClientWorld extends World implements InputHandler, IClientConnectionHandler {
	private static final int NUM_REPEATS = 3;
	
	/** The ID of the player */
	private int playerID;
	/** The connection to the server */
	private IClientConnection connection;
	
	private final Object messageLogLock = new Object();
	private final ArrayList<Message> messageLog;
	
	/** Temporary variable to store message log color */
	private final Vector4f messageLogColor = new Vector4f();
	
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
	
	private int dirtyActionNorth = NUM_REPEATS;
	private int dirtyActionSouth = NUM_REPEATS;
	private int dirtyActionEast  = NUM_REPEATS;
	private int dirtyActionWest  = NUM_REPEATS;
	private int dirtyActionAim   = NUM_REPEATS;
	private int dirtyActionUse   = NUM_REPEATS;
	
	private Action actionNorth  = new Action(ActionType.END_MOVE_NORTH);
	private Action actionSouth  = new Action(ActionType.END_MOVE_SOUTH);
	private Action actionEast   = new Action(ActionType.END_MOVE_EAST );
	private Action actionWest   = new Action(ActionType.END_MOVE_WEST );
	private AimAction actionAim = new AimAction(0.0f);
	private Action actionUse    = new Action(ActionType.END_USE);
	
	/** This is the max line of sight buffer. */
	private FloatBuffer losMaxBuf = MemoryUtil.memAllocFloat(32);
	
	/** Audio Manager */
	private AudioManager audio;
	/** Client audio manager. It can handle AudioEvent's */
	private ClientAudioManager clientAudio;
	
	/** Client UpdateArgs structure */
	private transient UpdateArgs clientUpdateArgs = new UpdateArgs(0.0, null, null, null, new PacketCache());
	
	/**
	 * Constructs a client world
	 * @param map The map
	 * @param bank The entity bank
	 * @param _playerID The player controlled by the client
	 * @param _connection The connection to the server
	 * @param _audio The audio manager
	 */
	public ClientWorld(Map map, EntityBank bank, int _playerID, AudioManager _audio, IClientConnection _connection, ArrayList<Message> _messageLog) {
		super(map, bank);
		this.playerID = _playerID;
		this.audio = _audio;
		this.clientAudio = new ClientAudioManager(audio);
		this.connection = _connection;
		this.messageLog = _messageLog;
		connection.setHandler(this);
		
		this.updateStep(0.0f);
	}
	
	@Override
	protected void updateStep(double dt) {
		Player p = getPlayer();
		if (p != null) {
			this.cameraPos.lerp(p.position, (float)dt * 20.0f);
			audio.updateListenerPosition(p.position);
		}
		
		// Send server data
		dtPool += dt;
		while (dtPool > Util.DT_PER_SNAPSHOT_UPDATE) {
			dtPool -= Util.DT_PER_SNAPSHOT_UPDATE;
			// Send input
			try {
				if (dirtyActionNorth > 0) {
					connection.sendAction(actionNorth);
					dirtyActionNorth--;
				}
				if (dirtyActionSouth > 0) {
					connection.sendAction(actionSouth);
					dirtyActionSouth--;
				}
				if (dirtyActionEast > 0) {
					connection.sendAction(actionEast);
					dirtyActionEast--;
				}
				if (dirtyActionWest > 0) {
					connection.sendAction(actionWest);
					dirtyActionWest--;
				}
				if (dirtyActionAim > 0) {
					connection.sendAction(actionAim);
					dirtyActionAim--;
				}
				if (dirtyActionUse > 0) {
					connection.sendAction(actionUse);
					dirtyActionUse--;
				}
			} catch (ProtocolException e) {
				// Ignore for now
				e.printStackTrace();
			}
			
			this.bank.processCacheClient();
			
			clientUpdateArgs.dt = Util.DT_PER_SNAPSHOT_UPDATE;
			clientUpdateArgs.bank = this.bank;
			clientUpdateArgs.map = this.map;
			clientUpdateArgs.audio = this.audio;
			
			for (Entity e : this.bank.entities.values())
				e.clientUpdate(clientUpdateArgs);
			
			try {
				clientUpdateArgs.packetCache.processCache(connection);
			} catch (ProtocolException e) {
				// This is ok. This will be handled when the UI loops
			}
		}
	}
	
	private void drawDebugLines(IRenderer r, FloatBuffer buf) {
		float x = buf.limit() <= 1 ? 0.0f : buf.get(0);
		float y = buf.limit() <= 1 ? 0.0f : buf.get(1);
		for (int i = 2; i < buf.limit() - 3; i += 2) {
			float nx = buf.get(i);
			float ny = buf.get(i + 1);
			r.drawLine(x, y, nx, ny, ColorUtil.PINK, 1.0f);
		}
	}
	
	/**
	 * Renders the minimap at x,y on the screen with width w and height h.
	 * @param r The renderer
	 */
	public void renderMiniMap(IRenderer r, float x, float y, float w, float h, float zoom) {
		// Draw border
		float mmBorder = 10.0f;
		r.drawBox(Align.BL, x, y, w, h, ColorUtil.WHITE);
		r.drawBox(Align.BL, x+mmBorder, y+mmBorder, w-mmBorder*2, h-mmBorder*2, ColorUtil.BLACK);
		
		// Draw minimap
		r.enableStencilDraw(2);
		r.drawBox(Align.BL, x+mmBorder, y+mmBorder, w-mmBorder*2, h-mmBorder*2, ColorUtil.WHITE);
		r.disableStencilDraw();
		r.enableStencil(2);
		
		r.getModelViewMatrix()
				.pushMatrix()
				.translate(x, y, 0.0f)
				.translate(w/2, h/2, 0.0f)
				.scale(zoom)
				.translate(-cameraPos.x, -cameraPos.y, 0.0f);
		
		map.render(r);
		
		r.getModelViewMatrix().popMatrix();
		r.disableStencil();
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
		
		// Get player
		Player p = getPlayer();
		
		// Render map background
		this.map.renderBackground(r);
		
		if (p != null) {
			// Render line of sight
			Vector2f pos = p.position;
			//losMinBuf = map.getLineOfSight(pos, Player.LINE_OF_SIGHT_MIN, losMinBuf);
			//r.drawTriangleFan(losMinBuf, 0, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));
			losMaxBuf = map.getLineOfSight(pos, Player.LINE_OF_SIGHT_MAX, p.angle, Player.LINE_OF_SIGHT_FOV, losMaxBuf);
			r.drawTriangleFan(losMaxBuf, 0, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));
		}
		
		if (Util.isDebugRenderMode()) {
			//drawDebugLines(r, losMinBuf);
			drawDebugLines(r, losMaxBuf);
		}
		
		// Render map foreground
		this.map.renderForeground(r);
		
		if (p != null && !Util.isDebugRenderMode()) {
			// Draw stencil
			r.enableStencilDraw(1);
			//r.drawTriangleFan(losMinBuf, 0, 0, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			r.drawTriangleFan(losMaxBuf, 0, 0, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			
			// Disable stencil draw
			r.disableStencilDraw();
			r.enableStencil(1);
		}
		
		// Render entities
		for (Entity e : this.bank.entities.values()) {
			e.render(r);
		}
		
		r.disableStencil();
		
		r.getModelViewMatrix().popMatrix();
		
		// Render start time
		if (this.startTime != 0.0f) {
			int i = (int)Math.floor(this.startTime + 1);
			float scale = 1.0f + 2.0f * (this.startTime - (float)Math.floor(this.startTime));
			r.drawText(r.getFontBank().getFont("emulogic.ttf"),
					"" + i, Align.MM, false, r.getWidth()/2, r.getHeight()/2, scale, ColorUtil.RED);
		}
		
		// === Render UI ===
		// Draw messages
		synchronized (messageLogLock) {
			Font font = r.getFontBank().getFont("emulogic.ttf");
			float scale = 0.5f;
			float mh = font.getHeight(scale);
			float width = 1000.0f;
			float y = Util.HUD_PADDING + mh;
			for (int i = messageLog.size()-1; i >= 0; i--) {
				Message m = messageLog.get(i);
				double dt = (System.nanoTime() - m.timeReceived) / (double)Util.NANOS_PER_SECOND;
				float alpha = (float) (4.0 - dt);
				alpha = Math.min(1.0f, alpha);
				if (alpha <= 0.0f)
					break;
				
				messageLogColor.set(0.1f, 0.1f, 0.1f, alpha * 0.7f);
				r.drawBox(Align.BL, Util.HUD_PADDING, y, width, mh, messageLogColor);
				messageLogColor.set(1.0f, 1.0f, 1.0f, alpha);
				r.drawText(font, m.toString(), Align.ML, false, Util.HUD_PADDING + 5.0f, y+mh/2, scale, messageLogColor);
				y += mh;
			}
		}
		
		// Draw mini-map
		this.renderMiniMap(r, Util.HUD_PADDING, r.getHeight() - 300.0f - Util.HUD_PADDING, 300.0f, 300.0f, 30.0f);
		
		// Draw current ammo
		if (p != null) {
			Item i = p.getHeldItem();
			if (i != null) {
				i.renderUI(r);
			}
		}
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
			case GLFW_KEY_W: actionNorth.setType(ActionType.BEGIN_MOVE_NORTH); dirtyActionNorth = NUM_REPEATS; break;
			case GLFW_KEY_S: actionSouth.setType(ActionType.BEGIN_MOVE_SOUTH); dirtyActionSouth = NUM_REPEATS; break;
			case GLFW_KEY_D: actionEast .setType(ActionType.BEGIN_MOVE_EAST ); dirtyActionEast  = NUM_REPEATS; break;
			case GLFW_KEY_A: actionWest .setType(ActionType.BEGIN_MOVE_WEST ); dirtyActionWest  = NUM_REPEATS; break;
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
			case GLFW_KEY_W: actionNorth.setType(ActionType.END_MOVE_NORTH); dirtyActionNorth = NUM_REPEATS; break;
			case GLFW_KEY_S: actionSouth.setType(ActionType.END_MOVE_SOUTH); dirtyActionSouth = NUM_REPEATS; break;
			case GLFW_KEY_D: actionEast .setType(ActionType.END_MOVE_EAST ); dirtyActionEast  = NUM_REPEATS; break;
			case GLFW_KEY_A: actionWest .setType(ActionType.END_MOVE_WEST ); dirtyActionWest  = NUM_REPEATS; break;
			}
		}
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		// Send input to server
		float angle = (float) Util.getAngle(windowW/2, windowH/2, xpos, ypos);
		actionAim.setAngle(angle);
		dirtyActionAim = NUM_REPEATS;
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		// Send input to server
		if (button == GLFW_MOUSE_BUTTON_1) {
			if (action == GLFW_PRESS) {
				actionUse.setType(ActionType.BEGIN_USE);
				dirtyActionUse = NUM_REPEATS;
			} else if (action == GLFW_RELEASE) {
				actionUse.setType(ActionType.END_USE);
				dirtyActionUse = NUM_REPEATS;
			}
		}
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
	
	@Override
	public void processAudioEvent(AudioEvent ae) {
		this.clientAudio.processAudioEvent(ae);
	}
	
	@Override
	public void handleWorldUpdate(WorldUpdate update) {
		update.updateWorld(this);
	}
	
	@Override
	public void handleMessage(String name, String msg) {
		synchronized (messageLogLock) {
			messageLog.add(new Message(name, msg));
		}
	}
	
	public void destroy() {
		try {
			this.connection.sendLobbyLeaveRequest();
		} catch (ProtocolException e) {
			// The connection handler takes care of this
		}
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
		for (Entity e : this.bank.entities.values()) {
			e.render(r);
		}
		
		r.getModelViewMatrix().popMatrix();
	}
}
