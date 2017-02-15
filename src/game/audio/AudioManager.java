package game.audio;
import game.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;

import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioManager implements IAudioManager{
	private long device;
    private long context;
    private SoundListener listener;
    private final List<SoundBuffer> soundBufferList;
    private final Map<Integer,String> soundBufferMap;
    private final Map<String, List<SoundSource>> soundSourcesMap;
    private final Matrix4f cameraMatrix;
    private final int numberOfSourcesPerFile = 10; //this is the amount of sources each wav file will have available for them

    /**
     * Constructor for AudioManager, will initialise OpenAL, get all sound files from the resources/audio_assets library and places them into
     * memory to be played. The main backgroud music will also be played in an infinite loop. Also initialises Sources, the object from which sounds will be played.
     * @throws Exception
     */
    public AudioManager() throws Exception {
    	init();
    	//PLACEHOLDER this can be a players position on a map
    	setListener(new SoundListener(new Vector3f(0, 0, 0)));
    	
        soundBufferList = new ArrayList<>();
        soundBufferMap = new HashMap<>();
        soundSourcesMap = new HashMap<>();
        cameraMatrix = new Matrix4f();
        
        System.out.println("Loading audio...");
        //place all files into the buffer list
        File folder = new File( System.getProperty("user.dir") + "/resources/audio_assets/" );
		File[] listOfFiles = folder.listFiles();
		//create array and hashmaps of all sounds
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	placeFileInBuffer(file.getName());
		    }
		}
		
		//create the sources for each buffer
		int j = 0;
		for (SoundBuffer soundBuffer : soundBufferList){
			List<SoundSource> soundSourcesList = new ArrayList<>();
			for(int i=0; i<numberOfSourcesPerFile; i++){
				SoundSource source = new SoundSource(false,true); //ALL SOURCES RELATIVE TO PLAYER CURRENTLY = TRUE, this is the second parameter of this object
				//check that we have a source to play, otherwise system exit
				if (alGetError() != AL_NO_ERROR) {
					System.err.println("Too many sources! reduce the number of sources! Exiting...");
					System.exit(-1);
				}
				source.setBuffer(soundBuffer.getBufferId());
				soundSourcesList.add(source);
			}
			soundSourcesMap.put(soundBufferMap.get(soundBuffer.getBufferId()), soundSourcesList);
			System.out.println("Loaded audio: " + listOfFiles[j].getName());
			j++;
		}

		//play bgm
		SoundSource bgm = new SoundSource(true,true);
		SoundBuffer buffer = new SoundBuffer( System.getProperty("user.dir") + "/resources/audio_assets/[bgm]Desolation.wav");
		bgm.setBuffer(buffer.getBufferId());
		bgm.play();
		
		
		
		
		System.out.println(listOfFiles.length + " audio file(s) loaded.");
		
    }

    /**
     * Starts OpenAL procedures
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
    }
    
//    public void addSoundSources(String name, List<SoundSource> soundSources) {
//        this.soundSourcesMap.put(name, soundSources);
//    }
//
//    public SoundSource getSoundSource(String name) {
//        return this.soundSourcesMap.get(name);
//    }
    
    /**
     * Iterate available sound sources for a buffer and return it
     * returns null if no sources are available
     * @param wavfile
     */
    private SoundSource findAvailableSoundSource(String wavfile){
		List<SoundSource> sources = this.soundSourcesMap.get(wavfile);
		for (SoundSource source : sources){
			if (!source.isPlaying()){
				return source;
			}
		}
		return null;
    }
    
    public void playSoundSource(String name) {
        SoundSource soundSource = findAvailableSoundSource(name);
        if (soundSource != null && !soundSource.isPlaying()) {
            soundSource.play();
        }
    }

