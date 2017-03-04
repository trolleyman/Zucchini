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
import game.world.update.AngleUpdate;
import game.world.update.PositionUpdate;
import game.world.update.SetHeldItem;
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
	
	/** The speed of the player in m/s */
	private static final float MAX_SPEED = 2.75f;
	/** The radius of the player in m */
	private static final float RADIUS = 0.2f;
	
	/** If the player is moving north */
	private transient boolean moveNorth = false;
	/** If the player is moving south */
	private transient boolean moveSouth = false;
	/** If the player is moving east */
	private transient boolean moveEast  = false;
	/** If the player is moving west */
	private transient boolean moveWest  = false;
	
	/** Where the line of sight intersects with the map */
	private transient Vector2f lineOfSightIntersecton = new Vector2f();
	
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
		super(team, position, 1.0f);
		this.heldItem = _heldItem;
		if (this.heldItem != null)
			this.heldItem.setOwnerTeam(this.getTeam());
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
		if (this.heldItem != null) {
			this.heldItem.setOwner(this.getId());
			this.heldItem.setOwnerTeam(this.getTeam());
		}
		
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
		
		if (this.heldItem != null) {
			this.heldItem.position.set(this.position);
			this.heldItem.angle = this.angle;
			this.heldItem.update(ua);
		}
		
		if (!soundSourceInit) {
			this.walkingSoundID = ua.audio.playLoop("footsteps_running.wav", 0.6f,this.position);
			ua.audio.pauseLoop(walkingSoundID);
			soundSourceInit = true;
		}
		
		// Play walking sounds
		if (moveNorth || moveSouth || moveEast || moveWest) {
			//System.out.println("Starting sound id: " + walkingSoundID);
			ua.audio.continueLoop(this.walkingSoundID,this.position);
		} else {
			//System.out.println("Stopping sound id: " + walkingSoundID);
			ua.audio.pauseLoop(this.walkingSoundID);
		} 
		
		
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
		if (lineOfSightIntersecton == null) {
			lineOfSightIntersecton = new Vector2f();
			float x = position.x + LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
			float y = position.y + LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
			lineOfSightIntersecton.set(x, y);
		}
		
		r.drawLine(position.x, position.y, lineOfSightIntersecton.x, lineOfSightIntersecton.y, ColorUtil.RED, 1.0f);
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		Texture playerTexture = r.getTextureBank().getTexture("player_v1.png");
		r.drawTexture(playerTexture, Align.MM, position.x, position.y, RADIUS*2, RADIUS*2, angle);
		
		if (this.heldItem != null) {
			this.heldItem.position.set(this.position);
			this.heldItem.angle = this.angle;
			this.heldItem.render(r);
		}
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
			ArrayList<Entity> es = bank.getEntitiesNear(position.x, position.y, 0.4f);
			Optional<Entity> oe = es.stream()
					.filter((e) -> e instanceof Pickup)
					.min((l, r) -> Float.compare(l.position.distanceSquared(this.position), r.position.distanceSquared(this.position)));
			if (oe.isPresent()) {
				Pickup p = (Pickup) oe.get();
				Item item = p.getItem();
				item.setOwnerTeam(this.getTeam());
				item.setOwner(this.getId());
				bank.removeEntityCached(p.getId());
				this.dropHeldItem(bank, p.position);
				this.pickupItem(bank, item);
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
	public Player clone() {
		return new Player(this);
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(this.position.x, this.position.y, RADIUS, x0, y0, x1, y1, null);
	}
}
