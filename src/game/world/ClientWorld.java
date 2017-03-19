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
import game.render.*;
import game.world.entity.*;
import game.world.map.Map;
import game.world.entity.update.EntityUpdate;
import game.world.update.WorldUpdate;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

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
	
	/** This is the line of sight buffer. */
	private FloatBuffer losBuf = null;
	
	/** Audio Manager */
	private AudioManager audio;
	/** Client audio manager. It can handle AudioEvent's */
	private ClientAudioManager clientAudio;
	
	/** Client UpdateArgs structure */
	private transient UpdateArgs clientUpdateArgs = new UpdateArgs(0.0, null, null, null, new PacketCache(), null);
	
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
	
	public boolean isPlayerDead() {
		PlayerScoreboardInfo p = scoreboard.getPlayer(connection.getName());
		return p != null && p.dead;
	}
	
	@Override
	protected void updateStep(double dt) {
		Player p = getPlayer();
		if (p != null) {
			this.cameraPos.lerp(p.position, (float)dt * 20.0f);
			audio.updateListenerPosition(p.position);
		} else if (isPlayerDead()) {
			Vector4f rect = map.getRect();
			float mx = rect.x;
			float my = rect.y;
			float mw = rect.z;
			float mh = rect.w;
			
			// Calculate target zoom and position
			Vector2f targetPosition = Util.pushTemporaryVector2f();
			targetPosition.set(mx, my).add(mw/2, mh/2);
			float targetZoomW = windowW / (mw + 2);
			float targetZoomH = windowH / (mh + 2);
			float targetZoom = Math.min(targetZoomW, targetZoomH);
			
			// Lerp position and zoom
			this.cameraZoom += (targetZoom - this.cameraZoom) * (float)dt * 0.8f;
			this.cameraPos.lerp(targetPosition, (float)dt * 1.0f);
			Util.popTemporaryVector2f();
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
			clientUpdateArgs.scoreboard = null;
			
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
		if (buf == null)
			return;
		
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
		glEnable(GL_SCISSOR_TEST);
		glScissor((int)(x+mmBorder), (int)(y+mmBorder), (int)(w-mmBorder*2), (int)(h-mmBorder*2));
		
		this.render(r, x+w/2, y+h/2, zoom, true);
		glDisable(GL_SCISSOR_TEST);
	}
	
	/**
	 * Renders the world
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		this.render(r, r.getWidth()/2, r.getHeight()/2, cameraZoom, false);
	}
	
	/**
	 * Renders the world with a specified origin and zoom
	 * @param r The renderer
	 * @param x The window x-coordinate of the origin
	 * @param y The window y-coordinate of the origin
	 * @param zoom The zoom of the camera
	 */
	public void render(IRenderer r, float x, float y, float zoom, boolean drawWalls) {
		// Set model view matrix
		Player p = getPlayer();
		if (isPlayerDead()) {
			RenderSettings s = r.getRenderSettings();
			s.drawLineOfSightStencil = false;
			r.setRenderSettings(s);
		} else {
			RenderSettings s = r.getRenderSettings();
			s.drawLineOfSightStencil = true;
			r.setRenderSettings(s);
		}
		
		r.getModelViewMatrix()
				.pushMatrix()
				.translate(x, y, 0.0f)
				.scale(zoom)
				.translate(-cameraPos.x, -cameraPos.y, 0.0f);
		
		calculateLineOfSight();
		
		// === Draw World ===
		// Get a new framebuffer for rendering the world to
		Framebuffer worldFramebuffer = r.getFreeFramebuffer();
		worldFramebuffer.bind();
		
		if (!r.getRenderSettings().debugDrawLightingFramebuffer) {
			// Render the world
			drawWorld(r);
		}
		
		// === Draw Lighting ===
		// Get another new framebuffer for rendering the lighting to
		Framebuffer lightFramebuffer = r.getFreeFramebuffer();
		lightFramebuffer.bind();
		
		// Setup the blending function
		r.setLightingBlend();
		if (p != null && r.getRenderSettings().drawLineOfSightStencil) {
			// Render the line of sight stencil
			drawLineOfSightStencil(r);
		}
		// Draw the lighting
		drawLighting(r);
		r.disableStencil();
		
		// === Draw world with lighting ===
		Framebuffer.bindDefault();
		r.setDefaultBlend();
		if (r.getRenderSettings().debugDrawLightingFramebuffer) {
			r.drawFramebuffer(lightFramebuffer);
		} else {
			r.drawWorldWithLighting(worldFramebuffer, lightFramebuffer);
		}
		
		// Render map walls
		if (drawWalls)
			this.map.renderWalls(r);
		
		r.getModelViewMatrix().popMatrix();
	}
	
	private void calculateLineOfSight() {
		// Get player
		Player p = getPlayer();
		
		if (p != null) {
			// Render line of sight
			Vector2f pos = p.position;
			// losBuf = map.getLineOfSight(pos, Player.LINE_OF_SIGHT_MAX, losBuf);
			losBuf = map.getLineOfSight(pos, Player.LINE_OF_SIGHT_MAX, p.angle, Player.LINE_OF_SIGHT_FOV, losBuf);
		} else {
			// Display whole screen if there is no player
		}
	}
	
	private void drawWorld(IRenderer r) {
		// Get player
		Player p = getPlayer();
		
		// Render map background
		this.map.renderFloor(r);
		
		if (p != null && r.getRenderSettings().debugDrawLineOfSightLines) {
			drawDebugLines(r, losBuf);
		}
		
		// Render entities
		for (Entity e : this.bank.entities.values()) {
			e.render(r, map);
		}
	}
	
	private void drawLineOfSightStencil(IRenderer r) {
		r.enableStencilDraw(1);
		
		r.drawTriangleFan(losBuf, 0, 0, ColorUtil.WHITE);
		
		r.disableStencilDraw();
		r.enableStencil(1);
	}
	
	private void drawLighting(IRenderer r) {
		for (Entity e : this.bank.entities.values()) {
			e.renderLight(r, map);
		}
	}
	
	public void renderHUD(IRenderer r) {
		// Render start time
		if (this.startTime != 0.0f) {
			int i = (int)Math.floor(this.startTime + 1);
			float scale = 1.0f + 2.0f * (this.startTime - (float)Math.floor(this.startTime));
			r.drawText(r.getFontBank().getFont("emulogic.ttf"),
					"" + i, Align.MM, false, r.getWidth()/2, r.getHeight()/2, scale, ColorUtil.RED);
		}
		
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
		
		Player p = getPlayer();
		if (p != null) {
			// Draw current ammo
			Item i = p.getHeldItem();
			if (i != null) {
				i.renderUI(r);
			}
			
			// Render health
			float barWidth = 300.0f;
			float barHeight = 80.0f;
			float segments = barWidth / p.getMaxHealth();
			r.drawBox(Align.TR, windowW - Util.HUD_PADDING, windowH - Util.HUD_PADDING, barWidth, barHeight, ColorUtil.GREEN);//max health
			r.drawBox(Align.TR, windowW - Util.HUD_PADDING, windowH - Util.HUD_PADDING, segments * (p.getMaxHealth() - p.getHealth()), barHeight, ColorUtil.RED);
		}
		
		// Draw mini-map
		if (!isPlayerDead())
			this.renderMiniMap(r, Util.HUD_PADDING, r.getHeight() - 300.0f - Util.HUD_PADDING, 300.0f, 300.0f, 20.0f);
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
					connection.error(e);
				}
				break;
			case GLFW_KEY_LEFT_SHIFT:
			case GLFW_KEY_RIGHT_SHIFT:
				try {
					connection.sendAction(new Action(ActionType.TOGGLE_LIGHT));
				} catch (ProtocolException e) {
					connection.error(e);
				}
				break;
			case GLFW_KEY_R:
				try {
					connection.sendAction(new Action(ActionType.RELOAD));
				} catch (ProtocolException e) {
					connection.error(e);
				}
				break;
			case GLFW_KEY_ENTER:
				Player p = getPlayer();
				System.out.println("Player: " + (p == null ? "null" : p.position));
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
		float angle = Util.getAngle(windowW/2, windowH/2, (float)xpos, (float)ypos);
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
			connection.sendLobbyLeaveRequest();
		} catch (ProtocolException e) {
			connection.error(e);
		}
	}
}
