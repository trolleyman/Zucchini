package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.action.Action;
import game.action.AimAction;
import game.render.Align;
import game.render.IRenderer;

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
	
	/**
	 * Constructs a new player at the specified position
	 * @param position The position
	 */
	public Player(Vector2f position) {
		super(position);
	}
	
	@Override
	public void update(double dt) {
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
	}
	
	@Override
	public void render(IRenderer r) {
		float size = 0.5f;
		r.drawBox(Align.BL, position.x - size/2, position.y - size/2, size, size, ColorUtil.GREEN);
	}
	
	/**
	 * Handles an action on the player
	 * @param a The action
	 */
	public void handleAction(Action a) {
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
		case SHOOT: /* TODO: Implement shooting */ System.out.println("BANG!"); break;
		}
	}
}
