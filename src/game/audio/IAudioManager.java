package game.audio;

public interface IAudioManager {
	void play(String name, float volume);
	void playLoop(String name, float volume);
	void stopLoop(String name);
}
