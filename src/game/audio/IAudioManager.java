package game.audio;

import org.joml.Vector2f;

public interface IAudioManager {
	void play(String name, float volume, Vector2f position);
	int playLoop(String name, float volume,Vector2f position);
	void continueLoop(int sourceID,Vector2f position);
	void pauseLoop(int sourceID);
	void stopLoop(int sourceID);
}
