package game.audio;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {
	
	private final float zPos = 1.0f; //fixed z pos since our game will be 2D
    private final int sourceId;

    /**
     * The object that represents an entity that creates some form of sound
     * @param loop, Is this sound to be looped?
     * @param relative, Was the sound relative to the player?
     */
    public SoundSource(boolean loop, boolean relative) {
        this.sourceId = alGenSources();
        if (loop) {
            alSourcei(sourceId, AL_LOOPING, AL_TRUE);
        }
        if (relative) {
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
        }
        alSourcef(sourceId, AL_MAX_DISTANCE, 1f);
        
    }

    /**
     * Returns a source id
     * @return sourceID
     */
    public int getSourceId(){
    	return this.sourceId;
    }
    
    /**
     * Sets a source to be a relative source
     * @param bool
     */
    public void setRelative(boolean bool){
    	if (bool){
    		alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
    	}
    	else{
    		alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_FALSE);	
    	}
    }
    
    /**
     * Sets the sound buffer from which this source will play from
     * @param bufferId
     */
    public void setBuffer(int bufferId) {
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }
    
    /**
     * sets the volume of a source
     * @param volume
     */
    //this method will eventually be depreciated, as we will let OpenAL handle volumes
	public void setVolume(final float volume)
	{
		alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

    /**
     * Continues to play a source previously paused
     */
    public void continuePlaying()
	{
		alSourcePlay(sourceId);
	}
    
    /**
     * Sets a source to always repeat the sound
     * @param loop
     */
    public void setLooping(final boolean loop)
	{
		alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}
    
    /**
     * Sets the position of a source
     * @param position
     */
    public void setPosition(Vector2f position) {
        alSource3f(sourceId, AL_POSITION, position.x, position.y, zPos);
    }

    /**
     * Sets the speed of a source
     * @param speed
     */
    public void setSpeed(Vector3f speed) {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    /**
     * Sets the gain of a source
     * @param gain
     */
    public void setGain(float gain) {
        alSourcef(sourceId, AL_GAIN, gain);
    }

    /**
     * 
     * @param param
     * @param value
     */
    public void setProperty(int param, float value) {
        alSourcef(sourceId, param, value);
    }

    /**
     * Plays a source, from beggining to end
     */
    public void play() {
        alSourcePlay(sourceId);
    }

    /**
     * Returns if the source is currently playing
     * @return
     */
    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    /**
     * Pauses the source from playing
     */
    public void pause() {
        alSourcePause(sourceId);
    }

    /**
     * Stops the source from playing, this is different from pause as if we restart the source again, it will begin from the start
     */
    public void stop() {
        alSourceStop(sourceId);
    }

    /**
     * Removes source from memory
     */
    public void cleanup() {
        stop();
        alDeleteSources(sourceId);
    }
}