//    public void removeSoundSource(String name) {
//        this.soundSourceMap.remove(name);
//    }

    public void addSoundBuffer(SoundBuffer soundBuffer) {
        this.soundBufferList.add(soundBuffer);
    }

    public SoundListener getListener() {
        return this.listener;
    }

    public void setListener(SoundListener listener) {
        this.listener = listener;
    }

    public void updateListenerPosition(float x, float y) {
        // Update camera matrix with camera data
        //Transformation.updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), cameraMatrix);
        
//        listener.setPosition(camera.getPosition());
//        Vector3f at = new Vector3f();
//        cameraMatrix.positiveZ(at).negate();
//        Vector3f up = new Vector3f();
//        cameraMatrix.positiveY(up);
//        listener.setOrientation(at, up);
    	//TODO
    }
    
    public void setAttenuationModel(int model) {
        alDistanceModel(model);
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
	public void play(String name, float volume) {
//		//check that we have a source to play, otherwise system exit
//		if (alGetError() != AL_NO_ERROR) {
//			System.err.println("No available sources!, Exiting...");
//			System.exit(-1);
//		}
    	SoundSource source = findAvailableSoundSource(name);
    	if (source != null){
			source.setVolume(volume);
			source.play();
    	}
	}
    
    
    /**
     * Plays a wav file in a continous loop
     * @return A sourceID that can be used to stop a particular source, returns -1 if no available source
     */
    @Override
	public int playLoop(String name, float volume) {
    	SoundSource source = findAvailableSoundSource(name);
    	if (source != null){
			source.setVolume(volume);
			source.setLooping(true);
			source.play();
			return(source.getSourceId());
    	}
		return -1;
	}
    
    /**
     * Like play loop, but this funtion takes a sourceID instead to continue or pause a loop
     * @param sourceID, the source from which a sound will continue or be paused from playing from
     */
    @Override
    public void continueLoop(int sourceID){
    	SoundSource source = getSoundSource(sourceID);
    	if (!source.isPlaying()){
    		source.play();
    	}
    }
    
    /**
     * Like play loop, but this funtion takes a sourceID instead to continue or pause a loop
     * @param sourceID, the source from which a sound will continue or be paused from playing from
     */
    @Override
    public void pauseLoop(int sourceID){
    	SoundSource source = getSoundSource(sourceID);
    	if (source.isPlaying()){
    		source.pause();
    	}
    }
    
    /**
     * Stops a source from playing using it's id
     */
    //could still use some updating
    @Override
	public void stopLoop(int sourceID) {
    	SoundSource source = getSoundSource(sourceID);
//		for (SoundSource source : sources){
//			if (source.isPlaying()){
//				source.stop();
//				return;
//			}
//		}
    	source.stop();
	}
	
    /**
     * Given a sound source ID, this will return the sound source object
     * @param sourceID
     * @return SoundSource
     */
    private SoundSource getSoundSource(int sourceID){
    	int index = Math.floorDiv(sourceID,this.numberOfSourcesPerFile);
    	String filename = this.soundBufferMap.get(index);
    	List<SoundSource> list =  this.soundSourcesMap.get(filename);
    	for (SoundSource soundSource : list){
    		if (soundSource.getSourceId() == sourceID){
    			return soundSource;
    		}
    	}
    	return null;
    }
    
	/**
	 * Places all files in resources/audio_assets into a buffer, ready to be played
	 * @param filename
	 * @throws Exception
	 */
	private void placeFileInBuffer(String filename) throws Exception{
		SoundBuffer buffer = new SoundBuffer( System.getProperty("user.dir") + "/resources/audio_assets/"+filename);
		//add buffer to list and add name to hash map array for easy referencing
		this.soundBufferList.add(buffer);
		this.soundBufferMap.put(buffer.getBufferId(), filename);
	}
	
    public static void main(String args[]) throws Exception{
    	AudioManager soundMgr = new AudioManager();
    	
//        SoundBuffer buffBGM = new SoundBuffer(System.getProperty("user.dir") + "/resources/audio_assets/[bgm]Entombed.wav");
//        soundMgr.addSoundBuffer(buffBGM);
//        SoundSource sourceBGM = new SoundSource(true, true);
//        sourceBGM.setBuffer(buffBGM.getBufferId());
//        soundMgr.addSoundSource("[bgm]Entombed.wav", sourceBGM);
//        sourceBGM.setLooping(true);
//
//        SoundBuffer buffWalk = new SoundBuffer(System.getProperty("user.dir") + "/resources/audio_assets/footsteps_running.wav");
//        soundMgr.addSoundBuffer(buffWalk);
//        SoundSource sourceWalk = new SoundSource(true, false);
//        sourceWalk.setBuffer(buffWalk.getBufferId());
//        soundMgr.addSoundSource("footsteps_running.wav", sourceWalk);

//        Vector3f pos = particleEmitter.getBaseParticle().getPosition();
//        sourceFire.setPosition(pos);
//        sourceFire.setBuffer(buffFire.getBufferId());
//        soundMgr.addSoundSource(Sounds.FIRE.toString(), sourceFire);
//        sourceFire.play();

//        soundMgr.setListener(new SoundListener(new Vector3f(0, 0, 0)));

        //play bgm
        //sourceBGM.play();
        char c =' ' ;
        int idb = 0;
        int idw = 0;

        while (c != 'q'){
        	c = (char) System.in.read();
        	if (c=='b'){
        		idb = soundMgr.playLoop("[bgm]Desolation.wav",0.8f);
        	}
        	if (c=='n'){
        		soundMgr.pauseLoop(idb);
        	}
        	if (c=='m'){
        		soundMgr.continueLoop(idb);
        	}
        	if (c=='e'){
        		soundMgr.play("handgunshot.wav",1f);
        	}
        	if (c=='p'){
        		soundMgr.play("punch.wav",1f);
        	}
        	if (c=='w'){
        		idw = soundMgr.playLoop("footsteps_walking.wav",0.6f);
        	}
        	if (c=='s'){
        		soundMgr.pauseLoop(idw);
        	}
        	if (c=='t'){
        		soundMgr.continueLoop(idw);
        	}
        }
        soundMgr.cleanup();
    }
}
	
