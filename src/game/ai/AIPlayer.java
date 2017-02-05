package game.ai;

import game.world.ServerWorld;
import game.world.entity.Player;

public class AIPlayer extends AI {
	/**
	 * Clones the specified AI
	 * @param ai The AI
	 */
	public AIPlayer(AIPlayer ai) {
		super(ai);
	}
	
	public AIPlayer(Player _player/* TODO: , Connection whatever*/) {
		super(_player.getId());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(ServerWorld _w, double _dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public AIPlayer clone() {
		return new AIPlayer(this);
	}
}
