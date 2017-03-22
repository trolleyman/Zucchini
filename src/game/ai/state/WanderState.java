package game.ai.state;

import game.Util;
import game.action.AimAction;
import game.ai.AIPlayer;
import game.ai.Node;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.map.PathFindingMap;
import org.joml.Vector2f;

import java.util.Random;

public class WanderState implements State<AIPlayer> {
	private boolean wandering = false;
	int wanderX;
	float wanderY;
	int counterWandering = 0;
	Vector2f[] places = {new Vector2f(1.0f, 1.0f), new Vector2f(1.0f, 29.0f), new Vector2f(29.0f, 29.0f), new Vector2f(29.0f, 1.0f)};
	
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI enters WANDER state");
		counterWandering = 0;
	}
	
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		
		//if at location at the end of wandering -> find new destination to wander to
		if (!wandering) {
			if (aiPlayer.debug) System.out.println("While wandering!!!!!!!!!!!!");
			counterWandering++;
			//	boolean notAWallBool = false;
			Random rand = new Random();
			
			wanderX = rand.nextInt(3);
			aiPlayer.setDestination(pfmap, places[wanderX]);

			wandering = true;
			
		} else {
			//else continue to wander
			aiPlayer.setDestination(pfmap, places[wanderX]);
			
		}
		//check if at final location
		Node similarWander = pfmap.getClosestNodeTo(aiPlayer.position.x, aiPlayer.position.y);
		if (aiPlayer.debug) System.out.println(Math.round(similarWander.getX() / pfmap.scale) + ", " + wanderX);
		if (aiPlayer.debug) System.out.println(Math.round(similarWander.getY() / pfmap.scale) + ", " + wanderX);
		if (Math.round(similarWander.getX() / pfmap.scale) == Math.round(places[wanderX].x) && Math.round(similarWander.getY() / pfmap.scale) == Math.round(places[wanderX].y)) {
			wandering = false;
			
		}
		//if close pickup is worth picking up -> pickup
		if (aiPlayer.getClosestValublePickup(ua) != null) {
			aiPlayer.getStateMachine().changeState(new PickupState());
		}
		//have i had enough wandering -> move towards centre
		if (counterWandering > 1) {
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentreState());
			wandering = false;
			counterWandering = 0;
			
		}
		
		//if enemy near AND helditem is not useless -> DIE DIE DIE
		if (aiPlayer.getClosestSeenEntity(ua) != null && !aiPlayer.getHeldItem().isUseless()) {
			if (aiPlayer.debug2) System.out.println("While wandering, we see an enemy!");
			aiPlayer.getStateMachine().changeState(new ShootEnemyState());
			return;
		}
		
		
		
		
		float angle = (float) Util.getAngle(aiPlayer.velocity.x, aiPlayer.velocity.y);
		aiPlayer.handleAction(ua, new AimAction(angle));
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exit WANDER state");
	}
}

