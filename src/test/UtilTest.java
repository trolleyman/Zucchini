package test;

import game.Util;
import game.render.Align;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {
	private FloatBuffer buf1;
	private FloatBuffer buf2;
	
	@BeforeEach
	void setUp() {
		buf1 = MemoryUtil.memAllocFloat(1024);
		buf2 = MemoryUtil.memAllocFloat(1024);
	}
	
	@AfterEach
	void tearDown() {
		MemoryUtil.memFree(buf1);
		MemoryUtil.memFree(buf2);
	}
	
	private void testSort(float[] arr) {
		float[] sarr = arr.clone();
		// Test against library sort
		Arrays.sort(sarr);
		buf2.put(sarr);
		buf2.flip();
		
		buf1.put(arr);
		buf1.flip();
		Util.sortFloatBuffer(buf1);
		
		assertEquals(buf2, buf1);
	}
	
	@Test
	void isPointInRect() {
		float rx = 10.0f;
		float ry = 20.0f;
		float rw = 30.0f;
		float rh = 40.0f;
		
		assertEquals(false, Util.isPointInRect(0.0f, 0.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(11.0f, 0.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(0.0f, 21.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(true , Util.isPointInRect(11.0f, 21.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(11.0f, 61.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(41.0f, 21.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(41.0f, 61.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(11.0f, 61.0f, Align.BL, rx, ry, rw, rh));
		assertEquals(true , Util.isPointInRect(11.0f, 21.0f, Align.BL, rx, ry, rw, rh));
		
		// Check alignment works
		float x = 0.5f;
		float y = 0.5f;
		rx = 0.0f;
		ry = 0.0f;
		rw = 1.0f;
		rh = 1.0f;
		// TR
		assertEquals(true , Util.isPointInRect(x, y, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(x, y, Align.TL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(x, y, Align.BR, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(x, y, Align.TR, rx, ry, rw, rh));
		
		// BR
		assertEquals(false, Util.isPointInRect(x, -y, Align.BL, rx, ry, rw, rh));
		assertEquals(true , Util.isPointInRect(x, -y, Align.TL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(x, -y, Align.BR, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(x, -y, Align.TR, rx, ry, rw, rh));
		
		// TL
		assertEquals(false, Util.isPointInRect(-x, y, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(-x, y, Align.TL, rx, ry, rw, rh));
		assertEquals(true , Util.isPointInRect(-x, y, Align.BR, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(-x, y, Align.TR, rx, ry, rw, rh));
		
		// BL
		assertEquals(false, Util.isPointInRect(-x, -y, Align.BL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(-x, -y, Align.TL, rx, ry, rw, rh));
		assertEquals(false, Util.isPointInRect(-x, -y, Align.BR, rx, ry, rw, rh));
		assertEquals(true , Util.isPointInRect(-x, -y, Align.TR, rx, ry, rw, rh));
	}
	
	@Test
	void isValidName() {
		assertEquals(false, Util.isValidName(null));
		assertEquals(false, Util.isValidName(""));
		assertEquals(false, Util.isValidName("d"));
		assertEquals(false, Util.isValidName("da"));
		assertEquals(false, Util.isValidName("CAPS"));
		assertEquals(false, Util.isValidName("toooooooooooolooooooong"));
		assertEquals(true , Util.isValidName("username"));
		assertEquals(false, Util.isValidName("spaces bad"));
	}
	
	@Test
	void isValidLobbyName() {
		assertEquals(false, Util.isValidLobbyName(null));
		assertEquals(false, Util.isValidLobbyName(""));
		assertEquals(false, Util.isValidLobbyName("d"));
		assertEquals(false, Util.isValidLobbyName("da"));
		assertEquals(false, Util.isValidLobbyName("CAPS"));
		assertEquals(false, Util.isValidLobbyName("tooooooooooooooooloooooooooooooong"));
		assertEquals(true , Util.isValidLobbyName("username"));
		assertEquals(true , Util.isValidLobbyName("spaces bad"));
	}
	
	@Test
	void sortFloatBufferShort() {
		testSort(new float[] {0.5f, -1.0f, 4.0f, 10.0f, -20.0f, 41.0f, 32.0f, 23.0f, 13.0f});
	}
	
	@Test
	void sortFloatBufferRand() {
		float[] arr = new float[256];
		for (int i = 0; i < 256; i++) {
			arr[i] = (float)Math.random() * 10.0f;
		}
		testSort(arr);
	}
	
	@Test
	void sortFloatBufferInts() {
		float[] arr = new float[256];
		for (int i = 0; i < 256; i++) {
			arr[i] = (int) ((float)Math.random() * 10.0f);
		}
		testSort(arr);
	}
	
	@Test
	void removeSimilarFloats() {
		float diff = 0.12f;
		buf1.put(new float[] {0.0f, 0.0f, 0.1f, 0.1f, 0.11f, 0.15f, 0.2f, 0.3f, 0.45f, 0.7f, 0.9f, 4.0f, 4.0f, 4.9f});
		buf1.flip();
		buf2.put(new float[] {0.0f, 0.15f, 0.3f, 0.45f, 0.7f, 0.9f, 4.0f, 4.9f});
		buf2.flip();
		Util.removeSimilarFloats(buf1, diff);
		
		assertEquals(buf2, buf1);
	}
	
	@Test
	void reverseFloatBuffer() {
		float[] arr1 = new float[] {1.0f, -1.0f, 5.0f, 4.0f, 2.0f, 10.0f, -100.0f};
		float[] arr2 = new float[] {-100.0f, 10.0f, 2.0f, 4.0f, 5.0f, -1.0f, 1.0f};
		buf1.put(arr1);
		buf1.flip();
		buf2.put(arr2);
		buf2.flip();
		Util.reverseFloatBuffer(buf1);
		assertEquals(buf1, buf2);
	}
}
