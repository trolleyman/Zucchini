package game.world.entity;

import game.Util;
import game.action.Action;
import game.action.AimAction;
import game.net.Protocol;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.EntityBank;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
import game.world.entity.light.PointLight;
import game.world.entity.light.Spotlight;
import game.world.entity.update.AngleUpdate;
import game.world.entity.update.PositionUpdate;
import game.world.entity.update.HeldItemUpdate;
import game.world.entity.update.TorchLightUpdate;
import game.world.entity.weapon.Knife;
import game.world.entity.weapon.Weapon;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a player
 *
 * @author Callum
 */
public class Player extends MovableEntity {
	/** The max distance a player can see */
	public static final float LINE_OF_SIGHT_MAX = 50.0f;
	/** The angle of which the player can see */
	public static final float LINE_OF_SIGHT_FOV = (float)Math.toRadians(360.0);
	
	private static final Vector4f SPOT_COLOR = LightUtil.LIGHT_DIRECT_SUNLIGHT_6000;
	private static final Vector4f TORCH_COLOR = LightUtil.LIGHT_DIRECT_SUNLIGHT_6000;
	
	/** The speed of the player in m/s */
	private static final float MAX_SPEED = 4.0f;
	/** The radius of the player in m */
	private static final float RADIUS = 0.2f;
	/** True if the torch is on */
	private boolean torchOn = true;
	
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
	
	/** The name of the player */
	private String name;
	
	/** The currently held item. Not necessarily a weapon */
	private Item heldItem;
	
	private PointLight pointLight;
	/** The torch of the player */
	private Spotlight torch;
	
	/** Has the player been assigned a footstep sound source? */
	private transient int walkingSoundID = -1; // sound source id associated with player movement
	
	private transient boolean beganUse = false;
	
	/**
	 * Constructs a new player at the specified position holding a weapon
	 * @param position The position
	 * @param _name The name of the player
	 * @param _heldItem The currently held item
	 */
	public Player(int team, Vector2f position, String _name, Item _heldItem) {
		super(team, position, 1.0f);
		this.name = _name;
		this.heldItem = _heldItem;
		this.pointLight = new PointLight(
				new Vector2f(this.position),
				new Vector4f(SPOT_COLOR.x, SPOT_COLOR.y, SPOT_COLOR.z, 0.4f),
				3.0f, true);
		this.torch = new Spotlight(
				new Vector2f(position),
				new Vector4f(TORCH_COLOR.x, TORCH_COLOR.y, TORCH_COLOR.z, 1.0f),
				0.01f, true, (float) Math.toRadians(30.0f), (float) Math.toRadians(60.0f));
		updateChildrenInfo();
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
		
		this.name = p.name;
		this.heldItem = p.heldItem.clone();
		
		this.beganUse = p.beganUse;
		
		this.pointLight = p.pointLight.clone();
		this.torch = p.torch.clone();
	}
	
	private void updateChildrenInfo() {
		this.pointLight.position.set(this.position);
		this.torch.position.set(this.position);
		this.torch.angle = this.angle;
		this.torch.attenuationFactor = 0.005f;
		
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
	public float getMaxHealth() {
		return 10.0f;
	}
	
	public void setHeldItem(Item item) {
		this.heldItem = item;
	}
	
	public Item getHeldItem() {
		return heldItem;
	}
	
	public void setTorchOn(boolean torchOn) {
		this.torchOn = torchOn;
	}
	
	/**
	 * Gets the name of the player
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String getReadableName() {
		return getName();
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
		
		updateChildrenInfo();
		this.pointLight.update(ua);
		if (this.torchOn)
			this.torch.update(ua);
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
		
		updateChildrenInfo();
		this.pointLight.clientUpdate(ua);
		if (this.torchOn)
			this.torch.clientUpdate(ua);
		if (this.heldItem != null)
			this.heldItem.clientUpdate(ua);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		updateChildrenInfo();
		if (this.heldItem != null)
			this.heldItem.render(r, map);
		
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		Texture playerTexture = r.getTextureBank().getTexture("player_v1.png");
		r.drawTexture(playerTexture, Align.MM, position.x, position.y, RADIUS*2, RADIUS*2, angle);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		super.renderLight(r, map);
		
		updateChildrenInfo();
		this.pointLight.renderLight(r, map);
		if (this.torchOn)
			this.torch.renderLight(r, map);
		if (this.heldItem != null)
			this.heldItem.renderLight(r, map);
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
			break;
		}
		case END_USE: {
			this.beganUse = false;
			if (this.heldItem != null)
				this.heldItem.endUse();
			break;
		}
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
		case TOGGLE_LIGHT:
			this.torchOn = !this.torchOn;
			bank.updateEntityCached(new TorchLightUpdate(this.getId(), this.torchOn));
			break;
		case RELOAD:
			if (heldItem != null && heldItem instanceof Weapon) {
				Weapon w = (Weapon) heldItem;
				if (!w.isReloading())
					w.doReload(bank);
			}
			break;
		}
	}
	
	private void pickupItem(EntityBank bank, Item item) {
		this.dropHeldItem(bank, this.position);
		item.setOwner(this.getId());
		item.setOwnerTeam(this.getTeam());
		bank.updateEntityCached(new HeldItemUpdate(this.getId(), item));
		this.heldItem = item;
	}
	
	private void dropHeldItem(EntityBank bank, Vector2f position) {
		if (this.heldItem != null)
			bank.addEntityCached(new Pickup(new Vector2f(position), this.heldItem));
		bank.updateEntityCached(new HeldItemUpdate(this.getId(), null));
		this.heldItem = null;
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(this.position.x, this.position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public void death(UpdateArgs ua) {
		super.death(ua);
		Damage d = getLastDamage();
		Entity from = ua.bank.getEntity(d.ownerId);
		String s = d.type.getDescription(from, this);
		ua.packetCache.sendStringTcp(Protocol.sendMessageToClient("", s));
		ua.scoreboard.killPlayer(name, d);
		if (from != null && from instanceof Player) {
			Player p = (Player) from;
			if (name.equals(p.getName()))
				ua.scoreboard.addPlayerSuicide(p.getName());
			else
				ua.scoreboard.addPlayerKill(p.getName());
		}
	}
	
	@Override
	public Player clone() {
		return new Player(this);
	}
}
