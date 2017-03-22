package game.ai.state;

import game.action.Action;
import game.action.ActionType;
import game.ai.AIPlayer;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.entity.Entity;

public class PickupState implements State<AIPlayer> {
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI enters PICKUP state");
		
	}
	
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
		
		//if Enemy is near and weapon useful -> DIE DIE DIE
//		if (aiPlayer.getClosestSeenEntity(ua) != null && !aiPlayer.getHeldItem().isUseless()) {
//			if (aiPlayer.debug2) System.out.println("Moving Toward pickup, but encounters enemy!");
//			aiPlayer.getStateMachine().changeState(new ShootEnemyState());
//			return;
//		}
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits PICKUP state");
		
	}
}
