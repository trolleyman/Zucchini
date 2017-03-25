package game.ai.state;

import game.action.Action;
import game.action.ActionType;
import game.ai.AIPlayer;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.entity.Entity;
/**
 * the pick up state of the statemachine
 * @author George Alexander
 *
 */
public class PickupState implements State<AIPlayer> {
	/**
	 * when entering the state this gets called
	 * @param aiPlayer the ai player/owner of the statemachine
	 */
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI enters PICKUP state");
		
	}
	/**
	 * when statemachine is updated and this is the current state this method is called
	 * @param aiPlayer the owner of the aiplayer
	 * @param ua the update args
	 */
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		//finds closest weapon which is worth picking up
		Entity closestWeapon = aiPlayer.getClosestValublePickup(ua);
		//if closest weapon exists -> go to pick up
		if (closestWeapon != null) {
			aiPlayer.setDestination(ua.map.getPathFindingMap(), closestWeapon.position);
			//if close to weapon -> pick up and start wandering
			if (aiPlayer.position.distanceSquared(closestWeapon.position) < 0.4f) {
				aiPlayer.handleAction(ua, new Action(ActionType.PICKUP));
				aiPlayer.getStateMachine().changeState(new WanderState());
			}
		} else {
			//if weapon is not around -> wander
			aiPlayer.getStateMachine().changeState(new WanderState());
		}

	}
	/** 
	 * method called when exiting the state
	 * @param the owner of the state machine
	 */
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits PICKUP state");
		
	}
}
