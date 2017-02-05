package game.audio;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.*;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;


public class AudioManager {
	private static List<Integer> buffers = new ArrayList<>();
	private static long device;
	private static long context;

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

	public static int loadSound(final String file) throws FileNotFoundException
	{
		final int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
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
		AudioManager.init();
		AudioManager.setListenerData(0,0,0);
		final int buffer = AudioManager.loadSound( System.getProperty("user.dir") + "/resources/audio_assets/[bgm]Entombed.wav");
		Source source = new Source();
		source.setLooping(true);
		source.setVolume(1f);
		source.play(buffer);
		while(source.isPlaying()){
			//stops openAL from deleting source
		}
		source.delete();
		AudioManager.cleanUp();
	}	
}

