package game;

import game.render.IRenderer;
import game.ui.UI;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClientTest {
	private class TestUI extends UI {
		private boolean updated = false;
		private boolean rendered = false;
		private boolean next = false;
		private boolean destroyed = false;
		
		public TestUI(UI ui) {
			super(ui);
		}
		
		@Override
		public void update(double dt) {
			updated = true;
		}
		
		@Override
		public void render(IRenderer r) {
			rendered = true;
		}
		
		@Override
		public UI next() {
			if (!next) {
				next = true;
				return this;
			}
			return null;
		}
		
		@Override
		public void destroy() {
			destroyed = true;
		}
		
		public boolean hasPassed() {
			return updated && rendered && next && destroyed;
		}
	}
	
	@Test
	public void testClient() {
		Client c = new Client(false);
		TestUI testUI = new TestUI(c.getUI());
		c.setUI(testUI);
		c.run();
		assertTrue(testUI.hasPassed());
	}
}
