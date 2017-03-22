package game.ai;

import game.ai.state.WanderState;
import game.render.Align;
import game.world.Team;
import game.world.entity.*;
import org.joml.Vector2f;

import game.Util;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import game.world.map.Map;

/**
 * Represents an AI player, uses a FSM to determine it's actions
 * @author George and Yean
 */
public class AIPlayer extends Player {
	public boolean debug = false;    //debug messages for when ai changes states
	public boolean debug2 = false;  // debug messages for ai during the states
	private transient double time;
	public transient IStateMachine<AIPlayer, State<AIPlayer>> stateMachine;
	private Difficulty difficulty;
	
	public AIPlayer(AIPlayer ai) {
		super(ai);
		this.debug = ai.debug;
		this.stateMachine = ai.stateMachine;
		if (this.heldItem != null)
			this.heldItem = ai.heldItem.clone();
		this.difficulty = ai.difficulty;
	}
	
	/**
	 * Contructs an AIPlayer at position with a default held item
	 */
	public AIPlayer(int team, Vector2f position, String name, Difficulty difficulty) {
		super(team, position, name);
		setup(difficulty);
	}
	
	/**
	 * Contructs an AIPlayer at position with an Item
	 */
	public AIPlayer(int team, Vector2f position, String name, Item heldItem, Difficulty difficulty) {
		super(team, position, name, heldItem);
		setup(difficulty);
	}
	
	private void setup(Difficulty difficulty) {
		stateMachine = new StateMachine<>(this, new WanderState());
		this.difficulty = difficulty;
	}
	
	private void updateHeldItemInfo() {
		if (this.heldItem != null) {
			this.heldItem.setOwner(this);
			this.heldItem.angle = this.angle;
			
			// Calculate position
			Vector2f offset = Util.pushTemporaryVector2f();
			offset.set(Util.getDirX(angle+(float)Math.PI/2), Util.getDirY(angle+(float)Math.PI/2)).mul(0.15f);
			this.heldItem.position.set(this.position).add(offset);
			Util.popTemporaryVector2f();
		}
	}
	
	public IStateMachine<AIPlayer, State<AIPlayer>> getStateMachine(){
		return stateMachine;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		stateMachine.update(ua);		
		
		super.update(ua);
	}
	
	public Pickup getClosestValublePickup(UpdateArgs ua){
		Vector2f temp = Util.pushTemporaryVector2f();
		Pickup i = (Pickup)ua.bank.getClosestEntity(position.x, position.y,
				(e) ->  ua.map.intersectsLine(this.position.x, this.position.y, e.position.x, e.position.y, temp) == null
				&& e instanceof Pickup
				&& ((Pickup)e).getItem().aiValue() > this.heldItem.aiValue());
		Util.popTemporaryVector2f();
		return i;
	}
	
	public Entity getClosestSeenEntity(UpdateArgs ua) {
		Vector2f temp = Util.pushTemporaryVector2f();
		Entity ret = ua.bank.getClosestEntity(position.x, position.y,
				(e) -> Team.isHostileTeam(this.getTeam(), e.getTeam())
				&& ua.map.intersectsLine(this.position.x, this.position.y, e.position.x, e.position.y, temp) == null);
		Util.popTemporaryVector2f();
		return ret;
	}
	
	public boolean canSeePickUp(){
		//TODO: make function that can tell us if this entity can see a pickup
		// and also if it's worth picking up
		return false;
	}

	public boolean isShotAt() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public float getMaxHealth() {
		return 10.0f;
	}

	@Override
	public AIPlayer clone() {
		return new AIPlayer(this);
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(position.x, position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		updateHeldItemInfo();
		if (this.heldItem != null)
			this.heldItem.render(r,map);
		
		float x = position.x + 0.25f * (float) Math.sin(angle);
		float y = position.y + 0.25f * (float) Math.cos(angle);
		
//		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
//		r.drawCircle(position.x, position.y, RADIUS, ColorUtil.BLUE);
		r.drawTexture(r.getTextureBank().getTexture("ai_player_v1.png"), Align.MM, position.x, position.y, RADIUS*2, RADIUS*2, angle);
	}
}
