package game.ai;
/**
 * Contains all the possible states and their update methods that our ai can be in
 * @author Yean
 */
public enum AIPlayerStates implements State<AIPlayer>{
	/**
	 * Will be the default ai player state, when there is nothing to do
	 * the ai will wander around
	 */
	WANDER() {
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI has moved to wander state");
		}
		
		@Override
		public void update(AIPlayer aiPlayer){
			//TODO:do wandering things
			//top priority for all states will be if this entity can see an enemy, then switch states
			if(aiPlayer.canSeeEnemy()){
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
			}
			
			//wander
			
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			
		}
	},
	
	/**
	 * Upon zombies being too hard to fight or time conditions,
	 * the ai should move towards the centre of the map
	 */
	MOVE_TOWARDS_CENTRE(){
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI is looking to move more central");

		}
		
		@Override
		public void update(AIPlayer aiPlayer){
			//TODO:make the ai move towards the middle
			if(aiPlayer.canSeeEnemy()){
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
			}
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			
		}
	},
	
	/**
	 * If the ai is being shot at and cannot see where, 
	 * attempt to side step incoming fire and find where it is being shot from
	 */
	EVADE(){
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI is being shot at!");
		}
		
		@Override
		public void update(AIPlayer aiPlayer){
			//TODO:try to dodge incoming bullets
			if(aiPlayer.canSeeEnemy()){
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
			}
			
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			
		}
	},
	
	/**
	 * Upon seeing a higher valued weapon or powerup,
	 * go over to it and pick it up
	 */
	PICKUP(){
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI can see cool pickup and is going for it!");

		}
		
		@Override
		public void update(AIPlayer aiPlayer){
			//TODO:go towards pickup and take it
			if(aiPlayer.canSeeEnemy()){
				aiPlayer.getStateMachine().changeState(SHOOT_ENEMY);
			}
			
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			
		}
	},
	
	/**
	 * Upon seeing an enemy, we should shoot it
	 */
	SHOOT_ENEMY(){
		@Override
		public void enter(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			if(aiPlayer.debug()) System.out.println("AI has spotted an enemy and is engaging!");

		}
		
		@Override
		public void update(AIPlayer aiPlayer){
			//TODO:shoot at enemy
		}
		
		@Override
		public void exit(AIPlayer aiPlayer) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	

	

}
