package game.audio.event;

public class AudioContinueLoopEvent extends AudioEvent {
	public int id;
	
	public AudioContinueLoopEvent(int _id) {
		this.id = _id;
	}
}
