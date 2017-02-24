package game.audio.event;

import org.joml.Vector2f;

public class AudioPlayEvent extends AudioEvent {
	public String name;
	public float volume;
	public Vector2f position;
	
	public AudioPlayEvent(String _name, float _volume, Vector2f _position) {
		this.name = _name;
		this.volume = _volume;
		this.position = _position;
	}
}
