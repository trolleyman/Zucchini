package game.ai;
import java.util.Random;
import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.Player;
import game.world.map.PathFindingMap;
import org.joml.Vector2f;

/**
 * Contains all the possible states and their update methods that our Player ai can be in
 *
 * @author Yean
 */

public enum AIPlayerStates implements State<AIPlayer> {

	/**
	 * Will be the default ai player state, when there is nothing to do
	 * the ai will wander around
	 */
	WANDER() {
		private boolean wandering = false;
		float wanderX;
		float wanderY;
		int counterWandering =0;
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
				boolean notAWallBool = false;
				while(notAWallBool == false){
					Random rand =  new Random();
					wanderX = rand.nextInt(30);
					wanderY= rand.nextInt(30);
					boolean aWallNear = false;
					//for (float x = -1f; x < 1f; x+= 1f){
						//for (float y = -1f; y < 1f; y+=1f){
							if (!pfmap.notAWall(wanderX ,wanderY)){
								aWallNear = true;
								System.out.println("wall near");
							}
						//}
						
					//}
					if (aWallNear == false){
						notAWallBool = true;
						aiPlayer.setDestination(pfmap, new Vector2f(wanderX,wanderY));
						System.out.println("gogogogo");
					}
				}
				wandering = true;
				
			}else{
				aiPlayer.setDestination(pfmap, new Vector2f(wanderX,wanderY));
				float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, wanderX, wanderY);
				aiPlayer.handleAction(ua.bank, new AimAction(angle));
			}
			Node similarWander = pfmap.getClosestNodeTo(aiPlayer.position.x, aiPlayer.position.y);
			if(aiPlayer.debug) System.out.println(Math.round(similarWander.getX() / pfmap.scale) + ", " + wanderX);
			if(aiPlayer.debug) System.out.println(Math.round(similarWander.getY() / pfmap.scale) + ", " + wanderY);
			if (Math.round(similarWander.getX() / pfmap.scale) == Math.round(wanderX) && Math.round(similarWander.getY() / pfmap.scale) == Math.round(wanderY)){
					wandering = false;
					
			}
				
			if (counterWandering > 4){
				aiPlayer.getStateMachine().changeState(MOVE_TOWARDS_CENTRE);
				wandering = false;
				
			}
			if(aiPlayer.getClosestSeenEntity(ua) != null){
				if(aiPlayer.debug2) System.out.println("While wandering, we see an enemy!");
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI exit WANDER state");
		}
	},
	
	/**
	 * Upon zombies being too hard to fight or time conditions,
	 * the ai should move towards the centre of the map
	 */
	MOVE_TOWARDS_CENTRE() {
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
				
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			// TODO: make the ai move towards the middle
			
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI exits MOVE_TOWARDS_CENTRE state");
		}
	},
	
	/**
	 * If the ai is being shot at and cannot see where,
	 * attempt to side step incoming fire and find where it is being shot from
	 */
	EVADE() {
		boolean kiting = false;
		int kitingX ;
		int kitingY;
		int counter = 0;
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI enters EVADE state");
			
		}
		
		@Override
		public void update(AIPlayer aiPlayer, UpdateArgs ua) {
			PathFindingMap pfmap = ua.map.getPathFindingMap();
			Entity kill = aiPlayer.getClosestSeenEntity(ua);
			
			if (kill == null) {
				aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
				aiPlayer.getStateMachine().changeState(MOVE_TOWARDS_CENTRE);
			} else {
				float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, kill.position.x, kill.position.y);
				aiPlayer.handleAction(ua.bank, new Action(ActionType.BEGIN_USE));
				Random randAim = new Random();
				float aim = randAim.nextFloat() / 5;
				
				
					
			
				aiPlayer.handleAction(ua.bank, new AimAction(angle + aim)); // aimbot mode
				
			}
			
			//TODO:try to dodge incoming bullets
			if (!kiting){
				if(aiPlayer.debug) System.out.println("While wandering!!!!!!!!!!!!");
				
				boolean notAWallBool = false;
				while(notAWallBool == false){
					Random rand =  new Random();
					kitingX = rand.nextInt(30);
					kitingY= rand.nextInt(30);
					boolean aWallNear = false;
					for (float x = -1f; x < 1f; x+= 1f){
						for (float y = -1f; y < 1f; y+=1f){
							if (!pfmap.notAWall(kitingX + x,kitingY + y)){
								aWallNear = true;
								System.out.println("wall near");
							}
						}
						
					}
					if (aWallNear == false){
						notAWallBool = true;
						aiPlayer.setDestination(pfmap, new Vector2f(kitingX,kitingY));
						System.out.println("gogogogo");
					}
				}
				kiting = true;
				
			}else{
				aiPlayer.setDestination(pfmap, new Vector2f(kitingX,kitingY));
			}
			Node similarWander = pfmap.getClosestNodeTo(aiPlayer.position.x, aiPlayer.position.y);
			if(aiPlayer.debug) System.out.println(Math.round(similarWander.getX() / pfmap.scale) + ", " + kitingX);
			if(aiPlayer.debug) System.out.println(Math.round(similarWander.getY() / pfmap.scale) + ", " + kitingY);
			if (Math.round(similarWander.getX() / pfmap.scale) == Math.round(kitingX) && Math.round(similarWander.getY() / pfmap.scale) == Math.round(kitingY)){
				kiting = false;
					
			}
			
		
			
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI exits EVADE state");
		}
	},
	
	/**
	 * Upon seeing a higher valued weapon or powerup,
	 * go over to it and pick it up
	 */
	PICKUP() {
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
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			if (aiPlayer.isShotAt()) {
				if (aiPlayer.debug2) System.out.println("Moving Toward pickup, but gets shot from somewhere!");
				aiPlayer.getStateMachine().changeState(EVADE);
				return;
			}
			if (aiPlayer.canSeePickUp()) {
				if (aiPlayer.debug2) System.out.println("Moving Toward pickup");
			} else {
				if (aiPlayer.debug2) System.out.println("Pickup is no longer there");
				aiPlayer.getStateMachine().changeState(WANDER);
				return;
			}
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI exits PICKUP state");
			
		}
	},
	
	/**
	 * Upon seeing an enemy, we should shoot it
	 */
	SHOOT_ENEMY() {
		@Override
		public void enter(AIPlayer aiPlayer) {
			if (aiPlayer.debug) System.out.println("AI enters SHOOT_ENEMY state");
		}
		
		@Override
		public void update(AIPlayer aiPlayer, UpdateArgs ua) {
			Entity kill = aiPlayer.getClosestSeenEntity(ua);
			if (aiPlayer.getHealth() / aiPlayer.getMaxHealth() < 0.7f){
				aiPlayer.getStateMachine().changeState(EVADE);
			}
			if (kill == null) {
				aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
				aiPlayer.getStateMachine().changeState(MOVE_TOWARDS_CENTRE);
			} else {
				float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, kill.position.x, kill.position.y);
				aiPlayer.handleAction(ua.bank, new Action(ActionType.BEGIN_USE));
				aiPlayer.handleAction(ua.bank, new AimAction(angle)); // aimbot mode
				aiPlayer.setDestination(ua.map.getPathFindingMap(), kill.position);
			}
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			if (aiPlayer.debug) System.out.println("AI exits SHOOT_ENEMY state");
		}
	}
}
