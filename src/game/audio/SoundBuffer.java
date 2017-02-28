package game.audio;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.lwjgl.openal.*;

public class SoundBuffer {
	private final int bufferId;
	private String bufferName;
	/**
	 * A sound buffer represents a sound file loaded in memory to be played by SoundSources
	 * @param file, the name of a wav file
	 * @throws Exception
	 */
	public SoundBuffer(String filepath) throws Exception {
		this.bufferId = AL10.alGenBuffers();
		this.bufferName=filepath.substring(27);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filepath));
		WaveData wavFile = WaveData.create(bis);
		AL10.alBufferData(bufferId, wavFile.format, wavFile.data, wavFile.samplerate);
		wavFile.dispose();
    }

	/**
	 * Gets the name of the file the buffer represents
	 * @return the name of the file the buffer represents
	 */
	public String getBufferName(){
		return this.bufferName;
	}
	
	/**
	 * Returns this buffer's id
	 * @return
	 */
    public int getBufferId() {
        return this.bufferId;
    }

    /**
     * Removes a buffer from memory
     */
    public void cleanup() {
        AL10.alDeleteBuffers(this.bufferId);
    }
    
}
