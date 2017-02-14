package game.networking.util.interfaces;

public interface IGameControllerUDP
{
	/**
	 * this function processes a message and returns the next action to be taken
	 * by the game
	 *
	 * @param message
	 * @return next action
	 */
	public String processMessage(String message);
}
