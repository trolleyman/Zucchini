package game.audio;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.*;


public class SoundListener {
	public SoundListener() {
        this(new Vector3f(0, 0, 0));
    }

    public SoundListener(Vector3f position) {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);

    }

    public void setSpeed(Vector3f speed) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setPosition(Vector3f position) {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
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
