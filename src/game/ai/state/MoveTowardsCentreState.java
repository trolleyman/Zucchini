package game.ai.state;

import game.Util;
import game.action.AimAction;
import game.ai.AIPlayer;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.map.PathFindingMap;
import org.joml.Vector2f;
/**
 * the move towards state of the statemachine
 * @author George Alexander
 *
 */
public class MoveTowardsCentreState implements State<AIPlayer> {
	/**
	 * when entering the state this gets called
	 * @param aiPlayer the ai player/owner of the statemachine
	 */
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
	}
	/**
	 * when statemachine is updated and this is the current state this method is called
	 * @param aiPlayer the owner of the aiplayer
	 * @param ua the update args
	 */
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();

	
		//if enemy nearby and weapon is not useless -> die die die
		if (aiPlayer.getClosestSeenEntity(ua) != null && !aiPlayer.getHeldItem().isUseless()) {
			if (aiPlayer.debug2) System.out.println("While wandering, we see an enemy!");
			
			aiPlayer.getStateMachine().changeState(new ShootEnemyState());
			return;
		}
		//if item is useless -> wander to find pickup
		if (aiPlayer.getHeldItem().isUseless()){
			aiPlayer.getStateMachine().changeState(new WanderState());
		}
		//if close pickup is worth picking up -> pickup
		if (aiPlayer.getClosestValublePickup(ua) != null) {
			aiPlayer.getStateMachine().changeState(new PickupState());
		}
		//move to centre
		aiPlayer.setDestination(pfmap, new Vector2f(15, 15));
		
		float angle = (float) Util.getAngle(aiPlayer.velocity.x, aiPlayer.velocity.y);
		aiPlayer.handleAction(ua, new AimAction(angle));
		
		
	}
	/** 
	 * method called when exiting the state
	 * @param the owner of the state machine
	 */
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits MOVE_TOWARDS_CENTRE state");
	}
}
