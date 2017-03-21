package game.ai;

import org.joml.Vector2f;

import game.Util;
import game.action.AimAction;
import game.world.UpdateArgs;
import game.world.map.PathFindingMap;

public class MoveTowardsCentre implements State<AIPlayer> {
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		
		aiPlayer.setDestination(pfmap, new Vector2f(15,15));
		float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, 15, 15);
		aiPlayer.handleAction(ua.bank, new AimAction(angle));
		if (aiPlayer.getClosestSeenEntity(ua) != null) {
			if (aiPlayer.debug2) System.out.println("While wandering, we see an enemy!");
			
			aiPlayer.getStateMachine().changeState(new ShootEnemy());
			return;
		}
		// TODO: make the ai move towards the middle
		
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits MOVE_TOWARDS_CENTRE state");
	}
}
