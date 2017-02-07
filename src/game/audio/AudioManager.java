package game.audio;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;


public class AudioManager implements IAudioManager{
	private static ArrayList<Integer> buffers = new ArrayList<>();
	private static long device;
	private static long context;
	private static HashMap<String, Integer> soundNameMap = new HashMap<String, Integer>();
	
	public AudioManager() throws FileNotFoundException{
		AudioManager.init();
		File folder = new File( System.getProperty("user.dir") + "/resources/audio_assets/" );
		File[] listOfFiles = folder.listFiles();
		//create array and hashmaps of all sounds
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	placeFileInBuffer(file.getName());
		    }
		}
	}
	
	/**
	 * Places all files in resources/audio_assets into a buffer, ready to be played
	 * @param filename
	 * @throws FileNotFoundException
	 */
	private void placeFileInBuffer(String filename) throws FileNotFoundException{
		int buffer = loadSound( System.getProperty("user.dir") + "/resources/audio_assets/"+filename);
		//add name to hash map array for easy referencing
		this.soundNameMap.put(filename, buffers.size());
		this.buffers.add(buffer);
		
	}
	
	public static void init(){
		final String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDeviceName);

		int[] attributes = {0};
		context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);

		AL.createCapabilities(ALC.createCapabilities(device));
	}
	
	public static void setListenerData(final float x, final float y, final float z)
	{
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}

	public int loadSound(final String file) throws FileNotFoundException
	{
		int buffer = AL10.alGenBuffers();
		this.buffers.add(buffer);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		WaveData wavFile = WaveData.create(bis);
		AL10.alBufferData(buffer, wavFile.format, wavFile.data, wavFile.samplerate);
		wavFile.dispose();
		return buffer;
	}
	
	public static void cleanUp()
	{
		for (final int buffer : buffers)
		{
			alDeleteBuffers(buffer);
		}

		//Terminate OpenAL
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		AudioManager am = new AudioManager();
		AudioManager.init();
		AudioManager.setListenerData(0,0,0);
//		final int buffer = AudioManager.loadSound( System.getProperty("user.dir") + "/resources/audio_assets/[bgm]Entombed.wav");
//		Source source = new Source();
//		source.setLooping(true);
//		source.setVolume(1f);
//		source.play(buffer);
//		while(source.isPlaying()){
//			//stops openAL from deleting source
//		}
//		source.delete();
		am.play("[bgm]Entombed.wav", 1f);
		//AudioManager.cleanUp();
	}

	@Override
	public void play(String name, float volume) {
		Source source = new Source();
		source.setVolume(volume);
		int buffer = AudioManager.buffers.get((int) AudioManager.soundNameMap.get(name));
		source.play(buffer);
		while(source.isPlaying()){
			//stops openAL from deleting source
		}
	}

	@Override
	public void playLoop(String name, float volume) {
		// TODO Auto-generated method stub
		Source source = new Source();
		source.setVolume(volume);
		source.setLooping(true);
		source.play(AudioManager.buffers.get((int) AudioManager.soundNameMap.get(name)));
	}

	@Override
	public void stopLoop(String name) {
		//TODO
		
	}	
}

