package game.ai.state;

import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.ai.AIPlayer;
import game.ai.Node;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.Player;
import game.world.map.PathFindingMap;
import org.joml.Vector2f;

import java.util.Random;

public class EvadeState implements State<AIPlayer> {
	boolean kiting = false;
	int kitingX;
	int kitingY;
	int counter = 0;
	private boolean hasBegunUse;
	
	@Override
	public void enter(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI enters EVADE state");
		hasBegunUse = false;
	}
	
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		Entity kill = aiPlayer.getClosestSeenEntity(ua);
		
		if (kill == null) {
			aiPlayer.handleAction(ua, new Action(ActionType.END_USE));
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentreState());
		} else {
			float desiredAngle = aiPlayer.getFiringAngle(kill.position.x, kill.position.y);
			float newAngle = aiPlayer.getNewAngle(desiredAngle, ua.dt);
			aiPlayer.handleAction(ua, new AimAction(newAngle));
			
			float diff = Util.getAngleDiff(desiredAngle, newAngle);
			if (!hasBegunUse && diff < Math.toRadians(35.0)) {
				aiPlayer.handleAction(ua, new Action(ActionType.BEGIN_USE));
				hasBegunUse = true;
			} else if (!aiPlayer.getHeldItem().isUsing()) {
				aiPlayer.handleAction(ua, new Action(ActionType.END_USE));
				hasBegunUse = false;
			}
		}
		
		//TODO:try to dodge incoming bullets
		
		if (!kiting) {
			if (aiPlayer.debug) System.out.println("While wandering!!!!!!!!!!!!");
			
			boolean notAWallBool = false;
			while (notAWallBool == false) {
				Random rand = new Random();
				kitingX = rand.nextInt(30);
				kitingY = rand.nextInt(30);
				boolean aWallNear = false;
				for (float x = -1f; x < 1f; x += 1f) {
					for (float y = -1f; y < 1f; y += 1f) {
						if (!pfmap.notAWall(kitingX + x, kitingY + y)) {
							aWallNear = true;
							//System.out.println("wall near");
						}
					}
				}
				if (aWallNear == false) {
					notAWallBool = true;
					aiPlayer.setDestination(pfmap, new Vector2f(kitingX, kitingY));
					//System.out.println("gogogogo");
				}
			}
			kiting = true;
			
		} else {
			aiPlayer.setDestination(pfmap, new Vector2f(kitingX, kitingY));
		}
		Node similarWander = pfmap.getClosestNodeTo(aiPlayer.position.x, aiPlayer.position.y);
		//if(aiPlayer.debug) System.out.println(Math.round(similarWander.getX() / pfmap.scale) + ", " + kitingX);
		//if(aiPlayer.debug) System.out.println(Math.round(similarWander.getY() / pfmap.scale) + ", " + kitingY);
		if (Math.round(similarWander.getX() / pfmap.scale) == Math.round(kitingX) && Math.round(similarWander.getY() / pfmap.scale) == Math.round(kitingY)) {
			kiting = false;
		}
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		// TODO Auto-generated method stub
		if (aiPlayer.debug) System.out.println("AI exits EVADE state");
	}
}
