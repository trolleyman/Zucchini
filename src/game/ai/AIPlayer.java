package game.ai;

import org.joml.Vector2f;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.AutonomousPlayerEntity;
import game.world.entity.Item;
import game.world.entity.MovableEntity;

/**
 * Represents an AI player, uses a FSM to determine it's actions
 * @author Yean
 */
public class AIPlayer extends AutonomousPlayerEntity{
	public boolean debug = true;
	public transient IStateMachine<AIPlayer, AIPlayerStates> stateMachine;
	public Item heldItem;

	
	public AIPlayer(AIPlayer ai) {
		super(ai);
		this.debug = ai.debug;
		this.stateMachine = ai.stateMachine;
		this.heldItem = ai.heldItem;
	}
	
	/**
	 * Contructs an AIPlayer at position with an Item
	 * @param team
	 * @param position
	 * @param heldItem
	 */
	public AIPlayer(int team, Vector2f position, Item heldItem) {
		super(team, position);
		this.heldItem = heldItem;
		if (this.heldItem != null)
			this.heldItem.setOwnerTeam(this.getTeam());
		updateHeldItemInfo();
		stateMachine = new StateMachine<AIPlayer, AIPlayerStates>(this, AIPlayerStates.WANDER);
	}
	
	private void updateHeldItemInfo() {
		if (this.heldItem != null) {
			this.heldItem.setOwner(this.getId());
			this.heldItem.setOwnerTeam(this.getTeam());
			this.heldItem.angle = this.angle;
			
			// Calculate position
			Vector2f offset = Util.pushTemporaryVector2f();
			offset.set(Util.getDirX(angle+(float)Math.PI/2), Util.getDirY(angle+(float)Math.PI/2)).mul(0.15f);
			this.heldItem.position.set(this.position).add(offset);
			Util.popTemporaryVector2f();
		}
	}
	
	
	
	public IStateMachine<AIPlayer, AIPlayerStates> getStateMachine(){
		return stateMachine;
	}
	
	@Override
	public void update(UpdateArgs ua){
		super.update(ua);
		//update stateMachine
		stateMachine.update(ua);
		
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
