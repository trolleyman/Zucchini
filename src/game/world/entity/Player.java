package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.action.Action;
import game.action.AimAction;
import game.render.Align;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import game.world.World;

/**
 * Represents a player
 * 
 * @author Callum
 */
public class Player extends Entity {
	/** The speed of the player in m/s */
	private static final float SPEED = 2.0f;
	/** The size of the player in m */
	private static final float SIZE = 0.5f;
	
	/**
	 * The current velocity of the player.
	 * <p>
	 * Used so that we don't have to construct a new Vector2f every update.
	 */
	private Vector2f velocity = new Vector2f();
	
	/** If the player is moving north */
	private boolean moveNorth = false;
	/** If the player is moving south */
	private boolean moveSouth = false;
	/** If the player is moving east */
	private boolean moveEast  = false;
	/** If the player is moving west */
	private boolean moveWest  = false;
	
	/** Entity ID of the weapon */
	private int weaponID = Entity.INVALID_ID;

	private boolean beganFire = false;
	
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
		if (this.moveNorth)
			this.velocity.add( 0.0f,  1.0f);
		if (this.moveSouth)
			this.velocity.add( 0.0f, -1.0f);
		if (this.moveEast)
			this.velocity.add( 1.0f,  0.0f);
		if (this.moveWest)
			this.velocity.add(-1.0f,  0.0f);
		
		this.velocity.mul(SPEED).mul((float) ua.dt);
		this.position.add(this.velocity);
		
		// Make sure weapon keeps up with the player
		Entity eFinal = ua.bank.getEntity(weaponID);
		if (eFinal != null) {
			Entity e = eFinal.clone();
			e.position.set(this.position);
			e.angle = this.angle;
			ua.bank.updateEntityCached(e);
		}
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.BM, position.x, position.y, 0.01f, 20.0f, ColorUtil.RED, this.angle);
		r.drawBox(Align.MM, position.x, position.y, SIZE, SIZE, ColorUtil.GREEN, this.angle);
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
		float sx = position.x - SIZE/2;
		float sy = position.y - SIZE/2;
		float ex = position.x + SIZE/2;
		float ey = position.y + SIZE/2;
		
		Vector2f ret = null;
		ret = PhysicsUtil.getClosest(x0, y0, ret, PhysicsUtil.intersectLineLine(x0, y0, x1, y1, sx, ey, ex, ey)); // Top
		ret = PhysicsUtil.getClosest(x0, y0, ret, PhysicsUtil.intersectLineLine(x0, y0, x1, y1, sx, sy, ex, sy)); // Bottom
		ret = PhysicsUtil.getClosest(x0, y0, ret, PhysicsUtil.intersectLineLine(x0, y0, x1, y1, sx, sy, sx, ey)); // Left
		ret = PhysicsUtil.getClosest(x0, y0, ret, PhysicsUtil.intersectLineLine(x0, y0, x1, y1, ex, sy, ex, ey)); // Right
		return ret;
	}
}
