package game.ai;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.AutonomousPlayerEntity;
import game.world.entity.Item;
import game.world.entity.MovableEntity;

/**
 * Represents an AI player, uses a FSM to determine it's actions
 * @author Yean
 *
 */
public class AIPlayer extends AutonomousPlayerEntity{
	public boolean debug = true;
	public IStateMachine<AIPlayer, AIPlayerStates> stateMachine;
	public Item heldItem;

	/**
	 * Contructs an AIPlayer at position with an Item
	 * @param team
	 * @param position
	 * @param heldItem
	 */
	public AIPlayer(int team, Vector2f position, Item heldItem) {
		super(team, position,heldItem);
		this.heldItem = heldItem;
		if (this.heldItem != null)
			this.heldItem.setOwnerTeam(this.getTeam());
	}
	
	public AIPlayer(AutonomousPlayerEntity ape) {
		super(ape);
	}
	
	public StateMachine<AIPlayer, AIPlayerStates> getStateMachine(){
		return (StateMachine<AIPlayer, AIPlayerStates>) stateMachine;
	}
	
	public void update(UpdateArgs ua){
		super.update(ua);
		//update stateMachine
		stateMachine.update();
		
	}
	
	public boolean canSeeEnemy(){
		//TODO: make function that tells us if this entity can see an enemy
		return false;
	}
	
	public boolean canSeePickUp(){
		//TODO: make function that can tell us if this entity can see a pickup
		//and also if it's worth picking up
		return false;
	}


	public boolean debug() {
		return debug;
	}

	@Override
	public MovableEntity clone() {
		return new AIPlayer(this);
	}

	@Override
	public void render(IRenderer r) {
		float x = position.x + 0.25f * (float) Math.sin(angle);
		float y = position.y + 0.25f * (float) Math.cos(angle);
		
		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		r.drawCircle(position.x, position.y, 0.15F, ColorUtil.BLUE);		
	}
}
