package game.audio;

import java.util.ArrayList;

import game.audio.event.AudioEvent;
import game.audio.event.AudioPlayEvent;
import game.audio.event.AudioPlayLoopEvent;
import game.audio.event.AudioStopEvent;

public class ServerAudioManager implements IAudioManager {
	private int nextAudioID = 1;
	
	private ArrayList<AudioEvent> events = new ArrayList<>();
	
	public ServerAudioManager() {}
	
	public ArrayList<AudioEvent> clearCache() {
		ArrayList<AudioEvent> temp = events;
		events = new ArrayList<>();
		return temp;
	}
	
	@Override
	public void play(String name, float volume) {
		events.add(new AudioPlayEvent(name, volume));
	}
	
	@Override
	public int playLoop(String name, float volume) {
		int id = this.nextAudioID++;
		events.add(new AudioPlayLoopEvent(name, id, volume));
		return id;
	}
	
	@Override
	public void stopLoop(int id) {
		events.add(new AudioStopEvent(id));
	}
}
