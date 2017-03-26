package game.stateMachineTesting;

import game.ai.State;
import game.world.UpdateArgs;

/**
 * Contains all the possible states and their update methods that our test ai can be in
 * @author George, Yean
 */
public enum TestPlayerStates implements State<TestPlayer>{
	/**
	 * Will be the default ai player state, when there is nothing to do
	 * the ai will wander around
	 */
	WANDER() {
		@Override
		public void enter(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI enters WANDER state");
		}
		
		@Override
		public void update(TestPlayer aiPlayer,UpdateArgs ua){
			//TODO:do wandering things
			//top priority for all states will be if this entity can see an enemy, then switch states
			if(aiPlayer.canSeeEnemy()){
				System.out.println("While wandering, we see an enemy!");
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			if(aiPlayer.isShotAt()){
				System.out.println("While wandering, we are getting shot at!");
				aiPlayer.getStateMachine().changeState(EVADE);
				return;
			}
			if(aiPlayer.canSeePickup()){
				System.out.println("While wandering, we see a valuable pickup!");
				aiPlayer.getStateMachine().changeState(PICKUP);
				return;
			}
			
			//wander
			System.out.println("AI is wandering");
		}
		
		@Override
		public void exit(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI exit WANDER state");
		}
	},
	
	/**
	 * Upon zombies being too hard to fight or time conditions,
	 * the ai should move towards the centre of the map
	 */
	MOVE_TOWARDS_CENTRE(){
		@Override
		public void enter(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI enters MOVE_TOWARDS_CENTRE state");

		}
		
		@Override
		public void update(TestPlayer aiPlayer, UpdateArgs ua){
			//TODO:make the ai move towards the middle
			if(aiPlayer.canSeeEnemy()){
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}

		}
		
		@Override
		public void exit(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI exits MOVE_TOWARDS_CENTRE state");

		}
	},
	
	/**
	 * If the ai is being shot at and cannot see where, 
	 * attempt to side step incoming fire and find where it is being shot from
	 */
	EVADE(){
		@Override
		public void enter(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI enters EVADE state");
		}
		
		@Override
		public void update(TestPlayer aiPlayer, UpdateArgs ua){
			//TODO:try to dodge incoming bullets
			if(aiPlayer.canSeeEnemy()){
				System.out.println("While evading, we spot the enemy!");
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			if(aiPlayer.isShotAt()){
				//do evade stuff and try to find enemy
				System.out.println("Still evading! Trying to locate enemy...");
				return;
			}
			else{
				System.out.println("While evading, we spot the enemy!");
				aiPlayer.getStateMachine().changeState(WANDER);
				return;
			}
			
		}
		
		@Override
		public void exit(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI exits EVADE state");
		}
	},
	
	/**
	 * Upon seeing a higher valued weapon or powerup,
	 * go over to it and pick it up
	 */
	PICKUP(){
		@Override
		public void enter(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI enters PICKUP state");

		}
		
		@Override
		public void update(TestPlayer aiPlayer, UpdateArgs ua){
			//TODO:go towards pickup and take it
			if(aiPlayer.canSeeEnemy()){
				System.out.println("Moving Toward pickup, but encounters enemy!");
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
				return;
			}
			if(aiPlayer.isShotAt()){
				System.out.println("Moving Toward pickup, but gets shot from somewhere!");
				aiPlayer.getStateMachine().changeState(EVADE);
				return;
			}
			if(aiPlayer.canSeePickup()){
				System.out.println("Moving Toward pickup");
			}
			else{
				System.out.println("Pickup is no longer there");
				aiPlayer.getStateMachine().changeState(WANDER);
				return;
			}
		}
		
		@Override
		public void exit(TestPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI exits PICKUP state");

		}
	},
	
	/**
	 * Upon seeing an enemy, we should shoot it
	 */
	SHOOT_ENEMY(){
		@Override
		public void enter(TestPlayer aiPlayer) {
			if(aiPlayer.debug()) System.out.println("AI enters SHOOT_ENEMY state");

		}
		
		@Override
		public void update(TestPlayer aiPlayer, UpdateArgs ua) {
			if(aiPlayer.canSeeEnemy()){
				System.out.println("Shooting at enemy!");
				return;
			}
			else{
				System.out.println("Enemy is no longer there");
				aiPlayer.getStateMachine().changeState(WANDER);
				return;
			}
		}
		
		@Override
		public void exit(TestPlayer aiPlayer) {
			if(aiPlayer.debug()) System.out.println("AI exits SHOOT_ENEMY state");
		}
	},
}
