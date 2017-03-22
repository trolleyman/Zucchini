package game.world.entity;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector4f;

import game.Util;
import game.ai.Node;
import game.render.IRenderer;
import game.world.EntityBank;
import game.world.UpdateArgs;
import game.world.entity.light.PointLight;
import game.world.entity.light.Spotlight;
import game.world.entity.update.AngleUpdate;
import game.world.entity.update.HeldItemUpdate;
import game.world.map.PathFindingMap;

public abstract class AutonomousPlayerEntity extends MovableEntity {
	/** The max distance a player can see */
	public static final float LINE_OF_SIGHT_MAX = 20.0f;
	/** The angle of which the player can see */
	public static final float LINE_OF_SIGHT_FOV = (float)Math.toRadians(360.0);
	
	private static final Vector4f SPOT_COLOR = LightUtil.LIGHT_DIRECT_SUNLIGHT_6000;
	private static final Vector4f TORCH_COLOR = LightUtil.LIGHT_DIRECT_SUNLIGHT_6000;
	
	
	private transient Vector2f destination = null;
	/** This will be empty if there is no route */
	private transient ArrayList<Node> route = new ArrayList<>();
	/** The max speed of the entity */
	private transient float maxSpeed;
	
	private boolean torchOn = true;
	private PointLight pointLight;
	/** The torch of the player */
	private Spotlight torch;
	private transient Item heldItem;

	
	public AutonomousPlayerEntity(int team, Vector2f position, float momentumScale, float _maxSpeed, Item heldItem) {
		super(team, position, momentumScale);
		
	
		this.heldItem = heldItem;
		this.pointLight = new PointLight(
				new Vector2f(this.position),
				new Vector4f(SPOT_COLOR.x, SPOT_COLOR.y, SPOT_COLOR.z, 0.8f),
				1.0f, true);
		this.torch = new Spotlight(
				new Vector2f(position),
				new Vector4f(TORCH_COLOR.x, TORCH_COLOR.y, TORCH_COLOR.z, 1.0f),
				0.01f, true, (float) Math.toRadians(30.0f), (float) Math.toRadians(60.0f));
		updateChildrenInfo();
		
		this.maxSpeed = _maxSpeed;
	}
	
	@SuppressWarnings("unchecked")
	public AutonomousPlayerEntity(AutonomousPlayerEntity ape) {
		super(ape);
		this.pointLight.position.set(this.position);
		this.torch.position.set(this.position);
		this.torch.angle = this.angle;
		if (this.destination != null)
			this.destination = new Vector2f(ape.destination);
		
		this.route = (ArrayList<Node>) ape.route.clone();
		this.maxSpeed = ape.maxSpeed;
	}

	@Override
	public void update(UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		
		// If close to current node, remove it
		while (true) {
			if (this.route.size() == 0)
				break;
			Node targetNode = this.route.get(0);
			float dist = this.position.distance(pfmap.getNodeWorldX(targetNode), pfmap.getNodeWorldY(targetNode));
			if (dist < 0.15f)
				this.route.remove(0);
			else
				break;
		}
		
		// Update velocity
		super.update(ua);
		Vector2f target;
		if (this.route.size() == 0) {
			target = new Vector2f();
		} else {
			Node targetNode = this.route.get(0);
			// Set target to be direction to targetNode
			target = new Vector2f()
					.set(pfmap.getNodeWorldX(targetNode), pfmap.getNodeWorldY(targetNode))
					.sub(position)
					.normalize()
					.mul(maxSpeed);
			
			ua.bank.updateEntityCached(new AngleUpdate(this.getId(), Util.getAngle(velocity.x, velocity.y)));
		}
		this.addTargetVelocity(ua, target);
		updateChildrenInfo();
		this.pointLight.update(ua);
		if (this.torchOn)
			this.torch.update(ua);
		if (this.heldItem != null)
			this.heldItem.update(ua);
		
		
		
		// Update position
		
	}

	private void updateChildrenInfo() {
		this.pointLight.position.set(this.position);
		this.torch.position.set(this.position);
		this.torch.angle = this.angle;
		
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
	
	private void regenRoute(PathFindingMap map) {
		this.route.clear();
		
		if (this.destination == null)
			return;
		
		Node start = map.getClosestNodeTo(this.position);
		Node end = map.getClosestNodeTo(this.destination);
		
		this.route = map.findRoute(start, end);
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
	
	public void setDestination(PathFindingMap map, Vector2f newDest) {
		Vector2f prevDest;
		if (this.destination == null)
			prevDest = null;
		else
			prevDest = Util.pushTemporaryVector2f().set(this.destination);
		
		if (newDest != null) {
			if (this.destination == null)
				this.destination = new Vector2f();
			this.destination.set(newDest);
		} else {
			this.destination = null;
		}
			
		if (this.destination == null && newDest != null)
			this.destination = new Vector2f();
		this.destination.set(newDest);
		
		if (prevDest == null) {
			regenRoute(map);
		} else {
			Node prevDestNode = map.getClosestNodeTo(prevDest);
			Node destinationNode = map.getClosestNodeTo(destination);
			
			if (!prevDestNode.equals(destinationNode)) {
				regenRoute(map);
			}
		}
		
		if (prevDest != null)
			Util.popTemporaryVector2f();
	}
}
