package game.net;

import game.action.Action;
import game.action.ActionType;
import game.world.entity.Player;

public class DummyConnection implements IClientConnection {
	private Player player;
	
	public DummyConnection(Player _player) {
		this.player = _player;
	}

	@Override
	public void sendAction(Action a) {
		if (a.getType() != ActionType.AIM)
			System.out.println("Action recieved: " + a.toString());
		
		this.player.handleAction(a);
	}
}
