package game.world;
import java.util.ArrayList;

import org.joml.Vector2f;

import game.world.map.Wall;
public class pathFindingMap {
	/*
	 * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm seems like a good solution
	 */

	public static boolean[][] convertWallsToGrid(ArrayList<Wall> walls, int width, int height){
	
		boolean[][] map = new boolean[width][height];
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
				
				map[x0][y0] = true;
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
		return map;
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