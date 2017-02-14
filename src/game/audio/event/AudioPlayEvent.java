package game.audio.event;

public class AudioPlayEvent extends AudioEvent {
	public String name;
	public float volume;
	
	public AudioPlayEvent(String _name, float _volume) {
		this.name = _name;
		this.volume = _volume;
	}
}
