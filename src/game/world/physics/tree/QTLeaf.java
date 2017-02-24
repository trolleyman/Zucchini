package game.world.physics.tree;

import game.Util;
import game.world.physics.Collision;
import game.world.physics.PhysicsUtil;
import game.world.physics.shape.Shape;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Vector;

public class QTLeaf extends QuadTree {
	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	
	public QTLeaf(int maxLevel, int level, float x0, float y0, float x1, float y1) {
		super(maxLevel, level, x0, y0, x1, y1);
	}
	
	public QTLeaf(QTLeaf leaf) {
		super(leaf);
		
		for (Shape s : leaf.shapes) {
			this.shapes.add(s.clone());
		}
	}
	
	public int shapesSize() {
		return this.shapes.size();
	}
	
	@Override
	public QuadTree addShape(Shape s) {
		this.shapes.add(s);
		
		if (level == maxLevel || shapes.size() <= 1) {
			return this;
		} else {
			// Subdivide
			float midx = (this.x0 + this.x1) / 2.0f;
			float midy = (this.y0 + this.y1) / 2.0f;
			
			QTLeaf bl = new QTLeaf(this.maxLevel, this.level + 1, this.x0, this.y0, midx   , midy   );
			QTLeaf br = new QTLeaf(this.maxLevel, this.level + 1, midx   , this.y0, this.x1, midy   );
			QTLeaf tl = new QTLeaf(this.maxLevel, this.level + 1, this.x0, midy   , midx   , this.y1);
			QTLeaf tr = new QTLeaf(this.maxLevel, this.level + 1, midx   , midy   , this.x1, this.y1);
			
			QTNode node = new QTNode(this.maxLevel, this.level, x0, y0, x1, y1, bl, br, tl, tr);
			
			for (Shape oldShape : shapes)
				node.addShape(oldShape);
			
			return node;
		}
	}
	
	@Override
	public void removeShape(Shape s) {
		for (int i = 0; i < this.shapes.size(); i++) {
			Shape o = shapes.get(i);
			if (s == o || s.getEntityID() == o.getEntityID()) {
				shapes.remove(i);
				break;
			}
		}
	}
	
	@Override
	public ArrayList<Collision> getCollisions(Shape s, ArrayList<Collision> dest) {
		for (Shape o : shapes) {
			if (s == o) // Skip intersection with itself
				continue;
			
			Vector2f v = s.queryCollision(o, null);
			if (v != null) {
				if (dest == null)
					dest = new ArrayList<>();
				dest.add(new Collision(s, o, v));
			}
		}
		return dest;
	}
	
	@Override
	public Collision getClosestCollision(Shape s) {
		Collision c = null;
		for (Shape o : shapes) {
			Vector2f v = s.queryCollision(o, null);
			if (v != null) {
				c = PhysicsUtil.getClosest(s.getPosition(), c, new Collision(s, o, v));
			}
		}
		return c;
	}
	
	@Override
	public Vector2f getClosestIntersection(Shape s, Vector2f dest) {
		if (this.shapes.size() == 0)
			return null;
		
		Vector2f closest = null;
		Vector2f current = Util.pushTemporaryVector2f();
		
		for (Shape o : shapes) {
			Vector2f res = s.queryCollision(o, current);
			if (res != null) {
				if (closest == null) {
					// Set closest to current intersection
					closest = Util.pushTemporaryVector2f();
					closest.set(res);
				} else {
					// Calculate closest intersection
					float closestDist2 = closest.distanceSquared(s.getPosition());
					float currentDist2 = current.distanceSquared(s.getPosition());
					
					if (currentDist2 < closestDist2)
						closest.set(current); // Take closest intersection point
				}
			}
		}
		
		// Pop current
		Util.popTemporaryVector2f();
		
		if (closest == null) {
			return null;
		} else {
			// Alloc new dest if needed
			if (dest == null)
				dest = new Vector2f();
			
			dest.set(closest);
			
			// Pop closest
			Util.popTemporaryVector2f();
			
			return dest;
		}
	}
	
	@Override
	public void removeAllDirty(ArrayList<Shape> dest) {
		for (Shape s : shapes) {
			if (s.isDirty()) {
				s.clean();
				dest.add(s);
			}
		}
	}
	
	@Override
	public QuadTree trim() {
		return this;
	}
	
	@Override
	public QTLeaf clone() {
		return new QTLeaf(this);
	}
}
