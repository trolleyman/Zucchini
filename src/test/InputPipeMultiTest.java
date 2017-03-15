package test;

import game.InputHandler;
import game.InputPipeMulti;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InputPipeMultiTest {
	private InputPipeMulti input;
	private int num;
	
	@BeforeEach
	void setUp() {
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
	
	@AfterEach
	void tearDown() {
		assertEquals(3, num);
	}
	
	@Test
	void handleKey() {
		input.handleKey(1, 2, 3, 4);
	}
	
	@Test
	void handleChar() {
		input.handleChar('x');
	}
	
	@Test
	void handleCursorPos() {
		input.handleCursorPos(1.0, 2.0);
	}
	
	@Test
	void handleMouseButton() {
		input.handleMouseButton(5, 6, 7);
	}
	
	@Test
	void handleScroll() {
		input.handleScroll(3.0, 4.0);
	}
	
	@Test
	void handleResize() {
		input.handleResize(100, 200);
	}
}