package game.render.shader;

import game.Util;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.*;

/**
 * Used to render a point light with a color, position, attenuation factor, cone angle and cone direction.
 *
 * @author Callum
 */
public class SpotlightShader extends PointLightShader {
	private int coneAngleMinUniform;
	private int coneAngleMaxUniform;
	private int coneDirectionUniform;
	
	public SpotlightShader() {
		super("spotlight");
		
		coneAngleMinUniform = getUniformLocation("coneAngleMin");
		coneAngleMaxUniform = getUniformLocation("coneAngleMax");
		coneDirectionUniform = getUniformLocation("coneDirection");
	}
	
	public void setConeAngleMin(float angle) {
		glUniform1f(coneAngleMinUniform, angle);
	}
	
	public void setConeAngleMax(float angle) {
		glUniform1f(coneAngleMaxUniform, angle);
	}
	
	public void setConeDirection(float x, float y) {
		// Ensure that the direction is normalized
		Vector2f temp = Util.pushTemporaryVector2f().set(x, y).normalize();
		glUniform2f(coneDirectionUniform, temp.x, temp.y);
		Util.popTemporaryVector2f();
	}
}
