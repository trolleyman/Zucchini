package game.world.entity;

import game.Util;
import game.action.Action;
import game.action.AimAction;
import game.net.Protocol;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.PhysicsUtil;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageSource;
import game.world.entity.light.PointLight;
import game.world.entity.light.Spotlight;
import game.world.entity.update.AngleUpdate;
import game.world.entity.update.HeldItemUpdate;
import game.world.entity.update.TorchLightUpdate;
import game.world.entity.weapon.Knife;
import game.world.entity.weapon.MachineGun;
import game.world.entity.weapon.Weapon;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a player, either controlled by an AI or a human
 *
 * @author Callum
 */
public abstract class Player extends AutonomousEntity {
	/** The offset of the hand holding the weapon */
	private static final float WEAPON_OFFSET_X = 0.15f;
	
	/** The max distance a player can see */
	public static final float LINE_OF_SIGHT_MAX = 10.0f;
	/** The angle of which the player can see */
	public static final float LINE_OF_SIGHT_FOV = (float) Math.toRadians(360.0);
	
	private static final Vector4f SPOT_COLOR = LightUtil.LIGHT_DIRECT_SUNLIGHT_6000;
	private static final Vector4f TORCH_COLOR = LightUtil.LIGHT_DIRECT_SUNLIGHT_6000;
	
	/** The speed of the player in m/s */
	protected static final float MAX_SPEED = 4.0f;
	/** The radius of the player in m */
	protected static final float RADIUS = 0.2f;
	/** True if the torch is on */
	private boolean torchOn = true;
	
	public static Item getDefaultHeldItem() {
		return new Knife(new Vector2f(0.0f, 0.0f));
	}
	
	/** The name of the player */
	private String name;
	
	/** The currently held item. Not necessarily a weapon */
	protected Item heldItem;
	
	private PointLight pointLight;
	/** The torch of the player */
	private Spotlight torch;
	
	/** Has the player been assigned a footstep sound source? */
	private transient int walkingSoundID = -1; // sound source id associated with player movement
	
	private transient boolean beganUse = false;
	
	/**
	 * Constructs a new player at the specified position holding {@link Player#getDefaultHeldItem}
	 *
	 * @param position The position
	 * @param name     The name of the player
	 */
	public Player(int team, Vector2f position, String name) {
		this(team, position, name, Player.getDefaultHeldItem());
	}
	
