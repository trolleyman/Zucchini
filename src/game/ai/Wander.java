package game.ai;

import java.util.Random;

import org.joml.Vector2f;

import com.sun.org.apache.bcel.internal.generic.MONITORENTER;

import game.Util;
import game.action.AimAction;
import game.world.UpdateArgs;
import game.world.map.PathFindingMap;

public class Wander implements State<AIPlayer> {
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
		
		if (!wandering){
			if(aiPlayer.debug) System.out.println("While wandering!!!!!!!!!!!!");
			counterWandering ++;
		//	boolean notAWallBool = false;
			Random rand = new Random();
			
			wanderX = rand.nextInt(3);
			aiPlayer.setDestination(pfmap, places[wanderX]);
			
//			while(notAWallBool == false){
//				Random rand =  new Random();
//				wanderX = rand.nextInt(30);
//				wanderY= rand.nextInt(30);
//				boolean aWallNear = false;
//				for (float x = -1f; x < 1f; x+= 1f){
//					for (float y = -1f; y < 1f; y+=1f){
//						if (!pfmap.notAWall(wanderX ,wanderY)){
//							aWallNear = true;
//							System.out.println("wall near");
//						}
//					}
//					
//				}
//				if (aWallNear == false){
//					notAWallBool = true;
//					aiPlayer.setDestination(pfmap, new Vector2f(wanderX,wanderY));
//					System.out.println("gogogogo");
//				}
//			}
			wandering = true;
			
		}else{
			aiPlayer.setDestination(pfmap, places[wanderX]);
			//float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, wanderX, wanderY);
			//aiPlayer.handleAction(ua.bank, new AimAction(angle));
		}
		Node similarWander = pfmap.getClosestNodeTo(aiPlayer.position.x, aiPlayer.position.y);
		if(aiPlayer.debug) System.out.println(Math.round(similarWander.getX() / pfmap.scale) + ", " + wanderX);
		if(aiPlayer.debug) System.out.println(Math.round(similarWander.getY() / pfmap.scale) + ", " + wanderX);
		if (Math.round(similarWander.getX() / pfmap.scale) == Math.round(places[wanderX].x) && Math.round(similarWander.getY() / pfmap.scale) == Math.round(places[wanderX].y)){
				wandering = false;
				
		}
		if (aiPlayer.getClosestValublePickup(ua) != null){
			aiPlayer.getStateMachine().changeState(new PickupState());
		}
		if (counterWandering > 4){
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentre());
			wandering = false;
			
		}
		if(aiPlayer.getClosestSeenEntity(ua) != null){
			if(aiPlayer.debug2) System.out.println("While wandering, we see an enemy!");
			aiPlayer.getStateMachine().changeState(new ShootEnemy());
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

