package game.audio;

import game.audio.event.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the clent side's audio manager. It takes in audio events to be processed from the server audio manager.
 *
 * @author Yean & Callum
 */
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
	 *
	 * @param ae The AudioEvent
	 */
	public void processAudioEvent(AudioEvent ae) {
		if (ae instanceof AudioPlayEvent) {
			AudioPlayEvent e = (AudioPlayEvent) ae;
			int sourceID = audio.findAvailableSoundSourceID(e.name);
			if (sourceID == -1) {
				System.err.println("Warning: no more sound sources for file: " + e.name + "! This will not be played. (networked id " + e.id + ")");
				// Put a dummy source id in the map to indicate that the audio manager knows that
				// there is no client source ID for the networked ID.
				ids.put(e.id, -1);
			} else {
				audio.continueLoop(sourceID, e.position);
//				System.out.println("placing networked id " + e.id + "into map");
//				System.out.println(ids.toString());
				ids.put(e.id, sourceID);
			}
		} else if (ae instanceof AudioPlayLoopEvent) {
			AudioPlayLoopEvent e = (AudioPlayLoopEvent) ae;
			int sourceID = audio.findAvailableSoundSourceID(e.name);
			//System.out.println("Found (client) id to loop: "+sourceID+" for "+e.name);
			if (sourceID == -1) {
				System.err.println("Warning: no more sound sources for file: " + e.name + " available to loop! This will not be played. Stop some playing loops or assign more sources for this sound.");
				// Put a dummy source id in the map to indicate that the audio manager knows that
				// there is no client source ID for the networked ID.
				ids.put(e.id, -1);
			} else {
				ids.put(e.id, sourceID);
				audio.playLoop(e.name, sourceID, e.position);
			}
		} else if (ae instanceof AudioStopEvent) {
			AudioStopEvent e = (AudioStopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			int sourceID = ids.get(e.id);
			ids.remove(e.id);
			if (sourceID == -1) {
				// This audio play has failed on the client side, but not the server side.
				// A warning has already been given: give up.
				return;
			}
			audio.stopLoop(sourceID);
		} else if (ae instanceof AudioContinueLoopEvent) {
			AudioContinueLoopEvent e = (AudioContinueLoopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("CONTINUE LOOP Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			//int sourceID = ids.get(e.id);
			int sourceID = ids.get(e.id);
			if (sourceID == -1) {
				// This audio play has failed on the client side, but not the server side.
				// A warning has already been given: give up.
				return;
			}
			//System.out.println("Continuing (client) sourceID: "+ sourceID);
			audio.continueLoop(sourceID, e.position);
		} else if (ae instanceof AudioPauseLoopEvent) {
			AudioPauseLoopEvent e = (AudioPauseLoopEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			int sourceID = ids.get(e.id);
			if (sourceID == -1) {
				// This audio play has failed on the client side, but not the server side.
				// A warning has already been given: give up.
				return;
			}
			//System.out.println("Pausing (client) sourceID: "+ sourceID);
			audio.pauseLoop(sourceID);
		} else if (ae instanceof AudioUpdateSourcePosEvent) {
			AudioUpdateSourcePosEvent e = (AudioUpdateSourcePosEvent) ae;
			if (!ids.containsKey(e.id)) {
				System.err.println("Warning: Unknown audio (networked) id: " + e.id);
				return;
			}
			int sourceID = ids.get(e.id);
			if (sourceID == -1) {
				// This audio play has failed on the client side, but not the server side.
				// A warning has already been given: give up.
				return;
			}
			audio.updateSourcePos(sourceID, e.position);
		}
		
	}
}
