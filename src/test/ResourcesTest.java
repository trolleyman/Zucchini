package test;

import game.Resources;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.*;

class ResourcesTest {
	@Test
	void getFiles() {
		HashMap<String, byte[]> res = Resources.getFiles("img");
		assertTrue(res.size() > 0);
		res = Resources.getFiles("img", (s) -> s.endsWith(".png"));
		assertTrue(res.size() > 0);
		for (Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".png"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	void getImages() {
		HashMap<String, byte[]> res = Resources.getImages();
		assertTrue(res.size() > 0);
		for (Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".png"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	void getFonts() {
		HashMap<String, byte[]> res = Resources.getFonts();
		assertTrue(res.size() > 0);
		for (Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".ttf"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	void getAudioFiles() {
		HashMap<String, byte[]> res = Resources.getAudioFiles();
		assertTrue(res.size() > 0);
		for (Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".wav"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	void getShaders() {
		HashMap<String, byte[]> res = Resources.getShaders();
		assertTrue(res.size() > 0);
		boolean vert = false;
		boolean frag = false;
		for (Entry<String, byte[]> e : res.entrySet()) {
			if (e.getKey().endsWith(".vert"))
				vert = true;
			if (e.getKey().endsWith(".frag"))
				frag = true;
		}
		
		assertTrue(vert);
		assertTrue(frag);
	}
}