package game.ai;

import game.action.Action;
import game.action.ActionType;
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
		
		if (closestWeapon != null){
			aiPlayer.setDestination(ua.map.getPathFindingMap(), closestWeapon.position);
			if (aiPlayer.position.distanceSquared(closestWeapon.position) < 0.2f) {
				aiPlayer.handleAction(ua, new Action(ActionType.PICKUP));
				aiPlayer.getStateMachine().changeState(new Wander());
			}
		}else{
			aiPlayer.getStateMachine().changeState(new Wander());
			
		}
		
		
		
		
		if (aiPlayer.getClosestSeenEntity(ua) != null) {
			if (aiPlayer.debug2) System.out.println("Moving Toward pickup, but encounters enemy!");
			aiPlayer.getStateMachine().changeState(new ShootEnemy());
			return;
		}
		
		
		
		
		
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits PICKUP state");
		
	}
}
