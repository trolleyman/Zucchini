package game.audio.event;

import org.joml.Vector2f;

public class AudioPlayEvent extends AudioEvent {
	public int id;
	public String name;
	public float volume;
	public Vector2f position;
	
	public AudioPlayEvent(String _name,int id, float _volume, Vector2f _position) {
		this.id = id;
		this.name = _name;
		this.volume = _volume;
		this.position = _position;
	}
}
