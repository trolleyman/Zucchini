package test.audio;

import static org.junit.Assert.*;

import org.joml.Vector2f;
import org.junit.After;
import org.junit.Test;

import game.audio.AudioManager;
import game.audio.SoundBuffer;

public class AudioManagerTests{
	private AudioManager audio = new AudioManager();

	public AudioManagerTests() throws Exception{
	}

	@After
	public void close(){
		audio.cleanup();
	}
	
	@Test
	public void muteTest(){
		audio.mute();
		assertTrue(audio.isMuted());
	}
	
	@Test
	public void unMuteTest(){
		audio.unMute();
		assertFalse(audio.isMuted());
	}
	
	@Test
	public void setVolumeTest(){
		audio.setVolume(0.1f);
		assertTrue(audio.currentVolume == 0.1f);
		audio.setVolume(0.2f);
		assertTrue(audio.currentVolume == 0.2f);		
		audio.setVolume(0.3f);
		assertTrue(audio.currentVolume == 0.3f);
		audio.setVolume(0.6f);
		assertTrue(audio.currentVolume == 0.6f);
		audio.setVolume(0.9f);
		assertTrue(audio.currentVolume == 0.9f);
	}
	
	@Test
	public void getVolumeTest(){
		audio.setVolume(0.9f);
		assertTrue(audio.getVolume() == 0.9f);
	}
	
	@Test  (expected = Exception.class)
	public void findAvailableSoundSourceTest(){
		assertNotNull(audio.findAvailableSoundSource("slash.wav"));
		assertNull(audio.findAvailableSoundSource("this_wav_file_doesn't_exist.wav"));
	}
	
	@Test
	public void playTest(){
		int id = audio.play("slash.wav", 1f, new Vector2f(0,0));
		assertTrue(audio.getSoundSource(id).isPlaying());
	}
	
	@Test
	public void pauseTest(){
		int id = audio.play("slash.wav", 1f, new Vector2f(0,0));
		audio.pauseLoop(id);
		assertFalse(audio.getSoundSource(id).isPlaying());
	}
	
	@Test
	public void getSoundSourceTest(){
		int id = audio.play("slash.wav", 1f, new Vector2f(0,0));
		assertNotNull(audio.getSoundSource(id));
	}
	
	@Test
	public void updateSourcePositionTest(){
		Vector2f init = new Vector2f(0,0);
		Vector2f update = new Vector2f(1,1);
		int id = audio.play("slash.wav", 1f, init);
		audio.updateSourcePos(id, update);
	}
	
	@Test
	public void updateListenerPositionTest(){
		Vector2f update = new Vector2f(1,1);
		audio.updateListenerPosition(update);
		assertTrue(audio.getListener().getPos() == update);
	}
	
}
