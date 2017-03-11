package game.audio;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import game.Util;
import org.joml.Vector2f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

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
        HashMap<String, byte[]> audioFiles = Util.getAudioFiles();
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
				if(filename.equals("explosion.wav") || filename.equals("handgunshot.wav")){
					source.setRolloffFactor(1f);
					source.setReferenceDistance(1f);
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
		fileSourceMap.put("bullet_impact_wall.wav", 30);
		fileSourceMap.put("bullet_whiz1.wav", 0);
		fileSourceMap.put("bullet_whizz_silent.wav", 30);
		fileSourceMap.put("bullet_whizz2.wav", 0);
		fileSourceMap.put("bullet_whizz3.wav", 0);
		fileSourceMap.put("explosion.wav", 5);
		fileSourceMap.put("footsteps_running.wav", 30);
		fileSourceMap.put("footsteps_walking.wav", 0);
		fileSourceMap.put("grunt1.wav", 5);
		fileSourceMap.put("grunt2.wav", 5);
		fileSourceMap.put("grunt3.wav", 5);
		fileSourceMap.put("grunt4.wav", 5);
		fileSourceMap.put("gun_reload[2sec].wav", 5);
		fileSourceMap.put("handgunshot.wav", 30);
		fileSourceMap.put("laser_round.wav", 15);
		fileSourceMap.put("lasergun-fire.wav", 15);
		fileSourceMap.put("punch.wav", 5);
		fileSourceMap.put("rocket_reload[5sec].wav", 5);
		fileSourceMap.put("rocket-launcher.wav", 5);
		fileSourceMap.put("zombie1.wav", 5);
		fileSourceMap.put("zombie2.wav", 5);
		fileSourceMap.put("zombie3.wav", 5);
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
    public SoundSource findAvailableSoundSource(String wavfile){
		List<SoundSource> sources = this.soundSourcesMap.get(wavfile);
		for (SoundSource source : sources){
			if (!source.isPlaying()){
				return source;
			}
		}
		return null;
    }
    
    public int findAvailableSoundSourceID(String wavfile){
		List<SoundSource> sources = this.soundSourcesMap.get(wavfile);
		for (SoundSource source : sources){
			if (!source.isPlaying()){
				return source.getSourceId();
			}
		}
		return -1;
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
    	//source.setVolume(1f);
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
    		//source.setInUse(false);
    	}
    	//source.setVolume(0f);
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
    	source.setInUse(false);
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
	
    public static void main(String args[]) throws Exception{
    	AudioManager soundMgr = new AudioManager();
    	char c =' ' ;
        int idb = 0;
        int idw = 0;
        int idw2 = 0;
        
		int idw3 = soundMgr.playLoop("footsteps_running.wav",0.6f,listenerPos);
		System.out.println("idw3: "+idw3);
		soundMgr.pauseLoop(idw3);
		int idw4 = soundMgr.playLoop("footsteps_running.wav",0.6f,listenerPos);
		System.out.println("idw4: "+idw4);
		soundMgr.pauseLoop(idw4);
		int idw5 = soundMgr.playLoop("footsteps_running.wav",0.6f,listenerPos);
		System.out.println("idw5: "+idw5);
		soundMgr.pauseLoop(idw5);
		int idw6 = soundMgr.playLoop("footsteps_running.wav",0.6f,listenerPos);
		System.out.println("idw6: "+idw6);
		soundMgr.pauseLoop(idw6);
		int idw7 = soundMgr.playLoop("footsteps_running.wav",0f,listenerPos);
		System.out.println("idw7: "+idw7);
		soundMgr.pauseLoop(idw7);
		
        while (c != 'q'){
        	c = (char) System.in.read();
        	if (c=='1'){
        		System.out.println("is idw3 playing?: "+ soundMgr.getSoundSource(idw3).isPlaying());
        		if((soundMgr.getSoundSource(idw3).isPlaying())){
        			soundMgr.pauseLoop(idw3);
        			System.out.println("should pause idw3");
        		}else{
        			soundMgr.continueLoop(idw3, listenerPos);
        		}
        	}
        	if (c=='2'){
        		System.out.println("is idw4 playing?: "+ soundMgr.getSoundSource(idw4).isPlaying());
        		if((soundMgr.getSoundSource(idw4).isPlaying())){
        			soundMgr.pauseLoop(idw4);
        			System.out.println("should pause idw4");
        		}else{
        			soundMgr.continueLoop(idw4, listenerPos);
        		}
        	}
        	if (c=='3'){
        		System.out.println("is idw3 playing?: "+ soundMgr.getSoundSource(idw5).isPlaying());
        		if((soundMgr.getSoundSource(idw5).isPlaying())){
        			soundMgr.pauseLoop(idw5);
        			System.out.println("should pause idw3");
        		}else{
        			soundMgr.continueLoop(idw5, listenerPos);
        		}
        	}
        	if (c=='4'){
        		System.out.println("is idw3 playing?: "+ soundMgr.getSoundSource(idw6).isPlaying());
        		if((soundMgr.getSoundSource(idw6).isPlaying())){
        			soundMgr.pauseLoop(idw6);
        			System.out.println("should pause idw3");
        		}else{
        			soundMgr.continueLoop(idw6, listenerPos);
        		}
        	}
        	if (c=='5'){
        		System.out.println("is idw3 playing?: "+ soundMgr.getSoundSource(idw7).isPlaying());
        		if((soundMgr.getSoundSource(idw7).isPlaying())){
        			soundMgr.pauseLoop(idw7);
        			System.out.println("should pause idw3");
        		}else{
        			soundMgr.continueLoop(idw7, listenerPos);
        		}
        	}
        	if (c=='b'){
        		idb = soundMgr.playLoop("[bgm]Desolation.wav",0.8f,listenerPos);
        	}
        	if (c=='n'){
        		soundMgr.pauseLoop(idb);
        	}
        	if (c=='m'){
        		soundMgr.continueLoop(idb,listenerPos);
        	}
        	if (c=='e'){
        		soundMgr.play("handgunshot.wav",1f,listenerPos);
        	}
        	if (c=='p'){
        		soundMgr.play("punch.wav",1f,listenerPos);
        	}
        	if (c=='w'){
        		idw = soundMgr.playLoop("footsteps_walking.wav",0.6f,listenerPos);
        	}
        	if (c=='s'){
        		soundMgr.pauseLoop(idw);
        	}
        	if (c=='t'){
        		soundMgr.continueLoop(idw,listenerPos);
        	}
        	if (c=='r'){
        		idw2 = soundMgr.playLoop("footsteps_walking.wav",0.6f,listenerPos);
        		System.out.println("idw2: "+idw2);
        	}
        	if (c=='f'){
        		soundMgr.pauseLoop(idw2);
        	}
        	if (c=='b'){
        		soundMgr.stopLoop(idw2);
        	}
        	if (c=='v'){
        		soundMgr.continueLoop(idw2,listenerPos);
        	}
        	if (c=='l'){
        		System.out.println(soundMgr.findAvailableSoundSourceID("footsteps_walking.wav"));
        	}
        }
        soundMgr.cleanup();
    }
	
	
}