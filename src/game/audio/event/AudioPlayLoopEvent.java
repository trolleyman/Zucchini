package game.audio.event;

import org.joml.Vector2f;

public class AudioPlayLoopEvent extends AudioEvent {
	public String name;
	public int id;
	public float volume;
	public Vector2f position;
	
	public AudioPlayLoopEvent(String _name, int _id, float _volume, Vector2f position) {
		this.name = _name;
		this.id = _id;
		this.volume = _volume;
		this.position = position;
	}
}
