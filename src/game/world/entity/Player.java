package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.action.Action;
import game.action.AimAction;
import game.render.IRenderer;

public class Player extends Entity {
	private static final float SPEED = 2.0f;
	
	private Vector2f velocity = new Vector2f();
	
	private boolean moveNorth = false;
	private boolean moveSouth = false;
	private boolean moveEast  = false;
	private boolean moveWest  = false;
	
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
		r.drawBox(position.x - size/2, position.y - size/2, size, size, ColorUtil.GREEN);
	}
	
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
