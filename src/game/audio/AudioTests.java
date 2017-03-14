package game.audio;

import org.joml.Vector2f;
/**
 * Class to test the sound system. It is set up so that it takes input via system console and plays a specified sound.
 * Things this class tests:
 * 	If a sound can play
 * 	Multiple instances of one sound playing at one time
 * 	Looping function
 * @author Yean
 */
public class AudioTests {
    private static Vector2f listenerPos = new Vector2f(0, 0);

	public static void main(String args[]) throws Exception{
    	AudioManager soundMgr = new AudioManager();
    	char c =' ' ;
        int idb = 0;
        int idw = 0;
        int idw2 = 0;
		
		soundMgr.findAvailableSoundSource("pump-shotgun-shot.wav");
		
        while (c != 'q'){
        	c = (char) System.in.read();
        	if(c=='.'){
        		soundMgr.play("pump-shotgun-shot.wav",1.0f, listenerPos);
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
