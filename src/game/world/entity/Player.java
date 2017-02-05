package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.action.Action;
import game.action.AimAction;
import game.render.Align;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.World;

/**
 * Represents a player
 * 
 * @author Callum
 */
public class Player extends Entity {
	/** The speed of the player in m/s */
	private static final float SPEED = 2.0f;
	
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
	
	public void setWeapon(int _weaponID) {
		this.weaponID = _weaponID;
	}
	
	@Override
	public void update(EntityBank eb, double dt) {
		this.velocity.zero();
		if (this.moveNorth)
			this.velocity.add( 0.0f,  1.0f);
		if (this.moveSouth)
			this.velocity.add( 0.0f, -1.0f);
		if (this.moveEast)
			this.velocity.add( 1.0f,  0.0f);
		if (this.moveWest)
			this.velocity.add(-1.0f,  0.0f);
		
		this.velocity.mul(SPEED).mul((float) dt);
		this.position.add(this.velocity);
		
		// Make sure weapon keeps up with the player
		Entity e = eb.getEntity(weaponID).clone();
		e.position.set(this.position);
		e.angle = this.angle;
		eb.updateEntityCached(e);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.BM, position.x, position.y, 0.01f, 20.0f, ColorUtil.RED, this.angle);
		r.drawBox(Align.MM, position.x, position.y, 0.5f, 0.5f, ColorUtil.GREEN, this.angle);
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
		case BEGIN_SHOOT: {
			Entity e = bank.getEntity(weaponID);
			if (e != null && e instanceof Weapon) {
				Weapon wp = (Weapon)e;
				wp.fireBegin();
			} else {
				System.out.println("*Click*: No weapon.");
			}
		}
		break;
		case END_SHOOT: {
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
}
