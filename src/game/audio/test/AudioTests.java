package game.audio.test;

import org.joml.Vector2f;

import game.audio.AudioManager;
/**
 * Class to test the sound system. It is set up so that it takes input via system console and plays a specified sound.
 * Things this class tests:
 * 	If a sound can play
 *  Correct sound file playing
 *  Multiple instances of sound playing at the same time
 * 	Multiple instances of one file playing at the same time
 * 	Looping function
 * 	Directional sounds
 *  Positional sounds
 * @author Yean
 */
public class AudioTests {
	/**listenerPos represents the players position in the world*/
	//in this testing, we are in (0,0). All sounds will be heard from the position (0,0) in our world
    private static Vector2f listenerPos = new Vector2f(0, 0);

	public static void main(String args[]) throws Exception{
    	AudioManager soundMgr = new AudioManager();
    	char c =' ' ;
        int id1 = 0;
        int id2 = 0;
			
        while (c != 'q'){
        	c = (char) System.in.read();
        	if(c=='.'){
        		id1 = soundMgr.play("pump-shotgun-shot.wav",1.0f, listenerPos);
        		System.out.println("Found source no " + id1 + " to play");
        	}
        	if (c=='/'){
        		id1 = soundMgr.play("handgunshot.wav",1f,listenerPos);
        		System.out.println("Found source no " + id1 + " to play");
        	}
        	if (c=='w'){
        		id2 = soundMgr.playLoop("zombie1.wav", 1f, listenerPos);
        	}
        	if (c=='s'){
        		//can only stop the most recent loop
        		System.out.println("Stopping source number "+id2);
        		soundMgr.pauseLoop(id2);
        	}
        	if (c=='d'){//tests directional sounds
        		//simulates a person running from your left to your right, works best with headphones in
        		Vector2f initialPos = new Vector2f(-4f,0f);
        		int sourceID = soundMgr.play("footsteps_running.wav", 1f, initialPos);
        		while(soundMgr.getSoundSource(sourceID).isPlaying()){
        			initialPos.x = initialPos.x + 0.35f;
        			soundMgr.updateSourcePos(sourceID, initialPos);
        			Thread.sleep(100);
        		}
        	}
        	if (c=='e'){ //plays an explosion sound on you
        		soundMgr.play("explosion.wav",1f,listenerPos);
        	}
        	if (c=='r'){ //plays an explosion sound, but much further away from you
        		soundMgr.play("explosion.wav",1f,new Vector2f(0f,15f));
        	}
        	if (c=='t'){ //plays an explosion sound, but very far away from you, shouldn't be able to hear this
        		int id = soundMgr.play("explosion.wav",1f,new Vector2f(1500f,1500f));
        		if(id!=-1 && soundMgr.getSoundSource(id).isPlaying()){
        			System.out.println("Sound source id: " + id + " is playing very far away...");
        		}
        	}
        	if (c =='m'){
        		soundMgr.mute();
        	}
        	if (c =='u'){
        		soundMgr.unMute();
        	}
        	//TODO: finish vloume tests
        	if (c == 'v'){
    			System.out.println("Type in a float between 0-1");
				Float v = 10f;
    			while (v>1f || v<0f ){
    				v = (float) System.in.read();
        			System.out.println("v: " +v );
        			if ( v<1f && v>0f){
        				soundMgr.setVolume(v);
        			}
    			}
        	}
        }
        soundMgr.cleanup();
    }
}
