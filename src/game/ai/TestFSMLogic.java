package game.ai;

import java.util.HashMap;
import game.world.UpdateArgs;
import junit.*;

/**
 * Tests the logic of the state machine by role playing what events that our ai will go through
 * @author George, Yean
 */
public class TestFSMLogic {
	public final static int refreshRate = 1; //artificial update rate per second
	public static UpdateArgs ua = new UpdateArgs(refreshRate, null, null, null, null, null);
	public static TestPlayer ai = new TestPlayer();
	public enum eventType{SEE_ENEMY, ENEMY_GONE, SEE_PICKUP, PICKUP_GONE, SHOT_AT, NOT_SHOT_AT};
	public static HashMap<Integer,eventType> events = new HashMap<Integer,eventType>();

	public  void main() throws InterruptedException{		
		
		int currentRate=0;
		
		//craft events and place them into hash map to test our ai decision making
		events.put(2, eventType.SEE_ENEMY);
		events.put(5, eventType.ENEMY_GONE);
		events.put(8, eventType.SEE_PICKUP); //sees pickup
		events.put(11, eventType.SEE_ENEMY); //but then we are interrupted...
		events.put(13, eventType.PICKUP_GONE); // during this fight the pickup is gone
		events.put(15, eventType.ENEMY_GONE); 
		events.put(17, eventType.SHOT_AT);
		events.put(19, eventType.SEE_ENEMY); 
		events.put(20, eventType.SEE_PICKUP); //sees enemy and pickup at about the same time
		events.put(22, eventType.NOT_SHOT_AT);
		events.put(25, eventType.ENEMY_GONE); //the pickup is still available after the enemy is gone
		events.put(27, eventType.PICKUP_GONE); //finally secure that pickup after the fight
		
		while(true){
			if(events.containsKey(currentRate)){
				handleEvent(events.get(currentRate));
			}
			
			ai.update(ua);
			
			Thread.sleep(1000/refreshRate);
			currentRate++;
		}
		
		
	}
	
	private static void handleEvent(eventType eventType) {
		switch(eventType){
		case SEE_ENEMY : ai.setCanSeeEnemy(true); break;
		case ENEMY_GONE : ai.setCanSeeEnemy(false);break;
		case SEE_PICKUP : ai.setCanSeePickUp(true);break;
		case PICKUP_GONE : ai.setCanSeePickUp(false);break;
		case SHOT_AT : ai.setShotAt(true);break;
		case NOT_SHOT_AT : ai.setShotAt(false);break;
		}
		
	}
}
