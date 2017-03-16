package game.audio;
import java.io.File;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import game.Resources;
import game.Util;
import org.joml.Vector2f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;


/**
 * This is the main audio manager class that will contain methods to play sounds, update position of sounds and listeners (players).
 * The audio manager is capable of simulating a world space of several sound effects playing at once and manages what sounds a player can hear.
 * @author Yean
 *
 */
public class AudioManager implements IAudioManager{
	private long device;
    private long context;
    private SoundListener listener;
    private List<SoundBuffer> soundBufferList= new ArrayList<>();
    private Map<Integer,String> soundBufferMap= new HashMap<>();
    private Map<String, List<SoundSource>> soundSourcesMap = new HashMap<>();
    //private final int numberOfSourcesPerFile = 16; //this is the amount of sources each wav file will have available for them
    private static Vector2f listenerPos = new Vector2f(0, 0);
	/** A hash map relating Filename->number of sources*/
	public Map<String,Integer> fileSourceMap = new HashMap<>();
	
    /**
     * Constructor for AudioManager, will initialise OpenAL, get all sound files from the resources/audio_assets library and places them into
     * memory to be played. The main backgroud music will also be played in an infinite loop. Also initialises Sources, the object from which sounds will be played.
     * @throws Exception
     */
    public AudioManager() throws Exception {
    	init();
    	setListener(new SoundListener(listenerPos));
    	 
    	alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
        
    	
        System.out.println("Loading audio...");
        //place all files into the buffer list
        HashMap<String, byte[]> audioFiles = Resources.getAudioFiles();
		//create array and hashmaps of all sounds
		for (Entry<String, byte[]> e : audioFiles.entrySet()) {
	        placeFileInBuffer(e.getValue(), e.getKey());
		}
		
		//create the sources for each buffer
		for (SoundBuffer soundBuffer : soundBufferList){
			String filename = soundBuffer.getBufferName();
			List<SoundSource> soundSourcesList = new ArrayList<>();
			for(int i=0; i<fileSourceMap.get(filename); i++){
				SoundSource source = new SoundSource(false,false); 
				//check that we have a source to play, otherwise system exit
				if (alGetError() != AL_NO_ERROR) {
					System.err.println("Too many sources! reduce the number of sources! Exiting...");
					System.exit(-1);
				}
				source.setBuffer(soundBuffer.getBufferId());
				soundSourcesList.add(source);
				//special cases: explosion sound and gun firing, be more louder and able to hear this far away
				if(filename.equals("explosion.wav")){
					source.setRolloffFactor(1f);
					source.setReferenceDistance(1.5f);
				}
				if( filename.equals("handgunshot.wav")){
					source.setRolloffFactor(2f);
					source.setReferenceDistance(1.5f);
				}
				if( filename.equals("pump-shotgun-shot.wav")){
					source.setRolloffFactor(2f);
					source.setReferenceDistance(1.5f);
				}
			}
			soundSourcesMap.put(soundBufferMap.get(soundBuffer.getBufferId()), soundSourcesList);
			System.out.println("Loaded audio: " + soundBuffer.getBufferName());
		}
		//play bgm
		SoundSource bgm = new SoundSource(true,true);
		bgm.setVolume(1.0f);
		String bgmName = "[bgm]Desolation.wav";
		SoundBuffer buffer = new SoundBuffer(audioFiles.get(bgmName), bgmName);
		bgm.setBuffer(buffer.getBufferId());
		bgm.play();
		
		System.out.println(audioFiles.size() + " audio file(s) loaded.\n");
    }
    /**
     * Starts OpenAL procedures and creates sources layout
     * @throws Exception
     */
    public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
        
