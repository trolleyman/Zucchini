package game.ai;

import game.world.UpdateArgs;

public class Pickup implements State<AIPlayer> {
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI enters PICKUP state");
		
	}
	
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		//TODO:go towards pickup and take it
		if (aiPlayer.getClosestSeenEntity(ua) != null) {
			if (aiPlayer.debug2) System.out.println("Moving Toward pickup, but encounters enemy!");
			aiPlayer.getStateMachine().changeState(new ShootEnemy());
			return;
		}
		if (aiPlayer.isShotAt()) {
			if (aiPlayer.debug2) System.out.println("Moving Toward pickup, but gets shot from somewhere!");
			aiPlayer.getStateMachine().changeState(new Evade());
			return;
		}
		if (aiPlayer.canSeePickUp()) {
			if (aiPlayer.debug2) System.out.println("Moving Toward pickup");
		} else {
			if (aiPlayer.debug2) System.out.println("Pickup is no longer there");
			aiPlayer.getStateMachine().changeState(new Wander());
			return;
		}
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits PICKUP state");
		
	}
}
