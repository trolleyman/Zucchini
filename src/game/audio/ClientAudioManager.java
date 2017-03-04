package game.audio;

import java.util.ArrayList;
import java.util.HashMap;

import game.audio.event.AudioContinueLoopEvent;
import game.audio.event.AudioEvent;
import game.audio.event.AudioPauseLoopEvent;
import game.audio.event.AudioPlayEvent;
import game.audio.event.AudioPlayLoopEvent;
import game.audio.event.AudioStopEvent;
import game.audio.event.AudioUpdateSourcePosEvent;

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
			int sourceID = audio.findAvailableSoundSourceID(e.name);
			if (sourceID==-1){
				System.err.println("Warning: no more sound sources for file: "+e.name+"! This will not be played.");
			}else{
				audio.continueLoop(sourceID, e.position);
//				System.out.println("placing networked id " + e.id + "into map");
//				System.out.println(ids.toString());
				ids.put(e.id, sourceID);
			}
		} else if (ae instanceof AudioPlayLoopEvent) {
			AudioPlayLoopEvent e = (AudioPlayLoopEvent) ae;
			int sourceID = audio.findAvailableSoundSourceID(e.name);
			//System.out.println("Found (client) id to loop: "+sourceID+" for "+e.name);
			if (sourceID==-1){
				System.err.println("Warning: no more sound sources for file: "+e.name+" available to loop! This will not be played. Stop some playing loops or assign more sources for this sound.");
			}else{
				ids.put(e.id, sourceID);
				audio.playLoop(e.name,sourceID,e.position);
			}
		} else if (ae instanceof AudioStopEvent) {
			AudioStopEvent e = (AudioStopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			int sourceID = ids.get(e.id);
			System.out.println("Stopping (client) id: "+sourceID);
			ids.remove(e.id);
			audio.stopLoop(sourceID);
		} else if (ae instanceof AudioContinueLoopEvent){
			AudioContinueLoopEvent e = (AudioContinueLoopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("CONTINUE LOOP Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			//int sourceID = ids.get(e.id);
			int sourceID = ids.get(e.id);
			//System.out.println("Continuing (client) sourceID: "+ sourceID);
			audio.continueLoop(sourceID,e.position);
		} else if (ae instanceof AudioPauseLoopEvent){
			AudioPauseLoopEvent e = (AudioPauseLoopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			int sourceID = ids.get(e.id);
			//System.out.println("Pausing (client) sourceID: "+ sourceID);
			audio.pauseLoop(sourceID);
		} else if (ae instanceof AudioUpdateSourcePosEvent){
			AudioUpdateSourcePosEvent e = (AudioUpdateSourcePosEvent) ae;
			if(!ids.containsKey(e.id)){
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			int sourceID = ids.get(e.id);
			audio.updateSourcePos(sourceID, e.position);
		}
		
	}
}
