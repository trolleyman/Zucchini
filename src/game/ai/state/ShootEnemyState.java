package game.ai.state;

import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.ai.AIPlayer;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.entity.Entity;

import java.util.Random;
/**
 * the shoot enemy state of the statemachine
 * @author George Alexander
 *
 */
public class ShootEnemyState implements State<AIPlayer> {
	private boolean hasBegunUse;
	/**
	 * when entering the state this gets called
	 * @param aiPlayer the ai player/owner of the statemachine
	 */
	@Override
	public void enter(AIPlayer aiPlayer) {
		if (aiPlayer.debug) System.out.println("AI enters SHOOT_ENEMY state");
		hasBegunUse = false;
	}
	/**
	 * when statemachine is updated and this is the current state this method is called
	 * @param aiPlayer the owner of the aiplayer
	 * @param ua the update args
	 */
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		Entity kill = aiPlayer.getClosestSeenEntity(ua);
	
		//if close pickup is worth picking up -> pickup
		if (aiPlayer.getClosestValublePickup(ua) != null) {
			aiPlayer.getStateMachine().changeState(new PickupState());
		}
		//if weapon is useless -> wander to find a weapon!
		if (aiPlayer.getHeldItem().isUseless()){
			aiPlayer.getStateMachine().changeState(new WanderState());	
		}
		//if health is getting low and item is not melee -> evade
		if (aiPlayer.getHealth() / aiPlayer.getMaxHealth() < 0.95f && aiPlayer.getHeldItem().aiValue() > 1) {
			aiPlayer.getStateMachine().changeState(new EvadeState());
		}
		//if no enemies in sight -> go towards center
		if (kill == null) {
			aiPlayer.handleAction(ua, new Action(ActionType.END_USE));
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentreState());
		} else {
			//if enemies in sight -> DIE DIE DIE
			float desiredAngle = aiPlayer.getFiringAngle(kill.position.x, kill.position.y);
			float newAngle = aiPlayer.getNewAngle(desiredAngle, ua.dt);
			aiPlayer.handleAction(ua, new AimAction(newAngle));
			
			float diff = Math.abs(Util.getAngleDiff(desiredAngle, newAngle));
			if (!hasBegunUse && diff < Math.toRadians(35.0)) {
				aiPlayer.handleAction(ua, new Action(ActionType.BEGIN_USE));
				hasBegunUse = true;
			} else if (!aiPlayer.getHeldItem().isUsing()) {
				aiPlayer.handleAction(ua, new Action(ActionType.END_USE));
				hasBegunUse = false;
			}
			aiPlayer.setDestination(ua.map.getPathFindingMap(), kill.position);
		}
		
	}
	/** 
	 * method called when exiting the state
	 * @param the owner of the state machine
	 */
	@Override
	public void exit(AIPlayer aiPlayer) {
		if (aiPlayer.debug) System.out.println("AI exits SHOOT_ENEMY state");
	}
	
}
