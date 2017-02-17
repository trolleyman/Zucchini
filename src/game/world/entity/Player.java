package game.world.entity;

import game.ColorUtil;
import game.Util;
import game.action.Action;
import game.action.AimAction;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.EntityBank;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import game.world.update.PositionUpdate;
import game.world.update.VelocityUpdate;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a player
 *
 * @author Callum
 */
public class Player extends MovableEntity {
	/** The size of the player's line of sight */
	public static final float LINE_OF_SIGHT_MAX = 20.0f;
	
	/** The speed of the player in m/s */
	private static final float MAX_SPEED = 2.5f;
	/** The radius of the player in m */
	private static final float RADIUS = 0.2f;
	
	private boolean isMoving = false;
	/** If the player is moving north */
	private transient boolean moveNorth = false;
	/** If the player is moving south */
	private transient boolean moveSouth = false;
	/** If the player is moving east */
	private transient boolean moveEast  = false;
	/** If the player is moving west */
	private transient boolean moveWest  = false;
	
	/** Where the line of sight intersects with the map */
	private transient Vector2f lineOfSightIntersecton;
	
	/**The currently held item. Not necessarily a weapon */
	private Item heldItem;
	
	/** Has the player been assigned a footstep sound source? */
	private boolean soundSourceInit = false;
	private int walkingSoundID;//sound source id associated with player movement
	
	private transient boolean beganUse = false;
	
	/**
	 * Constructs a new player at the specified position holding a weapon
	 * @param position The position
	 * @param _heldItem The currently held item
	 */
	public Player(int team, Vector2f position, Item _heldItem) {
		super(team, position);
		this.heldItem = _heldItem;
		
		this.lineOfSightIntersecton = new Vector2f();
	}
	
	/**
	 * Clones the specified player
	 * @param p The player
	 */
	public Player(Player p) {
		super(p);
		
		this.moveNorth = p.moveNorth;
		this.moveSouth = p.moveSouth;
		this.moveEast = p.moveEast;
		this.moveWest = p.moveWest;
		
		this.heldItem = (Item) p.heldItem.clone();
		
		this.beganUse = p.beganUse;
		
		this.lineOfSightIntersecton = p.lineOfSightIntersecton;
	}
	
	@Override
	protected float getMaxHealth() {
		return 10.0f;
	}
	
	public void pickupItem(EntityBank bank, Item item) {
		if (this.heldItem != null)
			this.dropHeldItem(bank, this.position);
		
		this.heldItem = item;
	}
	
	public void dropHeldItem(EntityBank bank, Vector2f position) {
		bank.addEntityCached(new Pickup(position, this.heldItem));
		this.heldItem = null;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		// Calculate velocity
		super.update(ua);
		
		if (!soundSourceInit){
			this.walkingSoundID = ua.audio.playLoop("footsteps_running.wav", 0.6f);
			ua.audio.pauseLoop(this.walkingSoundID);
			soundSourceInit = true;
		}
		
		{
			Vector2f newVelocity = new Vector2f();
			Vector2f temp = Util.pushTemporaryVector2f();
			temp.zero();
			if (this.moveNorth)
				temp.add(0.0f, 1.0f);
			if (this.moveSouth)
				temp.add(0.0f, -1.0f);
			if (this.moveEast)
				temp.add(1.0f, 0.0f);
			if (this.moveWest)
				temp.add(-1.0f, 0.0f);
			temp.mul(MAX_SPEED);
			if (Math.abs(temp.distanceSquared(this.velocity)) > Util.EPSILON) {
				newVelocity.set(this.velocity);
				newVelocity.lerp(temp, (float) ua.dt * 8.0f);
				
				ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), newVelocity));
			}
			Util.popTemporaryVector2f();
		}
		
		//Play walking sounds
		//System.out.println(moveNorth + " " +moveSouth+ " " +moveEast+ " " +moveWest);
		if(moveNorth || moveSouth || moveEast || moveWest ){
			//System.out.println("Starting sound id: " + walkingSoundID);
			ua.audio.continueLoop(this.walkingSoundID);
		} else {
			ua.audio.pauseLoop(this.walkingSoundID);
			//System.out.println("Stopping sound id: " + walkingSoundID);
		}

//		if (this.walkingSoundID==1){
//			System.out.println("Velocity: " + velocity);
//			System.out.println("Position: " + position);
//		}
		
		// Get intersection
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
		}
		Util.popTemporaryVector2f();
		
		this.heldItem.position = this.position;
		this.heldItem.angle = this.angle;
		this.heldItem.update(ua);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		
		float x = position.x + LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
		float y = position.y + LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
		
		if (ua.map.intersectsLine(position.x, position.y, x, y, lineOfSightIntersecton) == null)
			lineOfSightIntersecton.set(x, y);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawLine(position.x, position.y, lineOfSightIntersecton.x, lineOfSightIntersecton.y, ColorUtil.RED, 1.0f);
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		Texture playerTexture = r.getImageBank().getTexture("player_v1.png");
		r.drawTexture(playerTexture, Align.MM, position.x, position.y, RADIUS*2, RADIUS*2, angle);
		
		this.heldItem.position = this.position;
		this.heldItem.angle = this.angle;
		this.heldItem.render(r);
	}
	
	/**
	 * Handles an action on the player
	 * @param bank The entity bank
	 * @param a The action
	 */
	public void handleAction(EntityBank bank, Action a) {
		switch (a.getType()) {
		case BEGIN_MOVE_NORTH: this.moveNorth = true ; break;
		case BEGIN_MOVE_SOUTH: this.moveSouth = true ; break;
		case BEGIN_MOVE_EAST : this.moveEast  = true ; break;
		case BEGIN_MOVE_WEST : this.moveWest  = true ; break;
		case END_MOVE_NORTH  : this.moveNorth = false; break;
		case END_MOVE_SOUTH  : this.moveSouth = false; break;
		case END_MOVE_EAST   : this.moveEast  = false; break;
		case END_MOVE_WEST   : this.moveWest  = false; break;
		case AIM: super.angle = ((AimAction)a).getAngle(); break;
		case BEGIN_USE: {
			if (!this.beganUse) {
				this.beganUse = true;
				this.heldItem.beginUse();
			}
		}
		break;
		case END_USE: {
			this.beganUse = false;
			this.heldItem.endUse();
		}
		break;
		case PICKUP: {
			// Get pickups around to the player
			ArrayList<Entity> es = bank.getEntitiesNear(position.x, position.y, 0.4f);
			Optional<Entity> oe = es.stream()
					.filter((e) -> e instanceof Pickup)
					.min((l, r) -> Float.compare(l.position.distanceSquared(this.position), r.position.distanceSquared(this.position)));
			if (oe.isPresent()) {
				Pickup p = (Pickup) oe.get();
				Item item = p.getItem();
				bank.removeEntityCached(p.getId());
				this.pickupItem(bank, item);
			}
			break;
		}
		}
	}
	
	@Override
	public Player clone() {
		return new Player(this);
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(this.position.x, this.position.y, RADIUS, x0, y0, x1, y1, null);
	}
}
