package game;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class InputPipeMultiTest {
	private InputPipeMulti input;
	private int num;
	
	@Before
	public void setUp() {
		InputHandler output = new InputHandler() {
			@Override
			public void handleKey(int key, int scancode, int action, int mods) {
				num++;
			}
			
			@Override
			public void handleChar(char c) {
				num++;
			}
			
			@Override
			public void handleCursorPos(double xpos, double ypos) {
				num++;
			}
			
			@Override
			public void handleMouseButton(int button, int action, int mods) {
				num++;
			}
			
			@Override
			public void handleScroll(double xoffset, double yoffset) {
				num++;
			}
			
			@Override
			public void handleResize(int w, int h) {
				num++;
			}
		};
		
		ArrayList<InputHandler> ihs = new ArrayList<>();
		ihs.add(output);
		ihs.add(output);
		ihs.add(output);
		input = () -> ihs;
	}
	
	@After
	public void tearDown() {
		assertEquals(3, num);
	}
	
	@Test
	public void handleKey() {
		input.handleKey(1, 2, 3, 4);
	}
	
	@Test
	public void handleChar() {
		input.handleChar('x');
	}
	
	@Test
	public void handleCursorPos() {
		input.handleCursorPos(1.0, 2.0);
	}
	
	@Test
	public void handleMouseButton() {
		input.handleMouseButton(5, 6, 7);
	}
	
	@Test
	public void handleScroll() {
		input.handleScroll(3.0, 4.0);
	}
	
	@Test
	public void handleResize() {
		input.handleResize(100, 200);
	}
}