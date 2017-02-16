package game.audio;

public interface IAudioManager {
	void play(String name, float volume);
	int playLoop(String name, float volume);
	void continueLoop(int sourceID);
	void pauseLoop(int sourceID);
	void stopLoop(int sourceID);
}
