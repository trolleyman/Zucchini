package game.audio;

public interface IAudioManager {
	void play(String name, float volume);
	int playLoop(String name, float volume);
	void stopLoop(int sourceID);
}
