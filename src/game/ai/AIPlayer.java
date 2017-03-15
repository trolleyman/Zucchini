package game.ai;

import org.joml.Vector2f;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.AutonomousPlayerEntity;
import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.entity.MovableEntity;
import game.world.entity.update.PositionUpdate;
import game.world.map.Map;
import game.world.map.PathFindingMap;

/**
 * Represents an AI player, uses a FSM to determine it's actions
 * @author George and Yean
 */
public class AIPlayer extends AutonomousPlayerEntity{
	protected boolean debug = true;    //debug messages for when ai changes states
	protected boolean debug2 = false;  // debug messages for ai during the states
	public transient IStateMachine<AIPlayer, AIPlayerStates> stateMachine;
	public Item heldItem;
	public final float RADIUS = 0.15f;
	private static final float MAX_SPEED = 1.0f;
	//for statemachine
	private boolean canSeeEnemyVar = false;
	private PathFindingMap pfmap;
	//for lag
	private int tickCounter = 0;
	
	public AIPlayer(AIPlayer ai) {
		super(ai);
		this.debug = ai.debug;
		this.stateMachine = ai.stateMachine;
		this.heldItem = ai.heldItem;
	}
	
	/**
	 * Contructs an AIPlayer at position with an Item
	 * @param team
	 * @param position
	 * @param heldItem
	 */
	public AIPlayer(int team, Vector2f position, Item heldItem) {
		//TODO: AI player is currently in monster team... not sure if this is right
		super(team, position,1.0f,MAX_SPEED, heldItem);
		this.heldItem = heldItem;
		if (this.heldItem != null)
			this.heldItem.setOwnerTeam(this.getTeam());
		updateHeldItemInfo();
		
		stateMachine = new StateMachine<AIPlayer, AIPlayerStates>(this, AIPlayerStates.MOVE_TOWARDS_CENTRE);
		
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
	public PathFindingMap getPFMap(){
		return this.pfmap;
	}
	

	@Override
	public void update(UpdateArgs ua){
		
		pfmap =  ua.map.getPathFindingMap();
		if (tickCounter > 100){
			changeCanISeeEnemy(ua);
			stateMachine.update(ua);
			tickCounter = 0;
		}
		tickCounter++;
		
		
		
		
		// Calculate intersection
		// TODO: Not DRY enough - see Player#update(UpdateArgs)
		Vector2f intersection = Util.pushTemporaryVector2f();
		if (ua.map.intersectsCircle(position.x, position.y, RADIUS, intersection) != null) {
			// Intersection with map - push out
			Vector2f newPosition = new Vector2f();
			newPosition.set(position)
					.sub(intersection)
					.normalize()
					.mul(RADIUS + Util.EPSILON)
					.add(intersection);
			ua.bank.updateEntityCached(new PositionUpdate(this.getId(), newPosition));
			//ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), new Vector2f()));
		}
		Util.popTemporaryVector2f();

		super.update(ua);
	
		
	}
	
	public boolean canSeeEnemy(){
		//TODO: make function that tells us if this entity can see an enemy
		return canSeeEnemyVar;
	}
	private void changeCanISeeEnemy(UpdateArgs ua){
		Entity closest = ua.bank.getClosestHostileEntity(position.x, position.y, this.getTeam());
		if (closest != null){
			if (closest.position.distance(this.position) < 4.0f){
				canSeeEnemyVar = true;
			}else{
				canSeeEnemyVar = false;
			}
		}
	}
	public boolean canSeePickUp(){
		//TODO: make function that can tell us if this entity can see a pickup
		//and also if it's worth picking up
		return false;
	}

	public boolean isShotAt() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected float getMaxHealth() {
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
