package game.render;

public interface IRenderer {
	public void show();
	public void destroy();
	public boolean shouldClose();
	public void beginFrame();
	public void endFrame();
	public void drawTexture(String name, int x, int y, float rot);
}