    	//there are a max of 250 sources
        fileSourceMap.put("[bgm]Desolation.wav", 0);
		fileSourceMap.put("bullet_impact_body.wav", 10);
		fileSourceMap.put("bullet_impact_wall.wav", 20);
		fileSourceMap.put("bullet_whiz1.wav", 0);
		fileSourceMap.put("bullet_whizz_silent.wav", 20);
		fileSourceMap.put("bullet_whizz2.wav", 0);
		fileSourceMap.put("bullet_whizz3.wav", 0);
		fileSourceMap.put("explosion.wav", 5);
		fileSourceMap.put("footsteps_running.wav", 30);
		fileSourceMap.put("footsteps_walking.wav", 0);
		fileSourceMap.put("grunt1.wav", 5);
		fileSourceMap.put("grunt2.wav", 5);
		fileSourceMap.put("grunt3.wav", 5);
		fileSourceMap.put("grunt4.wav", 5);
		fileSourceMap.put("gun_reload[2sec].wav", 3);
		fileSourceMap.put("handgunshot.wav", 30);
		fileSourceMap.put("laser_round.wav", 5);
		fileSourceMap.put("lasergun-fire.wav", 5);
		fileSourceMap.put("no-ammo-click.wav", 3);
		fileSourceMap.put("pump-shotgun-reload[4sec].wav", 3);
		fileSourceMap.put("pump-shotgun-shot.wav", 5);
		fileSourceMap.put("punch-hit.wav", 5);
		fileSourceMap.put("rocket_reload[5sec].wav", 5);
		fileSourceMap.put("rocket-launcher.wav", 5);
		fileSourceMap.put("slash.wav", 20);
		fileSourceMap.put("zombie1.wav", 20);
		fileSourceMap.put("zombie2.wav", 20);
		fileSourceMap.put("zombie3.wav", 20);
    }
    
    
    public void mute(){
        alListenerf(AL_GAIN, 0f);
    }
    
    /**
     * Iterate available sound sources for a buffer and return it
     * returns null if no sources are available
     * @param wavfile
     */
    public SoundSource findAvailableSoundSource(String wavfile){
		List<SoundSource> sources = this.soundSourcesMap.get(wavfile);
		for (SoundSource source : sources){
			if (!source.isPlaying()){
				return source;
			}
		}
		return null;
    }
    
    /**
     * Finds the id of the next available sound source to be used
     * @param wavfile
     * @return id
     */
    public int findAvailableSoundSourceID(String wavfile){
		List<SoundSource> sources = this.soundSourcesMap.get(wavfile);
		for (SoundSource source : sources){
			if (!source.isPlaying()){
				return source.getSourceId();
			}
		}
		return -1;
    }

    /**
     * Adds a sound buffer
     * @param soundBuffer
     */
    public void addSoundBuffer(SoundBuffer soundBuffer) {
        this.soundBufferList.add(soundBuffer);
    }
    
    /**
     * Gets the listener
     * @return listener
     */
    public SoundListener getListener() {
        return this.listener;
    }
    
    /**
     * Sets the listener
     * @param listener
     */
    public void setListener(SoundListener listener) {
        this.listener = listener;
    }
    
    /**
     * Update where the listener is in the game world
     * @param pos
     */
    public void updateListenerPosition(Vector2f pos) {
    	listener.setPosition(pos);
    }
    
    /**
     * Removes all sources, buffers and such from memeory
     */
    public void cleanup() {
        for (List<SoundSource> soundSourcesList : soundSourcesMap.values()) {
        	for (SoundSource soundSource: soundSourcesList){
        		soundSource.cleanup();
        	}
        }
        soundSourcesMap.clear();
        for (SoundBuffer soundBuffer : soundBufferList) {
            soundBuffer.cleanup();
        }
        soundBufferList.clear();
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
    
    /**
     * Plays a sound
     * @param name, the name of the wavfile
     * @param volume, the volume of the sound
     */
    @Override
	public int play(String name, float volume, Vector2f position) {
    	//check that we have a source to play
    	SoundSource source = findAvailableSoundSource(name);
    	if (source != null){
			source.setVolume(volume);
			source.setPosition(position);
			source.play();
			return source.getSourceId();
			//DSystem.out.println("Played "+name+" at position: "+position.toString());
    	}
    	return -1;
	}
    
    /**
     * updates a position of a source
     */
    @Override
	public void updateSourcePos(int sourceID, Vector2f position) {
		SoundSource source = getSoundSource(sourceID);
		source.setPosition(position);
	}
    
    /**
     * Plays a wav file in a continous loop, returns -1 if no source can be found to play from!
     * @return A sourceID that can be used to stop a particular source, returns -1 if no available source
     */
    @Override
	public int playLoop(String name, float volume, Vector2f position) {
    	SoundSource source = findAvailableSoundSource(name);
    	if (source != null){
        	System.out.println("Found and using (client) sourceID: " + source.getSourceId()+" for sound "+name);
			source.setVolume(volume);
			source.setLooping(true);
			source.setPosition(position);
			source.play();
			//source.setInUse(true);
			return(source.getSourceId());
    	}
		return -1;
	}
    
    
    
    /**
     * Like play loop, but this funtion takes a sourceID instead to continue a loop
     * @param sourceID, the source from which a sound will continue to be playing from
     */
    @Override
    public void continueLoop(int sourceID, Vector2f position){
    	SoundSource source = getSoundSource(sourceID);
	    if (source == null) {
		    System.err.println("Warning: invalid sourceID in continueLoop: " + sourceID);
		    return;
	    }
    	source.setPosition(position);
    	if (!source.isPlaying()){
    		source.play();
    	}
    }
    
    /**
     * Like play loop, but this funtion takes a sourceID instead to pause a loop
     * @param sourceID, the source from which a sound will be paused from playing from
     */
    @Override
    public void pauseLoop(int sourceID){
    	SoundSource source = getSoundSource(sourceID);
    	if (source == null) {
    		System.err.println("Warning: invalid sourceID in pauseLoop: " + sourceID);
    		return;
	    }
    	if (source.isPlaying()){
    		source.pause();
    	}
    }
    
    /**
     * Stops a source from playing using it's id
     */
    @Override
	public void stopLoop(int sourceID) {
    	System.out.println("getting sourceID: " +sourceID);
    	SoundSource source = getSoundSource(sourceID);
	    if (source == null) {
		    System.err.println("Warning: invalid sourceID in stopLoop: " + sourceID);
		    return;
	    }
    	System.out.println("source: "+source);
    	System.out.println("Stopping sourceID: " +source.getSourceId());
    	source.stop();
	}
    
    /**
     * Given a sound source ID, this will return the sound source object returns null if it isnt found
     * @param sourceID
     * @return SoundSource
     */
    public SoundSource getSoundSource(int sourceID){
    	for (List<SoundSource> list : this.soundSourcesMap.values()){
    		for(SoundSource source : list){
    			if (source.getSourceId()==sourceID){
    				return source;
    			}
    		}
    	}
    	return null;
    }
    
	/**
	 * Place file in resources/audio_assets into a buffer, ready to be played
	 * @throws Exception
	 */
	private void placeFileInBuffer(byte[] data, String name) throws Exception{
		SoundBuffer buffer = new SoundBuffer(data, name);
		//add buffer to list and add name to hash map array for easy referencing
		this.soundBufferList.add(buffer);
		this.soundBufferMap.put(buffer.getBufferId(), name);
	}
	
    
	
	
}