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
import game.world.entity.update.AngleUpdate;
import game.world.entity.update.PositionUpdate;
import game.world.entity.update.SetHeldItem;
import game.world.entity.weapon.Knife;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a player
 *
 * @author Callum
 */
public class Player extends MovableEntity {
	/** The min distance a player can see */
	public static final float LINE_OF_SIGHT_MIN = 1.0f;
	/** The max distance a player can see */
	public static final float LINE_OF_SIGHT_MAX = 20.0f;
	/** The angle of which the player can see */
	public static final float LINE_OF_SIGHT_FOV = (float)Math.toRadians(160.0);
	
	/** The speed of the player in m/s */
	private static final float MAX_SPEED = 3.0f;
	/** The radius of the player in m */
	private static final float RADIUS = 0.2f;
	
	public static Item getDefaultHeldItem() {
		return new Knife(new Vector2f(0.0f, 0.0f));
	}
	
	/** If the player is moving north */
	private transient boolean moveNorth = false;
	/** If the player is moving south */
	private transient boolean moveSouth = false;
	/** If the player is moving east */
	private transient boolean moveEast  = false;
	/** If the player is moving west */
	private transient boolean moveWest  = false;
	
	/**The currently held item. Not necessarily a weapon */
	private Item heldItem;
	
	/** Has the player been assigned a footstep sound source? */
	private boolean soundSourceInit = false;
	private int walkingSoundID = -1; // sound source id associated with player movement
	
	private transient boolean beganUse = false;
	
	/**
	 * Constructs a new player at the specified position holding a weapon
	 * @param position The position
	 * @param _heldItem The currently held item
	 */
	public Player(int team, Vector2f position, Item _heldItem) {
		super(team, position, 1.0f);
		this.heldItem = _heldItem;
		updateHeldItemInfo();
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
		
		this.heldItem = p.heldItem.clone();
		
		this.beganUse = p.beganUse;
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
	protected float getMaxHealth() {
		return 10.0f;
	}
	
	public void setHeldItem(Item item) {
		this.heldItem = item;
	}
	
	public Item getHeldItem() {
		return heldItem;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		{
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
			if (temp.x != 0.0f && temp.y != 0.0f)
				temp.normalize();
			temp.mul(MAX_SPEED);
			
			this.addTargetVelocity(ua, temp);
			Util.popTemporaryVector2f();
		}
		
		// Update velocity
		super.update(ua);
		
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
			this.position.set(newPosition);
			ua.bank.updateEntityCached(new PositionUpdate(this.getId(), newPosition));
			//ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), new Vector2f()));
		}
		Util.popTemporaryVector2f();
		
		updateHeldItemInfo();
		if (this.heldItem != null)
			this.heldItem.update(ua);
		
		// Play walking sounds
		if (moveNorth || moveSouth || moveEast || moveWest) {
			//System.out.println("Starting sound id: " + walkingSoundID);
			if (walkingSoundID == -1){
				this.walkingSoundID = ua.audio.play("footsteps_running.wav", 0.6f,this.position);
			} else {
				ua.audio.continueLoop(this.walkingSoundID,this.position);
			}
		} else {
			//System.out.println("Stopping sound id: " + walkingSoundID);
			if (walkingSoundID != -1){
				ua.audio.pauseLoop(this.walkingSoundID);
				walkingSoundID=-1;
			}
		}
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		
		updateHeldItemInfo();
		if (this.heldItem != null)
			this.heldItem.clientUpdate(ua);
	}
	
	@Override
	public void render(IRenderer r) {
		updateHeldItemInfo();
		if (this.heldItem != null)
			this.heldItem.render(r);
		
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		Texture playerTexture = r.getTextureBank().getTexture("player_v1.png");
		r.drawTexture(playerTexture, Align.MM, position.x, position.y, RADIUS*2, RADIUS*2, angle);
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
		case AIM:
			angle = ((AimAction)a).getAngle();
			bank.updateEntityCached(new AngleUpdate(this.getId(), angle));
			break;
		case BEGIN_USE: {
			if (!this.beganUse) {
				this.beganUse = true;
				if (this.heldItem != null)
					this.heldItem.beginUse();
			}
		}
		break;
		case END_USE: {
			this.beganUse = false;
			if (this.heldItem != null)
				this.heldItem.endUse();
		}
		break;
		case PICKUP: {
			// Get pickups around to the player
			ArrayList<Entity> es = bank.getEntitiesNear(position.x, position.y, 0.5f);
			Optional<Entity> oe = es.stream()
					.filter((e) -> e instanceof Pickup)
					.min((l, r) -> Float.compare(l.position.distanceSquared(this.position), r.position.distanceSquared(this.position)));
			if (oe.isPresent()) {
				Pickup p = (Pickup) oe.get();
				
				// Drop current item
				this.dropHeldItem(bank, p.position);
				
				// Get item
				Item item = p.getItem();
				
				// Own item & pickup item
				item.setOwnerTeam(this.getTeam());
				item.setOwner(this.getId());
				this.pickupItem(bank, item);
				
				// Remove pickup entity
				bank.removeEntityCached(p.getId());
			}
			break;
		}
		}
	}
	
	private void pickupItem(EntityBank bank, Item item) {
		this.dropHeldItem(bank, this.position);
		item.setOwner(this.getId());
		item.setOwnerTeam(this.getTeam());
		bank.updateEntityCached(new SetHeldItem(this.getId(), item));
		this.heldItem = item;
	}
	
	private void dropHeldItem(EntityBank bank, Vector2f position) {
		if (this.heldItem != null)
			bank.addEntityCached(new Pickup(new Vector2f(position), this.heldItem));
		bank.updateEntityCached(new SetHeldItem(this.getId(), null));
		this.heldItem = null;
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(this.position.x, this.position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public Player clone() {
		return new Player(this);
	}
}
