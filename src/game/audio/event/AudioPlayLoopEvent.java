package game.audio.event;

public class AudioPlayLoopEvent extends AudioEvent {
	public String name;
	public int id;
	public float volume;
	
	public AudioPlayLoopEvent(String _name, int _id, float _volume) {
		this.name = _name;
		this.id = _id;
		this.volume = _volume;
	}
}
