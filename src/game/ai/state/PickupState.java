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
		//TODO:go towards pickup and take it
		Entity closestWeapon = aiPlayer.getClosestValublePickup(ua);
		
		if (closestWeapon != null) {
			aiPlayer.setDestination(ua.map.getPathFindingMap(), closestWeapon.position);
			if (aiPlayer.position.distanceSquared(closestWeapon.position) < 0.2f) {
				aiPlayer.handleAction(ua, new Action(ActionType.PICKUP));
				aiPlayer.getStateMachine().changeState(new WanderState());
			}
		} else {
			aiPlayer.getStateMachine().changeState(new WanderState());
			
		}
		
		if (aiPlayer.getClosestSeenEntity(ua) != null) {
			if (aiPlayer.debug2) System.out.println("Moving Toward pickup, but encounters enemy!");
			aiPlayer.getStateMachine().changeState(new ShootEnemyState());
			return;
		}
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits PICKUP state");
		
	}
}
