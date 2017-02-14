package game.audio.event;

public class AudioStopEvent extends AudioEvent {
	public int id;
	
	public AudioStopEvent(int _id) {
		this.id = _id;
	}
}
