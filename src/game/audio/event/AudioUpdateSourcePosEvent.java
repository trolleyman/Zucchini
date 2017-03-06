package game.audio.event;

import org.joml.Vector2f;

public class AudioUpdateSourcePosEvent extends AudioEvent {
	public int id;
	public Vector2f position;
	
	public AudioUpdateSourcePosEvent(int id, Vector2f position){
		this.id=id;
		this.position=position;
	}
}
