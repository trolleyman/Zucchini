package game.action;

/**
 * The class for {@link game.action.ActionType#AIM ActionType.AIM}
 * 
 * @author Callum
 */
public class AimAction extends Action {
	/**
	 * The angle in the same format as {@link game.world.entity.Entity#angle Entity.angle}.
	 */
	private float angle;
	
	/**
	 * Constructs an {@link game.action.ActionType#AIM AIM} action.
	 * @param angle angle in the same format as {@link game.world.entity.Entity#angle Entity.angle}.
	 */
	public AimAction(float _angle) {
		super(ActionType.AIM);
		
		this.angle = _angle;
	}
	
	/**
	 * Returns an angle in the same format as {@link game.world.entity.Entity#angle Entity.angle}.
	 * @return The angle
	 */
	public float getAngle() {
		return angle;
	}
	
	/**
	 * Sets the angle for this aim action
	 * @param _angle The new angle
	 */
	public void setAngle(float _angle) {
		this.angle = _angle;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + angle;
	}
}
