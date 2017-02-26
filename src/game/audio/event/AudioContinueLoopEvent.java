package game.audio.event;

import org.joml.Vector2f;

public class AudioContinueLoopEvent extends AudioEvent {
	public int id;
	public Vector2f position;
	
	public AudioContinueLoopEvent(int _id, Vector2f position) {
		this.id = _id;
		this.position = position;
	}
}
