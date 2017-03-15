package test;

import game.InputHandler;
import game.InputPipe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InputPipeTest {
	private InputPipe input;
	private InputHandler output;
	private int num;
	
	@Before
	public void setUp() {
		num = 0;
		input = () -> output;
		output = new InputHandler() {
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
	}
	
	@After
	public void tearDown() {
		assertEquals(1, num);
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