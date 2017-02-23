package game.world.physics.tree;

import game.world.physics.shape.Shape;

public class QTNode extends QuadTree {
	private QuadTree bl;
	private QuadTree br;
	private QuadTree tl;
	private QuadTree tr;
	
	public QTNode(float x0, float y0, float x1, float y1,
	              QuadTree bl, QuadTree br, QuadTree tl, QuadTree tr) {
		super(x0, y0, x1, y1);
		this.bl = bl;
		this.br = br;
		this.tl = tl;
		this.tr = tr;
	}
	
	@Override
	public void addShape(Shape s) {
		
	}
}
