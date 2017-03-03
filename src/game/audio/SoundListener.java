package game.audio;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import static org.lwjgl.openal.AL10.*;


public class SoundListener {
	public static final float zPos = 0.0f;
	public Vector2f playerPos;
	
	public SoundListener() {
        this(new Vector2f(0, 0));
        this.playerPos = new Vector2f(0, 0);
    }

	/**
	 * Represents a listener, or a player in other words
	 * @param position, the postion of a player
	 */
    public SoundListener(Vector2f position) {
        alListener3f(AL_POSITION, position.x, position.y, zPos);
        alListener3f(AL_VELOCITY, 0, 0, 0);
        this.playerPos = position;
    }
    
    public void setSpeed(Vector3f speed) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setPosition(Vector2f position) {
        alListener3f(AL_POSITION, position.x, position.y, zPos  );
        this.playerPos = position;
    }
    
    public Vector2f getPos(){
    	return this.playerPos;
    }
    /*
     * OpenAL will not update their position for you. 
     * It will use the relative velocity to calculate the 
     * Doppler effect, but the positions won’t be modified.
     * So, if you want to simulate a moving source or listener 
     * you must take care of updating their positions in the game
     * loop.
     */

    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        alListenerfv(AL_ORIENTATION, data);
    }
}
