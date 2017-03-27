package game.world.entity;

import game.Util;
import game.action.Action;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import game.world.map.Map;
import org.joml.Vector2f;

/**
 * A human player
 */
public class HumanPlayer extends Player {
	/** If the player is moving north */
	private transient boolean moveNorth = false;
	/** If the player is moving south */
	private transient boolean moveSouth = false;
	/** If the player is moving east */
	private transient boolean moveEast = false;
	/** If the player is moving west */
	private transient boolean moveWest = false;
	
	public HumanPlayer(int team, Vector2f position, String name) {
		super(team, position, name);
	}
	
	public HumanPlayer(int team, Vector2f position, String name, Item item) {
		super(team, position, name, item);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		{
			Vector2f temp = Util.pushTemporaryVector2f();
			temp.zero();
			if (this.moveNorth)
				temp.add(0.0f, 1.0f);
			if (this.moveSouth)
				temp.add(0.0f, -1.0f);
			if (this.moveEast)
				temp.add(1.0f, 0.0f);
			if (this.moveWest)
				temp.add(-1.0f, 0.0f);
			if (temp.x != 0.0f && temp.y != 0.0f)
				temp.normalize();
			temp.mul(Player.MAX_SPEED);
			
			this.addTargetVelocity(ua, temp);
			Util.popTemporaryVector2f();
		}
		
		// Update velocity
		super.update(ua);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		updateChildrenInfo();
		if (this.heldItem != null)
			this.heldItem.render(r, map);
		
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		Texture playerTexture = r.getTextureBank().getTexture("player_v1.png");
		r.drawTexture(playerTexture, Align.MM, position.x, position.y, RADIUS * 2, RADIUS * 2, angle);
	}
	
	@Override
	public void handleAction(UpdateArgs ua, Action a) {
		switch (a.getType()) {
			case BEGIN_MOVE_NORTH:
				this.moveNorth = true;
				break;
			case BEGIN_MOVE_SOUTH:
				this.moveSouth = true;
				break;
			case BEGIN_MOVE_EAST:
				this.moveEast = true;
				break;
			case BEGIN_MOVE_WEST:
				this.moveWest = true;
				break;
			case END_MOVE_NORTH:
				this.moveNorth = false;
				break;
			case END_MOVE_SOUTH:
				this.moveSouth = false;
				break;
			case END_MOVE_EAST:
				this.moveEast = false;
				break;
			case END_MOVE_WEST:
				this.moveWest = false;
				break;
			default:
				super.handleAction(ua, a);
		}
	}
}
