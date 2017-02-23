package game.world.physics;

import game.world.physics.tree.QTLeaf;
import game.world.physics.tree.QuadTree;

public class PhysicsWorld {
	private QuadTree tree;
	
	public PhysicsWorld() {
		tree = new QTLeaf();
	}
}
