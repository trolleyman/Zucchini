package game.action;

public class AimAction extends Action {
	
	/**
	 * The angle in the same format at Entity.angle.
	 */
	private float angle;
	
	public AimAction(float _angle) {
		super(ActionType.AIM);
		
		this.angle = _angle;
	}
	
	public float getAngle() {
		return angle;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + angle;
	}
}
