package game.audio;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {

    private final int sourceId;

    public SoundSource(boolean loop, boolean relative) {
        this.sourceId = alGenSources();
        if (loop) {
            alSourcei(sourceId, AL_LOOPING, AL_TRUE);
        }
        if (relative) {
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
        }
    }

    public int getSourceId(){
    	return this.sourceId;
    }
    
    public void setRelative(boolean bool){
    	if (bool){
    		alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
    	}
    	else{
    		alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_FALSE);	
    	}
    }
    
    public void setBuffer(int bufferId) {
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }
    
	public void setVolume(final float volume)
	{
		alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

    
    public void continuePlaying()
	{
		alSourcePlay(sourceId);
	}
    
    public void setLooping(final boolean loop)
	{
		alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}
    
    public void setPosition(Vector3f position) {
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
    }

    public void setSpeed(Vector3f speed) {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setGain(float gain) {
        alSourcef(sourceId, AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
        alSourcef(sourceId, param, value);
    }

    public void play() {
        alSourcePlay(sourceId);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void cleanup() {
        stop();
        alDeleteSources(sourceId);
    }
}

