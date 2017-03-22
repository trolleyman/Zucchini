package game.world.entity;

import game.Util;
import game.ai.Node;
import game.world.UpdateArgs;
import game.world.entity.weapon.Knife;
import game.world.map.PathFindingMap;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * An entity that can be given a position to go to, and will navigate to that.
 */
public abstract class AutonomousEntity extends MovableEntity {
	/** The min time between route regen */
	private static double MIN_TIME_BETWEEN_ROUTE_REGEN = 0.4;
	
	/** Whether the AutonomousEntity is enabled or not */
	protected boolean enabled;
	
	private transient Vector2f destination = null;
	
	/** This will be empty if there is no route */
	private transient ArrayList<Node> route = new ArrayList<>();
	
	/** The max speed of the entity */
	private transient float maxSpeed;
	
	/** Time since last regen of the route */
	private transient double timeSinceRouteRegen = MIN_TIME_BETWEEN_ROUTE_REGEN;
	
	/** Should the route be regenerated */
	private transient boolean routeDirty = true;
	
	/**
	 * Constructs a new AutonomousEntity with no collision shape
	 * @param team The team of the entity
	 * @param position The position of the entity
	 * @param momentumScale The momentum scale. See {@link MovableEntity#MovableEntity(int, Vector2f, float)}
	 * @param _maxSpeed The max speed of the AutonomousEntity
	 * @param _enabled Whether or not the AutonomousEntity is enabled.
	 */
	public AutonomousEntity(int team, Vector2f position, float momentumScale, float _maxSpeed, boolean _enabled) {
		super(team, position, momentumScale);
		this.maxSpeed = _maxSpeed;
		this.enabled = _enabled;
	}
	
	/**
	 * Constructs a new AutonomousEntity with a circular collision shape
	 * @param team The team of the entity
	 * @param position The position of the entity
	 * @param radius The radius of the collision shape
	 * @param momentumScale The momentum scale. See {@link MovableEntity#MovableEntity(int, Vector2f, float, float)}
	 * @param _maxSpeed The max speed of the AutonomousEntity
	 * @param _enabled Whether or not the AutonomousEntity is enabled.
	 */
	public AutonomousEntity(int team, Vector2f position, float radius, float momentumScale, float _maxSpeed, boolean _enabled) {
		super(team, position, radius, momentumScale);
		this.maxSpeed = _maxSpeed;
		this.enabled = _enabled;
	}
	
	@SuppressWarnings("unchecked")
	public AutonomousEntity(AutonomousEntity e) {
		super(e);
		this.enabled = e.enabled;
		
		if (this.destination != null)
			this.destination = new Vector2f(e.destination);
		
		this.route = (ArrayList<Node>) e.route.clone();
		this.maxSpeed = e.maxSpeed;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		if (enabled) {
			timeSinceRouteRegen += MIN_TIME_BETWEEN_ROUTE_REGEN;
			PathFindingMap pfmap = ua.map.getPathFindingMap();
			
			// If the route is dirty, regenerate the route
			if (routeDirty && timeSinceRouteRegen > MIN_TIME_BETWEEN_ROUTE_REGEN) {
				regenRoute(ua.map.getPathFindingMap());
				routeDirty = false;
				timeSinceRouteRegen = 0.0;
			}
			
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
			}
			this.addTargetVelocity(ua, target);
		}
		
		// Update position
		super.update(ua);
	}
	
	private void regenRoute(PathFindingMap map) {
		this.route.clear();
		
		if (this.destination == null)
			return;
		
		Node start = map.getClosestNodeTo(this.position);
		Node end = map.getClosestNodeTo(this.destination);
		
		this.route = map.findRoute(start, end);
	}
	
	public void setDestination(PathFindingMap map, Vector2f newDest) {
		this.enabled = true;
		
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
		
		if (prevDest == null || destination == null) {
			this.routeDirty = true;
		} else {
			Node prevDestNode = map.getClosestNodeTo(prevDest);
			Node destinationNode = map.getClosestNodeTo(destination);
			
			if (!prevDestNode.equals(destinationNode)) {
				this.routeDirty = true;
			}
		}
		
		if (prevDest != null)
			Util.popTemporaryVector2f();
	}
}
