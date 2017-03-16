package game.audio;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import static org.lwjgl.openal.AL10.*;

/**
 * Sound listeners represent an entity in the world space that can hear sounds. These will be the players in our game.
 * @author Yean
 *
 */
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
    
    /**
     * Sets the speed of the listener
     * @param speed
     */
    public void setSpeed(Vector3f speed) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    /**
     * Sets the position of the listener
     * @param position
     */
    public void setPosition(Vector2f position) {
        alListener3f(AL_POSITION, position.x, position.y, zPos  );
        this.playerPos = position;
    }
    
    /**
     * Gets the position of the listener
     * @return pos
     */
    public Vector2f getPos(){
    	return this.playerPos;
    }

    /**
     * Can be used to set the orientation of a player
     * @param at
     * @param up
     */
    //most likely won't be used as we are making a 2d game with a set camera orientation, but this can remain to test out if we want to
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
