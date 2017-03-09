package game.audio;

import org.joml.Vector2f;

public interface IAudioManager {
	int play(String name, float volume, Vector2f position);
	int playLoop(String name, float volume, Vector2f position);
	void updateSourcePos(int sourceID, Vector2f position);
	void continueLoop(int sourceID, Vector2f position);
	void pauseLoop(int sourceID);
	void stopLoop(int sourceID);
}
