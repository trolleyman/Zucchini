package game.ai.state;

import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.ai.AIPlayer;
import game.ai.State;
import game.world.UpdateArgs;
import game.world.entity.Entity;

import java.util.Random;

public class ShootEnemyState implements State<AIPlayer> {
	private boolean hasBegunUse;
	
	@Override
	public void enter(AIPlayer aiPlayer) {
		if (aiPlayer.debug) System.out.println("AI enters SHOOT_ENEMY state");
		hasBegunUse = false;
	}
	
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		Entity kill = aiPlayer.getClosestSeenEntity(ua);
		if (aiPlayer.getHealth() / aiPlayer.getMaxHealth() < 0.95f && aiPlayer.getHeldItem().aiValue() > 1) {
			aiPlayer.getStateMachine().changeState(new EvadeState());
		}
		if (kill == null) {
			aiPlayer.handleAction(ua, new Action(ActionType.END_USE));
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentreState());
		} else {
			float desiredAngle = aiPlayer.getFiringAngle(kill.position.x, kill.position.y);
			float newAngle = aiPlayer.getNewAngle(desiredAngle, ua.dt);
			aiPlayer.handleAction(ua, new AimAction(newAngle));
			
			float diff = Math.abs(Util.getAngleDiff(desiredAngle, newAngle));
			if (!hasBegunUse && diff < Math.toRadians(35.0)) {
				aiPlayer.handleAction(ua, new Action(ActionType.BEGIN_USE));
				hasBegunUse = true;
			} else if (!aiPlayer.getHeldItem().isUsing()) {
				aiPlayer.handleAction(ua, new Action(ActionType.END_USE));
				hasBegunUse = false;
			}
			aiPlayer.setDestination(ua.map.getPathFindingMap(), kill.position);
		}
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		if (aiPlayer.debug) System.out.println("AI exits SHOOT_ENEMY state");
	}
	
}
