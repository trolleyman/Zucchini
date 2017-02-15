package game.world.entity;

import game.ColorUtil;
import game.Util;
import game.action.Action;
import game.action.AimAction;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.render.TextureBank;
import game.world.EntityBank;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import org.joml.Vector2f;

/**
 * Represents a player
 *
 * @author Callum
 */
public class Player extends Entity {
	/** The size of the player's line of sight */
	public static final float LINE_OF_SIGHT_MAX = 20.0f;
	
	/** The speed of the player in m/s */
	private static final float SPEED = 1.5f;
	/** The radius of the player in m */
	private static final float RADIUS = 0.3f;
	
	/**
	 * The current velocity of the player.
	 * <p>
	 * Used so that we don't have to construct a new Vector2f every update.
	 */
	private transient Vector2f velocity = new Vector2f();
	
	private boolean isMoving = false;
	/** If the player is moving north */
	private transient boolean moveNorth = false;
	/** If the player is moving south */
	private transient boolean moveSouth = false;
	/** If the player is moving east */
	private transient boolean moveEast  = false;
	/** If the player is moving west */
	private transient boolean moveWest  = false;
	
	/** Has the player been assigned a footstep sound source? */
	private boolean soundSourceInit = false;
	private int walkingSoundID;//sound source id associated with player movement
	
	/** Entity ID of the weapon */
	private int weaponID = Entity.INVALID_ID;
	
	private transient boolean beganFire = false;
	
	/**
	 * Clones the specified player
	 * @param p The player
	 */
	public Player(Player p) {
		super(p);
		
		this.velocity = p.velocity;
		
		this.moveNorth = p.moveNorth;
		this.moveSouth = p.moveSouth;
		this.moveEast = p.moveEast;
		this.moveWest = p.moveWest;
		
		this.weaponID = p.weaponID;
		
		this.beganFire = p.beganFire;
	}
	
	/**
	 * Constructs a new player at the specified position holding a weapon
	 * @param position The position
	 * @param _weaponID The current weapon ID
	 */
	public Player(Vector2f position, int _weaponID) {
		super(position);
		this.weaponID = _weaponID;
	}
	
	@Override
	protected float getMaxHealth() {
		return 10.0f;
	}
	
	public void setWeapon(int _weaponID) {
		this.weaponID = _weaponID;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		this.velocity.zero();
		if (!soundSourceInit){
			this.walkingSoundID = ua.audio.playLoop("footsteps_walking.wav", 1.0f);
			ua.audio.pauseLoop(this.walkingSoundID);
			soundSourceInit = true;
		}
				
		if (this.moveNorth){
			this.velocity.add( 0.0f,  1.0f);
		}
		if (this.moveSouth){
			this.velocity.add( 0.0f, -1.0f);
		}
		if (this.moveEast){
			this.velocity.add( 1.0f,  0.0f);
		}
		if (this.moveWest){
			this.velocity.add(-1.0f,  0.0f);
		}
		
		//Play walking sounds
		//System.out.println(moveNorth + " " +moveSouth+ " " +moveEast+ " " +moveWest);
		if(moveNorth || moveSouth || moveEast || moveWest ){
			//System.out.println("Starting sound id: " + walkingSoundID);
			ua.audio.continueLoop(this.walkingSoundID);
		} else {
			ua.audio.pauseLoop(this.walkingSoundID);
			//System.out.println("Stopping sound id: " + walkingSoundID);
		}
		
//		if (this.walkingSoundID==1){
//			System.out.println("Velocity: " + velocity);
//			System.out.println("Position: " + position);
//		}
		this.velocity.mul(SPEED).mul((float) ua.dt);
		this.position.add(this.velocity);
		ua.bank.updateEntityCached(this);
		
		

		// Make sure weapon keeps up with the player
		Entity eFinal = ua.bank.getEntity(weaponID);
		if (eFinal != null) {
			Entity e = eFinal.clone();
			e.position.set(this.position);
			e.angle = this.angle;
			ua.bank.updateEntityCached(e);
		}
		
		// Get intersection
		Vector2f intersection = Util.pushTemporaryVector2f();
		if (ua.map.intersectsCircle(position.x, position.y, RADIUS, intersection) != null) {
			// Intersection with map - push out
			Vector2f temp = Util.pushTemporaryVector2f();
			temp.set(position)
				.sub(intersection)
				.normalize()
				.mul(RADIUS)
				.add(intersection);
			position.set(temp);
			Util.popTemporaryVector2f();
		}
		Util.popTemporaryVector2f();
		
	}
	
	@Override
	public void render(IRenderer r) {
		float x = position.x + LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
		float y = position.y + LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		Texture playerTexture = r.getImageBank().getTexture("player_v1.png");
		r.drawTexture(playerTexture, Align.MM, position.x, position.y, RADIUS*2, RADIUS*2, angle);
	}
	
	/**
	 * Handles an action on the player
	 * @param bank The entity bank
	 * @param a The action
	 */
	public void handleAction(EntityBank bank, Action a) {
		switch (a.getType()) {
		case BEGIN_MOVE_NORTH: this.moveNorth = true ; break;
		case BEGIN_MOVE_SOUTH: this.moveSouth = true ; break;
		case BEGIN_MOVE_EAST : this.moveEast  = true ; break;
		case BEGIN_MOVE_WEST : this.moveWest  = true ; break;
		case END_MOVE_NORTH  : this.moveNorth = false; break;
		case END_MOVE_SOUTH  : this.moveSouth = false; break;
		case END_MOVE_EAST   : this.moveEast  = false; break;
		case END_MOVE_WEST   : this.moveWest  = false; break;
		case AIM: super.angle = ((AimAction)a).getAngle(); break;
		case BEGIN_FIRE: {
			if (!this.beganFire) {
				this.beganFire = true;
				Entity e = bank.getEntity(weaponID);
				if (e != null && e instanceof Weapon) {
					Weapon wp = (Weapon)e;
					wp.fireBegin();
				} else {
					System.out.println("*Click*: No weapon.");
				}
			}
		}
		break;
		case END_FIRE: {
			this.beganFire = false;
			Entity e = bank.getEntity(weaponID);
			if (e != null && e instanceof Weapon) {
				Weapon wp = (Weapon)e;
				wp.fireEnd();
			}
		}
		break;
		}
	}
	
	@Override
	public Player clone() {
		return new Player(this);
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(this.position.x, this.position.y, RADIUS, x0, y0, x1, y1, null);
	}
}
