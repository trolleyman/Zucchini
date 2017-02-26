package game.audio;

import java.util.ArrayList;
import java.util.HashMap;

import game.audio.event.AudioContinueLoopEvent;
import game.audio.event.AudioEvent;
import game.audio.event.AudioPauseLoopEvent;
import game.audio.event.AudioPlayEvent;
import game.audio.event.AudioPlayLoopEvent;
import game.audio.event.AudioStopEvent;

public class ClientAudioManager {
	private AudioManager audio;
	
	/** This maps audio (networked) ids -> source ids */
	private HashMap<Integer, Integer> ids = new HashMap<>();
	private ArrayList<Integer> lIds = new ArrayList<>();
	
	public ClientAudioManager(AudioManager _audio) {
		this.audio = _audio;
	}
	
	/**
	 * Handles the given AudioEvent
	 * @param ae The AudioEvent
	 */
	public void processAudioEvent(AudioEvent ae) {
		if (ae instanceof AudioPlayEvent) {
			AudioPlayEvent e = (AudioPlayEvent) ae;
			audio.play(e.name, e.volume,e.position);
		} else if (ae instanceof AudioPlayLoopEvent) {
			AudioPlayLoopEvent e = (AudioPlayLoopEvent) ae;
			int sourceID = audio.playLoop(e.name, e.volume,e.position);
			if (sourceID==-1){
				System.err.println("Warning: no more sound sources for file: "+e.name+" available to loop! This will not be played. Stop some playing loops or assign more sources for this sound.");
			}else{
				//if for some reason, the sound source is already in the hash map, then do audio.playLoop again to get a new source
				while (ids.containsValue(sourceID)){
					sourceID = audio.playLoop(e.name, e.volume,e.position);
				}
				ids.put(e.id, sourceID);
				lIds.add(sourceID);
			}
		} else if (ae instanceof AudioStopEvent) {
			AudioStopEvent e = (AudioStopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			//int sourceID = ids.get(e.id);
			int sourceID = lIds.get(e.id);
			audio.stopLoop(sourceID);
		} else if (ae instanceof AudioContinueLoopEvent){
			AudioContinueLoopEvent e = (AudioContinueLoopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			//int sourceID = ids.get(e.id);
			int sourceID = lIds.get(e.id);
			//System.out.println("Continuing (client) sourceID: "+ sourceID);
			audio.continueLoop(sourceID,e.position);
		} else if (ae instanceof AudioPauseLoopEvent){
			AudioPauseLoopEvent e = (AudioPauseLoopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			//int sourceID = ids.get(e.id);
			int sourceID = lIds.get(e.id);
			//System.out.println("Pausing (client) sourceID: "+ sourceID);
			audio.pauseLoop(sourceID);
		}
		
	}
}
