package game.ai;

import org.joml.Vector2f;

import game.world.UpdateArgs;
import game.world.entity.Item;
import game.world.entity.Player;

/**
 * Represents an AI player, uses a FSM to determine it's actions
 * @author Yean
 *
 */
public class AIPlayer extends Player{
	public boolean debug = true;
	public IStateMachine<AIPlayer, AIPlayerStates> stateMachine;

	public AIPlayer(int team, Vector2f position, Item _heldItem) {
		super(team, position, _heldItem);
		stateMachine = new StateMachine<AIPlayer,AIPlayerStates>(this,AIPlayerStates.WANDER);
	}

	
	public void update(UpdateArgs ua){
		super.update(ua);
		
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
}
