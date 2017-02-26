package game.world.map;
import java.util.ArrayList;
import java.util.HashMap;

import game.ai.AStar;
import game.ai.Node;
import org.joml.Vector2f;

import game.world.map.Wall;
public class PathFindingMap {
	
	public float scale;
	
	public float width;
	public float height;
	
	public boolean[][] grid;
	
	private HashMap<Node, HashMap<Node, ArrayList<Node>>> routeCache = new HashMap<>();
	
	/*
	 * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm seems like a good solution
	 */

	public PathFindingMap(Map map, float _scale){
		this.scale = _scale;
		
		ArrayList<Wall> walls = improvePrecision(scale, map.walls);
		
		float[] widthAndHeight = findWidthHeight(walls);
		
		width = widthAndHeight[0];
		height = widthAndHeight[1];
		
		grid = new boolean[(int)width][(int)height];
		//lines act like float[0] = x1 float[1] = y1 float[2] = x2 float[3] = y2
		
		int x0, y0, x1, y1, diffX, diffY, sx, sy, err, err2;
		// pointsList =  convertToPointList(lines);
		for (int z = 0; z < walls.size(); z++){
			
			
			
			x0 = (int)walls.get(z).p0.x;
			y0 = (int)walls.get(z).p0.y;
			x1 = (int)walls.get(z).p1.x;
			y1 = (int)walls.get(z).p1.y;
			
			diffX = Math.abs(x1 - x0);
			diffY = Math.abs(y1 - y0);
			
			
			
			sx = x0 < x1 ? 1 : -1;
			sy = y0 < y1 ? 1 : -1;
			
			err = diffX - diffY;
			while (true){
				
				grid[x0][y0] = true;
				if (x0 == x1 && y0 == y1){
					break;
				}
				err2 = err * 2;
				
				if (err2 > -diffY){
					err = err - diffY;
					x0 = x0 + sx;
				}
				
				if (err2 < diffX){
					err = err + diffX;
					y0 = y0 +sy;
				}
			}
		}
	}
	
	private static ArrayList<Wall> improvePrecision (float multiplier, ArrayList<Wall> walls){
		ArrayList<Wall> newWalls = new ArrayList<Wall>();
		
		for (Wall wall : walls){
			Wall newWall = new Wall(wall.p0.x * multiplier, wall.p0.y * multiplier,
					wall.p1.x * multiplier, wall.p1.y * multiplier);
			
			newWalls.add(newWall);
			
		}
		return newWalls;
	}
	
	private static float[] findWidthHeight(ArrayList<Wall> walls){
		float[] biggestValue = new float[2]; //0 = width 1 = height
		for (Wall z : walls){
			if (z.p0.x > biggestValue[0]){
				biggestValue[0] = z.p0.x + 1;
			}
			if (z.p1.x > biggestValue[0]){
				biggestValue[0] = z.p1.x + 1;
			}
			if (z.p0.y > biggestValue[1]){
				biggestValue[1] = z.p0.y + 1;
			}
			if (z.p1.y > biggestValue[1]) {
				biggestValue[1] = z.p1.y + 1;
			}
		}
		
		return biggestValue;
		
	}
	
	/**
	 * Gets the closest node to the specified position. The position given is in world (unscaled) coordinated.
	 * @param v The position
	 */
	public Node getClosestNodeTo(Vector2f v) {
		return this.getClosestNodeTo(v.x, v.y);
	}
	
	/**
	 * Gets the closest node to the specified position. The position given is in world (unscaled) coordinated.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public Node getClosestNodeTo(float x, float y) {
		// Scale coordinates
		x = x * scale;
		y = y * scale;
		
		// Limit coordinates
		if (x < 0.0f)
			x = 0.0f;
		if (x > width)
			x = width;
		if (y < 0.0f)
			y = 0.0f;
		if (y > height)
			y = height;
		
		// Round coordinates
		int ix = Math.round(x);
		int iy = Math.round(y);
		
		// Get the ix,iy node
		return new Node(ix, iy);
	}
	
	public float getNodeWorldX(Node node) {
		return node.getX() / scale;
	}
	
	public float getNodeWorldY(Node node) {
		return node.getY() / scale;
	}
	
	public ArrayList<Node> findRoute(Node start, Node end) {
		HashMap<Node, ArrayList<Node>> startMap = routeCache.get(start);
		if (startMap != null) {
			ArrayList<Node> cachedRoute = startMap.get(end);
			if (cachedRoute != null)
				if (cachedRoute.size() == 0)
					return cachedRoute;
				else
					return new ArrayList<>(cachedRoute);
		} else {
			startMap = new HashMap<>();
			routeCache.put(start, startMap);
		}
		
		ArrayList<Node> route = new AStar(start, end, this.grid).findRoute();
		startMap.put(end, route);
		if (route.size() == 0)
			return route;
		return new ArrayList<>(route);
	}

//	No longer needed walls are now arraylist of wall
//	private static ArrayList<Point> convertToPointList (float[] lines){
//		int x;
//		int y;
//		ArrayList<Point> pointsList = new ArrayList<Point>();
//		if ((lines.length) % 2 != 0){
//			System.out.println("lines is not even!");
//			return null;
//		}
//		for (int z = 0; z < lines.length - 1; z+=2){
//			x = (int)Math.round(lines[z]);
//			y = (int)Math.round(lines[z+1]);
//			pointsList.add(new Point(x,y));
//			
//		}
//		return pointsList;
//		
//	}
	
	
}