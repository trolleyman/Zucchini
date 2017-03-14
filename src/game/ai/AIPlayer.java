package game.ai;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.AutonomousEntity;
import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.map.PathFindingMap;
import game.world.entity.update.PositionUpdate;
import java.util.Random;
import org.joml.Vector2f;
public class AIPlayer extends AutonomousEntity {
	private static final float MAX_SPEED = 1.0f;
	private static final float RADIUS = 0.15f;
	private transient boolean soundSourceInit = false;
	private transient int zombieSoundID;
	//dont delete pls
	private int tickCounter = 0;
	private boolean canSeeEnemyVar = false;
	private Item heldItem;
	public IStateMachine<AIPlayer, AIPlayerStates> stateMachine;
	
	public boolean debug(){ 
		return true;
	}
	
	public boolean canSeeEnemy(){
		
		return canSeeEnemyVar;
		
	}
	private void changeCanISeeEnemy(UpdateArgs ua){
		Entity closest = ua.bank.getClosestHostileEntity(position.x, position.y, Team.MONSTER_TEAM);
		if (Math.abs(closest.position.distance(this.position)) < 1.0f){
			canSeeEnemyVar = true;
		}else{
			canSeeEnemyVar = false;
		}
	}
	public AIPlayer(Vector2f position, Item heldItem) {
		super(Team.MONSTER_TEAM, position, 1.0f, MAX_SPEED);
		this.heldItem = heldItem;
		if (this.heldItem != null)
			this.heldItem.setOwnerTeam(this.getTeam());
		updateHeldItemInfo();
		stateMachine = new StateMachine<AIPlayer, AIPlayerStates>(this, AIPlayerStates.WANDER);
	}
	
	public AIPlayer(AIPlayer a) {
		super(a);
	}
	public IStateMachine<AIPlayer, AIPlayerStates>  getStateMachine(){
		return this.stateMachine;
	}
	@Override
	public void update(UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		if (tickCounter > 100){
			switch(stateMachine.getCurrentState()){
			case EVADE:
			
			case MOVE_TOWARDS_CENTRE:
				this.setDestination(pfmap,new Vector2f(pfmap.height / 2,pfmap.width));
				if (canSeeEnemy()){
					stateMachine.changeState(AIPlayerStates.SHOOT_ENEMY);
				}
			case PICKUP:
				
			case SHOOT_ENEMY:
				
				Entity kill = ua.bank.getClosestHostileEntity(position.x, position.y, this.getTeam());
				if (kill == null) {
					this.setDestination(pfmap, null);
				} else {
					this.setDestination(pfmap, kill.position);
				}
				
			case WANDER:
			
			default:
				stateMachine.changeState(AIPlayerStates.MOVE_TOWARDS_CENTRE);
			
			}
			tickCounter = 0;
		}
		stateMachine.update(ua);
		
			// Update AI
			super.update(ua);
	
		tickCounter ++;
		
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
		
		if (!soundSourceInit) {
			Random rng = new Random();
			this.zombieSoundID = ua.audio.play("zombie"+(rng.nextInt(3)+1)+".wav", 1f, this.position);
			//this.walkingSoundID = ua.audio.play("footsteps_running.wav", 0.1f,this.position);
			//System.out.println("found zombie footstep sound "+walkingSoundID+" for zombie "+this.getId());
			ua.audio.pauseLoop(zombieSoundID);
			//ua.audio.pauseLoop(walkingSoundID);
			soundSourceInit = true;
		}
		
		// Play zombie sounds
		ua.audio.continueLoop(this.zombieSoundID,this.position);
		//ua.audio.continueLoop(this.walkingSoundID,this.position);
		Util.popTemporaryVector2f();
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
	
	@Override
	public void render(IRenderer r) {
		float x = position.x + 0.25f * (float) Math.sin(angle);
		float y = position.y + 0.25f * (float) Math.cos(angle);
		
		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
	}
	
	@Override
	protected float getMaxHealth() {
		return 10.0f;
	}
	
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(position.x, position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public AIPlayer clone() {
		return new AIPlayer(this);
	}
}