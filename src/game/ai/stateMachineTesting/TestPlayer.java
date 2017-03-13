package game.ai.stateMachineTesting;

import game.ai.IStateMachine;
import game.ai.StateMachine;
import game.world.UpdateArgs;

/**
 * A test player ai class that will be used to test the logic of the ai decision making
 * @author Yean
 *
 */
public class TestPlayer {
	public boolean debug = false;
	public IStateMachine<TestPlayer, TestPlayerStates> stateMachine;
	//The following member variables should be used for testing only
	public boolean canSeeEnemy = false;
	public boolean canSeePickUp = false;
	public boolean isShotAt = false;
	
	public boolean isCanSeeEnemy() {
		return canSeeEnemy;
	}


	public void setCanSeeEnemy(boolean canSeeEnemy) {
		this.canSeeEnemy = canSeeEnemy;
	}


	public boolean isCanSeePickUp() {
		return canSeePickUp;
	}


	public void setCanSeePickUp(boolean canSeePickUp) {
		this.canSeePickUp = canSeePickUp;
	}


	public void setShotAt(boolean isShotAt) {
		this.isShotAt = isShotAt;
	}


	public TestPlayer(){
		stateMachine = new StateMachine<TestPlayer, TestPlayerStates>(this,TestPlayerStates.WANDER);
	}
	
	
	public void update(UpdateArgs ua){
		stateMachine.update(ua);
	}
	
	
	public boolean isShotAt(){
		return isShotAt;
	}
	
	public boolean canSeeEnemy(){
		return canSeeEnemy;
	}
	
	public boolean canSeePickup(){
		return canSeePickUp;
	}
	
	public boolean debug(){
		return debug;
	}
	
	public IStateMachine<TestPlayer, TestPlayerStates> getStateMachine(){
		return this.stateMachine;
	}
}
