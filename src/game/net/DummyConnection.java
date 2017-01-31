package game.net;

import game.action.Action;
import game.action.ActionType;
import game.world.entity.Player;

/**
 * A dummy class to implement {@link IClientConnection} so that we can play the game and prototype aspects of it
 * without having to implement networking atm.
 * 
 * @author Callum
 */
public class DummyConnection implements IClientConnection {
	private Player player;
	
	/**
	 * Constructs a new {@link DummyConnection}.
	 * @param _player The player to receive actions from the connection.
	 */
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
