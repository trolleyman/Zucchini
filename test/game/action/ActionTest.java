package game.action;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @see Action
 */
public class ActionTest {
	@Test
	public void getSetType() throws Exception {
		Action a = new Action(ActionType.BEGIN_USE);
		assertEquals(ActionType.BEGIN_USE, a.getType());
		a.setType(ActionType.END_USE);
		assertEquals(ActionType.END_USE, a.getType());
	}
	
	@Test
	public void getSetAngle() {
		float delta = 0.00001f;
		AimAction a = new AimAction(1.0f);
		assertEquals(1.0f, a.getAngle(), delta);
		a.setAngle(2.0f);
		assertEquals(2.0f, a.getAngle(), delta);
	}
}