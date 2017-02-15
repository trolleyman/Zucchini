package game.audio;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.lwjgl.openal.*;

public class SoundBuffer {
	private final int bufferId;
	
	public SoundBuffer(String file) throws Exception {
		this.bufferId = AL10.alGenBuffers();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		WaveData wavFile = WaveData.create(bis);
		AL10.alBufferData(bufferId, wavFile.format, wavFile.data, wavFile.samplerate);
		wavFile.dispose();
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public void cleanup() {
        AL10.alDeleteBuffers(this.bufferId);
    }
    
}
