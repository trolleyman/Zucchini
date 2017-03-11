package game.world.entity;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.Util;
import game.ai.Node;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.update.AngleUpdate;
import game.world.map.PathFindingMap;

public abstract class AutonomousPlayerEntity extends MovableEntity {
	private transient Vector2f destination = null;
	/** This will be empty if there is no route */
	private transient ArrayList<Node> route = new ArrayList<>();
	/** The max speed of the entity */
	private transient float maxSpeed;

	public AutonomousPlayerEntity(int team, Vector2f position, Item heldItem) {
		super(team, position, 1.0f);
	}
	
	public AutonomousPlayerEntity(AutonomousPlayerEntity e) {
		super(e);
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