	/**
	 * Constructs a new player at the specified position holding a weapon
	 *
	 * @param position  The position
	 * @param _name     The name of the player
	 * @param _heldItem The currently held item
	 */
	public Player(int team, Vector2f position, String _name, Item _heldItem) {
		super(team, position, RADIUS, 1.0f, MAX_SPEED, false);
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
	 *
	 * @param p The player
	 */
	public Player(Player p) {
		super(p);
		
		this.name = p.name;
		this.heldItem = p.heldItem.clone();
		
		this.beganUse = p.beganUse;
		
		this.pointLight = p.pointLight.clone();
		this.torch = p.torch.clone();
	}
	
	protected void updateChildrenInfo() {
		this.pointLight.position.set(this.position);
		this.torch.position.set(this.position);
		this.torch.angle = this.angle;
		this.torch.attenuationFactor = 0.005f;
		
		if (this.heldItem != null) {
			this.heldItem.setOwner(this);
			this.heldItem.angle = this.angle;
			
			// Calculate position
			Vector2f offset = Util.pushTemporaryVector2f();
			offset.set(Util.getDirX(angle + (float) Math.PI / 2), Util.getDirY(angle + (float) Math.PI / 2))
					.mul(WEAPON_OFFSET_X);
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
		// Update velocity & handle collisions
		super.update(ua);
		
		// Update children
		updateChildrenInfo();
		this.pointLight.update(ua);
		if (this.torchOn)
			this.torch.update(ua);
		if (this.heldItem != null)
			this.heldItem.update(ua);
		
		// Play walking sounds
		if (velocity.length() > 1f) {
			//System.out.println("Starting sound id: " + walkingSoundID);
			if (walkingSoundID == -1) {
				this.walkingSoundID = ua.audio.play("footsteps_running.wav", 0.6f, this.position);
			} else {
				ua.audio.continueLoop(this.walkingSoundID, this.position);
			}
		} else {
			//System.out.println("Stopping sound id: " + walkingSoundID);
			if (walkingSoundID != -1) {
				ua.audio.pauseLoop(this.walkingSoundID);
				walkingSoundID = -1;
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
	 *
	 * @param ua The update arguments
	 * @param a  The action
	 */
	public void handleAction(UpdateArgs ua, Action a) {
		switch (a.getType()) {
			case BEGIN_MOVE_NORTH:
			case BEGIN_MOVE_SOUTH:
			case BEGIN_MOVE_EAST:
			case BEGIN_MOVE_WEST:
			case END_MOVE_NORTH:
			case END_MOVE_SOUTH:
			case END_MOVE_EAST:
			case END_MOVE_WEST:
				System.err.println("[Game]: Warning: Invalid action type received for Player: " + a.getType());
				break;
			case AIM:
				angle = ((AimAction) a).getAngle();
				ua.bank.updateEntityCached(new AngleUpdate(this.getId(), angle));
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
				ArrayList<Entity> es = ua.bank.getEntitiesNear(position.x, position.y, 0.5f);
				Optional<Entity> oe = es.stream()
						.filter((e) -> e instanceof Pickup)
						.min((l, r) -> Float.compare(l.position.distanceSquared(this.position), r.position.distanceSquared(this.position)));
				if (oe.isPresent()) {
					Pickup p = (Pickup) oe.get();
					
					// Drop current item
					this.dropHeldItem(ua.bank, p.position);
					
					// Get item
					Item item = p.getItem();
					
					// Own item & pickup item
					item.setOwner(this);
					this.pickupItem(ua.bank, item);
					ua.packetCache.sendStringTcp(this.getName(), Protocol.sendMessageToClient("", "Equipped " + item.toString()));
					
					// Remove pickup entity
					ua.bank.removeEntityCached(p.getId());
				}
				break;
			}
			case TOGGLE_LIGHT:
				this.torchOn = !this.torchOn;
				ua.bank.updateEntityCached(new TorchLightUpdate(this.getId(), this.torchOn));
				break;
			case RELOAD:
				if (heldItem != null && heldItem instanceof Weapon) {
					Weapon w = (Weapon) heldItem;
					if (!w.isReloading())
						w.doReload(ua.bank);
				}
				break;
		}
	}
	
	private void pickupItem(EntityBank bank, Item item) {
		this.dropHeldItem(bank, this.position);
		item.setOwner(this);
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
		String s = d.type.getDescription(d.source, this);
		ua.packetCache.sendStringTcp(Protocol.sendMessageToClient("", s));
		ua.scoreboard.killPlayer(name, d);
		if (d.source.entityId != Entity.INVALID_ID && d.source.isPlayer) {
			if (name.equals(d.source.readableName))
				ua.scoreboard.addPlayerSuicide(d.source.readableName);
			else
				ua.scoreboard.addPlayerKill(d.source.readableName);
		}
		ua.audio.play("dying.wav", 1f, this.position);
		
		// Drop weapon
		dropHeldItem(ua.bank, this.position);
	}
	
	/**
	 * Gets the correct angle for the player so that the weapon it's holding will be aimed directly at the point x, y.
	 * @param x The x co-ordinate of the target
	 * @param y The y co-ordinate of the target
	 */
	public float getFiringAngle(float x, float y) {
		Vector2f weaponPos = Util.pushTemporaryVector2f();
		weaponPos.set(Util.getDirX(angle + (float) Math.PI / 2), Util.getDirY(angle + (float) Math.PI / 2))
				.mul(WEAPON_OFFSET_X).add(this.position);
		float firingAngle = Util.getAngle(weaponPos.x, weaponPos.y, x, y);
		Util.popTemporaryVector2f();
		return firingAngle;
	}
}
