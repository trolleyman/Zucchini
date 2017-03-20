package game.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;

import game.action.AimAction;
import game.world.Team;
import game.world.entity.*;
import game.world.entity.update.AngleUpdate;
import org.joml.Vector2f;

import game.ColorUtil;
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
	protected boolean debug = true;    //debug messages for when ai changes states
	protected boolean debug2 = true;  // debug messages for ai during the states
	private transient double  time;
	public transient IStateMachine<AIPlayer, AIPlayerStates> stateMachine;
	
	public AIPlayer(AIPlayer ai) {
		super(ai);
		this.debug = ai.debug;
		this.stateMachine = ai.stateMachine;
		this.heldItem = ai.heldItem;
	}
	
	/**
	 * Contructs an AIPlayer at position with a default held item
	 */
	public AIPlayer(int team, Vector2f position, String name) {
		super(team, position, name);
		setup();
	}
	
	/**
	 * Contructs an AIPlayer at position with an Item
	 */
	public AIPlayer(int team, Vector2f position, String name, Item heldItem) {
		super(team, position, name, heldItem);
		setup();
	}
	
	private void setup() {
		stateMachine = new StateMachine<>(this, AIPlayerStates.MOVE_TOWARDS_CENTRE);

	}
	
	private void updateHeldItemInfo() {
		if (this.heldItem != null) {
			this.heldItem.setOwner(this.getId());
			this.heldItem.setOwnerTeam(this.getTeam());
			this.heldItem.angle = this.angle;
			
			// Calculate position
			Vector2f offset = Util.pushTemporaryVector2f();
			offset.set(Util.getDirX(angle+(float)Math.PI/2), Util.getDirY(angle+(float)Math.PI/2)).mul(0.15f);
			this.heldItem.position.set(this.position).add(offset);
			Util.popTemporaryVector2f();
		}
	}
	
	public IStateMachine<AIPlayer, AIPlayerStates> getStateMachine(){
		return stateMachine;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		if (time > 0.5){
			stateMachine.update(ua);
			time = 0.0;
		}
		
		time += ua.dt;
		
		
		super.update(ua);
	}
	
	public Entity getClosestSeenEntity(UpdateArgs ua) {
		ArrayList<Entity> entities  = ua.bank.getEntitiesNear(this.position.x, this.position.y, Player.LINE_OF_SIGHT_MAX);
		Vector2f temp = Util.pushTemporaryVector2f();
		
		// Get entities that can be seen
		ListIterator<Entity> it = entities.listIterator();
		Entity kill;
		while (it.hasNext()) {
			kill = it.next();
			if (Team.isHostileTeam(this.getTeam(), kill.getTeam())) {
				Vector2f v = ua.map.intersectsLine(this.position.x, this.position.y, kill.position.x, kill.position.y, temp);
				
				if (v == null) {
					continue;
				}
			}
			it.remove();
		}
		Util.popTemporaryVector2f();
		
		// Now get nearest entity
		Optional<Entity> closest = entities.stream().min(
				(l, r) -> Float.compare(l.position.distanceSquared(position), r.position.distanceSquared(position)));
		if (closest.isPresent()) {
			// System.out.println("Hi there, " + closest.get().getReadableName() + " :)");
			return closest.get();
		}
		return null;
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
		
		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		r.drawCircle(position.x, position.y, RADIUS, ColorUtil.BLUE);
	}
}
