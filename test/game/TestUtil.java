package game;

import java.lang.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtil {
	public static void assertThrows(Class<? extends Throwable> clazz, RunnableThrowable f) {
		try {
			f.run();
		} catch (Throwable t) {
			assertEquals(t.getClass(), clazz);
			return;
		}
		assertTrue(false);
	}
	
	public interface RunnableThrowable {
		void run() throws Throwable;
	}
}
