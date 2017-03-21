package game.ai;

import java.util.Random;

import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.world.UpdateArgs;
import game.world.entity.Entity;

public class ShootEnemy implements State<AIPlayer>{
	private boolean hasBegunUse;
	
	@Override
	public void enter(AIPlayer aiPlayer) {
		if (aiPlayer.debug) System.out.println("AI enters SHOOT_ENEMY state");
		hasBegunUse = false;
	}
	
	@Override
	public void update(AIPlayer aiPlayer, UpdateArgs ua) {
		Entity kill = aiPlayer.getClosestSeenEntity(ua);
		if (aiPlayer.getHealth() / aiPlayer.getMaxHealth() < 0.95f && aiPlayer.getHeldItem().aiValue() > 1){
			aiPlayer.getStateMachine().changeState(new Evade());
		}
		if (kill == null) {
			aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentre());
		} else {
			float angle = Util.getAngle(aiPlayer.position.x, aiPlayer.position.y, kill.position.x, kill.position.y);
			Random randAim = new Random();
			float target = angle + (randAim.nextFloat() - 0.5f) / 5;
			
			float da = target - aiPlayer.angle;
			float newAngle = aiPlayer.angle + (da * (float)ua.dt * 5f);
			
			float diff = Util.getAngleDiff(angle, newAngle);
			
			if (!hasBegunUse && diff < Math.toRadians(35.0)) {
				aiPlayer.handleAction(ua.bank, new Action(ActionType.BEGIN_USE));
				hasBegunUse = true;
			} else if (!aiPlayer.getHeldItem().isUsing()) {
				aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
				hasBegunUse = false;
			}
			
			aiPlayer.handleAction(ua.bank, new AimAction(newAngle)); // aimbot mode
			aiPlayer.setDestination(ua.map.getPathFindingMap(), kill.position);
		}
	}
	
	@Override
	public void exit(AIPlayer aiPlayer) {
		if (aiPlayer.debug) System.out.println("AI exits SHOOT_ENEMY state");
	}

}
