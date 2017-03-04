package game.world.entity.weapon;

import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class RocketLauncher extends Weapon {
	
	private static final Vector4f COLOR = new Vector4f(0.0f, 0.4f, 0.0f, 1.0f);
	
	public RocketLauncher(RocketLauncher rl) {
		super(rl);
	}
	
	public RocketLauncher(Vector2f position) {
		super(position, true, 0.0f, 1, 5.0f);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.1f, 0.5f, COLOR, this.angle);
	}
	
	@Override
	protected void fire(UpdateArgs ua) {
		System.out.println("Whoosh! Rocket fired!");
		ua.audio.play("rocket-launcher.wav", 0.5f, this.position);
		ua.bank.addEntityCached(new Rocket(position, this.ownerTeam, this.angle));
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		System.out.println("Reloading rocket launcher...");
		int audioID = ua.audio.play("rocket_reload.wav", 1.0f, this.position);
		ua.audio.updateSourcePos(audioID, this.position); //TODO: DOESNT work since this is only called once
	}
	
	@Override
	public RocketLauncher clone() {
		return new RocketLauncher(this);
	}
}
