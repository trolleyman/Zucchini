package game.world.physics.tree;

import game.Util;
import game.world.physics.Collision;
import game.world.physics.PhysicsUtil;
import game.world.physics.shape.Shape;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.function.Predicate;

public class QTNode extends QuadTree {
	private QuadTree bl;
	private QuadTree br;
	private QuadTree tl;
	private QuadTree tr;
	
	public QTNode(int maxLevel, int level, float x0, float y0, float x1, float y1,
	              QuadTree bl, QuadTree br, QuadTree tl, QuadTree tr) {
		super(maxLevel, level, x0, y0, x1, y1);
		this.bl = bl;
		this.br = br;
		this.tl = tl;
		this.tr = tr;
	}
	
	public QTNode(QTNode node) {
		super(node);
		
		this.bl = bl.clone();
		this.br = br.clone();
		this.tl = tl.clone();
		this.tr = tr.clone();
	}
	
	private boolean isInBL(Vector4f aabb) {
		return aabb.x <= bl.x1 && aabb.y <= bl.y1;
	}
	private boolean isInBR(Vector4f aabb) {
		return aabb.z >  br.x0 && aabb.y <= br.y1;
	}
	private boolean isInTL(Vector4f aabb) {
		return aabb.x <= tl.x1 && aabb.w >  tl.y0;
	}
	private boolean isInTR(Vector4f aabb) {
		return aabb.z >  tr.x0 && aabb.w >  tr.y0;
	}
	
	@Override
	public QuadTree addShape(Shape s) {
		Vector4f aabb = s.getAABB();
		
		if (isInBL(aabb))
			bl = bl.addShape(s);
		if (isInBR(aabb))
			br = br.addShape(s);
		if (isInTL(aabb))
			tl = tl.addShape(s);
		if (isInTR(aabb))
			tr = tr.addShape(s);
		
		return this;
	}
	
	@Override
	public void removeShape(Shape s) {
		Vector4f aabb = s.getAABB();
		
		if (isInBL(aabb))
			bl.removeShape(s);
		if (isInBR(aabb))
			br.removeShape(s);
		if (isInTL(aabb))
			tl.removeShape(s);
		if (isInTR(aabb))
			tr.removeShape(s);
	}
	
	@Override
	public ArrayList<Collision> getCollisions(Shape s, ArrayList<Collision> dest) {
		Vector4f aabb = s.getAABB();
		
		if (isInBL(aabb))
			dest = this.bl.getCollisions(s, dest);
		if (isInBR(aabb))
			dest = this.br.getCollisions(s, dest);
		if (isInTL(aabb))
			dest = this.tl.getCollisions(s, dest);
		if (isInTR(aabb))
			dest = this.tr.getCollisions(s, dest);
		
		return dest;
	}
	
	@Override
	public Collision getClosestCollision(Shape s) {
		Collision closest = bl.getClosestCollision(s);
		closest = PhysicsUtil.getClosest(s.getPosition(), closest, br.getClosestCollision(s));
		closest = PhysicsUtil.getClosest(s.getPosition(), closest, tl.getClosestCollision(s));
		closest = PhysicsUtil.getClosest(s.getPosition(), closest, tr.getClosestCollision(s));
		return closest;
	}
	
	@Override
	public Vector2f getClosestIntersection(Shape s, Vector2f dest, Predicate<Shape> pred) {
		Vector2f blInt = Util.pushTemporaryVector2f();
		Vector2f brInt = Util.pushTemporaryVector2f();
		Vector2f tlInt = Util.pushTemporaryVector2f();
		Vector2f trInt = Util.pushTemporaryVector2f();
		
		blInt = bl.getClosestIntersection(s, blInt, pred);
		brInt = br.getClosestIntersection(s, brInt, pred);
		tlInt = tl.getClosestIntersection(s, tlInt, pred);
		trInt = tr.getClosestIntersection(s, trInt, pred);
		
		// Find closest among the four quadrants
		Vector2f ret;
		if (blInt == null && brInt == null && tlInt == null && trInt == null) {
			ret = null;
		} else {
			if (dest == null)
				dest = new Vector2f();
			
			ret = PhysicsUtil.getClosest(s.getPosition(), blInt, brInt);
			ret = PhysicsUtil.getClosest(s.getPosition(), ret , tlInt);
			ret = PhysicsUtil.getClosest(s.getPosition(), ret , trInt);
			dest.set(ret);
			
			ret = dest;
		}
		
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
		
		return ret;
	}
	
	@Override
	public void removeAllDirty(ArrayList<Shape> dest) {
		bl.removeAllDirty(dest);
		br.removeAllDirty(dest);
		tl.removeAllDirty(dest);
		tr.removeAllDirty(dest);
	}
	
	@Override
	public QuadTree trim() {
		bl = bl.trim();
		br = br.trim();
		tl = tl.trim();
		tr = tr.trim();
		
		if (bl instanceof QTLeaf && br instanceof QTLeaf && tl instanceof QTLeaf && tr instanceof QTLeaf
				&& ((QTLeaf) bl).shapesSize() == 0 && ((QTLeaf) br).shapesSize() == 0
				&& ((QTLeaf) tl).shapesSize() == 0 && ((QTLeaf) tr).shapesSize() == 0) {
			// This could be a leaf
			return new QTLeaf(maxLevel, level, x0, y0, x1, y1);
		}
		return this;
	}
	
	@Override
	public QTNode clone() {
		return new QTNode(this);
	}
}
