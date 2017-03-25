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

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents an AI player, uses a FSM to determine it's actions
 * @author George 
 */
public class AIPlayer extends Player {
	private static final Random rng = new Random(System.currentTimeMillis() + System.nanoTime() + new Random().nextLong());
	private static final String[] names = new String[] {
			"WALL-E",
			"HAL_9000",
			"AUTO",
			"GERTY",
			"ODIN",
			"Skynet",
			"MEDUSA",
			"UNIVAC",
			"AM",
			"Deep_Thgt",
			"Colossus",
			"WOPR",
			"JARVIS",
			"Mr_Smith",
			"SHODAN",
			"GLaDOS",
			"John_Henry_Eden",
			"Yes_Man",
			"DCPU-16",
			"TIS-100",
	};
	/**
	 * returns a random AI name 
	 * @return randomName
	 */
	public static String generateRandomName() {
		// Get random name from list
		int i = rng.nextInt(names.length);
		return names[i];
	}
	
	public boolean debug = false;    //debug messages for when ai changes states
	public boolean debug2 = false;  // debug messages for ai during the states
	private transient double time;
	public transient IStateMachine<AIPlayer, State<AIPlayer>> stateMachine;
	
	private Difficulty difficulty;
	/**
	 * Create a clone of the AIPlayer
	 * @param ai the clone
	 */
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
	 * @param team 
	 * @param position 
	 * @param name
	 * @param difficulty
	 */
	public AIPlayer(int team, Vector2f position, String name, Difficulty difficulty) {
		super(team, position, name);
		setup(difficulty);
	}
	

	/**
	 * Contructs an AIPlayer at position with an Item
	 * @param team
	 * @param position
	 * @param name
	 * @param heldItem
	 * @param difficulty
	 */
	public AIPlayer(int team, Vector2f position, String name, Item heldItem, Difficulty difficulty) {
		super(team, position, name, heldItem);
		setup(difficulty);
	}
	/**
	 * sets up statemachine and difficulty of the AI Player
	 * @param difficulty of the AIPlayer
	 */
	private void setup(Difficulty difficulty) {
		stateMachine = new StateMachine<>(this, new WanderState());
		this.difficulty = difficulty;
	}
	/**
	 * updates the item that the AIPlayer holds
	 */
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
	/**
	 * gets the statemachine
	 * @return the statemachine of the ai player
	 */
	public IStateMachine<AIPlayer, State<AIPlayer>> getStateMachine(){
		return stateMachine;
	}
	/**
	 * Update the statemachine as well as the super class
	 * @param ua the update arguments
	 */
	@Override
	public void update(UpdateArgs ua) {
		stateMachine.update(ua);
		
		super.update(ua);
	}
	/**
	 * gets the closest visible pick up that is worth picking up
	 * @param ua
	 * @return the closest valuable visible pickup
	 */
	public Pickup getClosestValublePickup(UpdateArgs ua){
		Vector2f temp = Util.pushTemporaryVector2f();
		Pickup i = (Pickup)ua.bank.getClosestEntity(position.x, position.y,
				(e) ->  ua.map.intersectsLine(this.position.x, this.position.y, e.position.x, e.position.y, temp) == null
				&& e instanceof Pickup
				&& ( ( (Pickup)e).getItem().aiValue() > this.heldItem.aiValue() || (this.heldItem.isUseless() && ((Pickup)e).getItem().aiValue() > 0 ))
						&& !((Pickup)e).getItem().isUseless() );
		Util.popTemporaryVector2f();
		return i;
	}
	
	/**
	 * gets the closest visible entity from the AI player
	 * @param ua 
	 * @return the closest visible entity from the AI player
	 */
	public Entity getClosestSeenEntity(UpdateArgs ua) {
		Vector2f temp = Util.pushTemporaryVector2f();
		Entity ret = ua.bank.getClosestEntity(position.x, position.y,
				(e) -> Team.isHostileTeam(this.getTeam(), e.getTeam())
				&& ua.map.intersectsLine(this.position.x, this.position.y, e.position.x, e.position.y, temp) == null);
		Util.popTemporaryVector2f();
		return ret;
	}
	
	/**
	 * Returns the actual angle that the AI will be set to, given a desired angle.
	 * The angle returned will depend on the difficulty of the AI.
	 * @param desiredAngle The target angle
	 * @param dt See {@link UpdateArgs#dt}
	 */
	public float getNewAngle(float desiredAngle, double dt) {
		float target = desiredAngle + ((float)Math.random() - 0.5f) * difficulty.getDeviation();
		
		float da = Util.getAngleDiff(this.angle, target);
		return this.angle + (da * (float) dt / difficulty.getTurningRate());
	}
	
	/**
	 * returns the max health
	 * @return max health
	 */
	@Override
	public float getMaxHealth() {
		return 10.0f;
	}
	/**
	 * clones the AI player
	 * @return a clone of this player
	 */
	@Override
	public AIPlayer clone() {
		return new AIPlayer(this);
	}
	/**
	 * checks if two connecting points are intersected by a wall
	 * @param x0 x coordinate of point 1
	 * @param y0 y coordinate of point 1
	 * @param x1 x coordinate of point 2
	 * @param y1 y coordinate of point 2
	 */
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(position.x, position.y, RADIUS, x0, y0, x1, y1, null);
	}
	/**
	 * render the AIPlayer
	 * @param r used to render images
	 * @param map the map of used for the game
	 */
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
