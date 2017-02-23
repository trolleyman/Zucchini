package game.world.physics.tree;

import game.world.physics.shape.Shape;

import java.util.ArrayList;

public class QTLeaf extends QuadTree {
	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	
	public QTLeaf(float x0, float y0, float x1, float y1) {
		super(x0, y0, x1, y1);
	}
	
	@Override
	public void addShape(Shape s) {
		this.shapes.add(s);
	}
}
