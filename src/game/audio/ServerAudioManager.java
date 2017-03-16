package game.audio;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.audio.event.AudioContinueLoopEvent;
import game.audio.event.AudioEvent;
import game.audio.event.AudioIsPlayingCheck;
import game.audio.event.AudioPauseLoopEvent;
import game.audio.event.AudioPlayEvent;
import game.audio.event.AudioPlayLoopEvent;
import game.audio.event.AudioStopEvent;
import game.audio.event.AudioUpdateSourcePosEvent;

/**
 * Server side audio management. Will make audio events when they happen and feed them to the client side audio manager running on each client.
 * @author Yean & Callum
 *
 */
public class ServerAudioManager implements IAudioManager {
	private int nextAudioID = 0;
	
	//array list of all current audio events
	private ArrayList<AudioEvent> events = new ArrayList<>();
	
	public ServerAudioManager() {}
	
	public ArrayList<AudioEvent> clearCache() {
		ArrayList<AudioEvent> temp = events;
		events = new ArrayList<>();
		return temp;
	}
	
	@Override
	public int play(String name, float volume, Vector2f position) {
		int id = this.nextAudioID++;
		events.add(new AudioPlayEvent(name,id, volume, position));
		return id;
	}
	
	@Override
	public int playLoop(String name, float volume, Vector2f position) {
		int id = this.nextAudioID++;
		events.add(new AudioPlayLoopEvent(name, id, volume, position));
		return id;
	}
	
	@Override
	public void continueLoop(int id, Vector2f position){
		events.add(new AudioContinueLoopEvent(id, position));
	}
	
	@Override
	public void stopLoop(int id) {
		events.add(new AudioStopEvent(id));
	}

	@Override
	public void pauseLoop(int id) {
		events.add(new AudioPauseLoopEvent(id));
	}

	@Override
	public void updateSourcePos(int sourceID, Vector2f position) {
		events.add(new AudioUpdateSourcePosEvent(sourceID, position));
	}

}
