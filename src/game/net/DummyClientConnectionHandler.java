package game.net;

import game.audio.event.AudioEvent;
import game.world.entity.Entity;

/**
 * An IClientConnectionHandler that does not handle any events.
 * 
 * @author Callum
 */
public class DummyClientConnectionHandler implements IClientConnectionHandler {
	@Override
	public void updateEntity(Entity _e) {
		System.err.println("Warning: Dummy client connection handler called.");
	}

	@Override
	public void removeEntity(int _id) {
		System.err.println("Warning: Dummy client connection handler called.");
	}

	@Override
	public void processAudioEvent(AudioEvent _ae) {
		System.err.println("Warning: Dummy client connection handler called.");
	}
}
