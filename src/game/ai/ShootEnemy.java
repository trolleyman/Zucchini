package game.ai;

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
		if (aiPlayer.getHealth() / aiPlayer.getMaxHealth() < 0.95f){
			aiPlayer.getStateMachine().changeState(new Evade());
		}
		if (kill == null) {
			aiPlayer.handleAction(ua.bank, new Action(ActionType.END_USE));
			aiPlayer.getStateMachine().changeState(new MoveTowardsCentre());
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
