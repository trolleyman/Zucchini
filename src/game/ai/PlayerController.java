package game.ai;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.action.Action;
import game.action.AimAction;
import game.action.IActionSource;
import game.world.World;
import game.world.entity.Player;

public class PlayerController extends EntityController {
	private static final float SPEED = 200.0f;
	
	private IActionSource source;
	
	private Vector2f velocity;
	
	private boolean moveNorth;
	private boolean moveSouth;
	private boolean moveEast;
	private boolean moveWest;
	
	public PlayerController(Player _player, IActionSource _source) {
		super(_player);
		
		this.source = _source;
		this.velocity = new Vector2f();
	}

	@Override
	public void update(World w, double dt) {
		ArrayList<Action> actions = this.source.getActions();
		
		for (Action a : actions) {
			switch (a.getType()) {
			case BEGIN_MOVE_NORTH: this.moveNorth = true; break;
			case BEGIN_MOVE_SOUTH: this.moveSouth = true; break;
			case BEGIN_MOVE_EAST : this.moveEast  = true; break;
			case BEGIN_MOVE_WEST : this.moveWest  = true; break;
			case END_MOVE_NORTH  : this.moveNorth = false; break;
			case END_MOVE_SOUTH  : this.moveSouth = false; break;
			case END_MOVE_EAST   : this.moveEast  = false; break;
			case END_MOVE_WEST   : this.moveWest  = false; break;
			case AIM: this.getEntity().angle = ((AimAction)a).getAngle(); break;
			}
		}
		
		this.velocity.zero();
		if (this.moveNorth)
			this.velocity.add( 0.0f,  1.0f);
		if (this.moveSouth)
			this.velocity.add( 0.0f, -1.0f);
		if (this.moveEast)
			this.velocity.add( 1.0f,  0.0f);
		if (this.moveWest)
			this.velocity.add(-1.0f,  0.0f);
		
		this.velocity.mul(SPEED);
	}
}
