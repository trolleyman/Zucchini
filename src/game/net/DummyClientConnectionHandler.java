package game.net;

import game.world.entity.Entity;

/**
 * An IClientConnectionHandler that does not handle any events.
 * 
 * @author Callum
 */
public class DummyClientConnectionHandler implements IClientConnectionHandler {
	@Override
	public void handleUpdateEntity(Entity _e) {
		System.err.println("Warning: Dummy client connection handler called.");
	}

	@Override
	public void handleRemoveEntity(int _id) {
		System.err.println("Warning: Dummy client connection handler called.");
	}
}
