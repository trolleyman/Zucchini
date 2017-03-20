package game.ai;

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
		@Override
		public void enter(AIPlayer aiPlayer) {
			
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI enters WANDER state");
		}
		
		@Override
		public void update(AIPlayer aiPlayer, UpdateArgs ua) {
			//TODO:do wandering things
			//top priority for all states will be if this entity can see an enemy, then switch states
			
			if (aiPlayer.getClosestSeenEntity(ua) != null) {
				if (aiPlayer.debug2) System.out.println("While wandering, we see an enemy!");
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			
			//wander
			if (aiPlayer.debug2) System.out.println("AI is wandering");
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
			
			aiPlayer.setDestination(pfmap, new Vector2f(15, 15));
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
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if (aiPlayer.debug) System.out.println("AI enters EVADE state");
		}
		
		@Override
		public void update(AIPlayer aiPlayer, UpdateArgs ua) {
			//TODO:try to dodge incoming bullets
			if (aiPlayer.getClosestSeenEntity(ua) != null) {
				if (aiPlayer.debug2) System.out.println("While evading, we spot the enemy!");
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;			}
			if (aiPlayer.isShotAt()) {
				//do evade stuff and try to find enemy
				if (aiPlayer.debug2) System.out.println("Still evading! Trying to locate enemy...");
				return;
			} else {
				if (aiPlayer.debug2) System.out.println("While evading, we spot the enemy!");
				aiPlayer.getStateMachine().changeState(WANDER);
				return;
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
		private boolean hasBegunUse;
		
		@Override
		public void enter(AIPlayer aiPlayer) {
			if (aiPlayer.debug) System.out.println("AI enters SHOOT_ENEMY state");
			hasBegunUse = false;
		}
		
		@Override
		public void update(AIPlayer aiPlayer, UpdateArgs ua) {
			Entity kill = aiPlayer.getClosestSeenEntity(ua);
			
			if (kill == null) {
				aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
				aiPlayer.getStateMachine().changeState(MOVE_TOWARDS_CENTRE);
			} else {
				if (!hasBegunUse) {
					aiPlayer.handleAction(ua.bank, new Action(ActionType.BEGIN_USE));
					hasBegunUse = true;
				} else if (!aiPlayer.getHeldItem().isUsing()) {
					aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
					hasBegunUse = false;
				}
				
				float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, kill.position.x, kill.position.y);
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
