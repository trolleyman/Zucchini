package game.ai;
import java.util.ArrayList;

import org.joml.Vector2f;

import game.world.pathFindingMap;
import game.world.map.TestMap;
import game.world.map.Wall;
public class AStarTesting {

	public static void main(String[] args) {
		
		
		ArrayList<Wall> walls = new TestMap().walls;
		walls = improvePrecision(5, walls);
		float[] widthAndHeight = findWidthHeight(walls);
		
		int width = (int)widthAndHeight[0];
		int height = (int)widthAndHeight[1];
	
		
		
		
		boolean [][] map = pathFindingMap.convertWallsToGrid(walls, width, height);
		
		ArrayList<Node> node = new ArrayList<Node>();
		node = new AStar(new Node(1,1), new Node(width - 5,height -5),map).findRoute();
		
		System.out.println(node);
		
		for (int y = map[0].length -1 ; y > -1 ; y --){
			for (int x = 0; x < map.length ; x ++){
				
				
				if (!node.isEmpty() && node.contains(new Node(x,y))){
						System.out.print("|.");
				}else if (map[x][y] == true){
					System.out.print("|x");
				}else{
					System.out.print("| ");
				}
				
			}
			System.out.println();
		}
		
		
		System.out.println(map[0][0]);
		System.out.println(map[0][8]);
		
				
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
			if (z.p1.y > biggestValue[1]){
				biggestValue[1] = z.p1.y+ 1;
			}
			
		}
		
		return biggestValue;
		
	}
	private static ArrayList<Wall> improvePrecision (int multiplier, ArrayList<Wall> walls){
		ArrayList<Wall> newWalls = new ArrayList<Wall>();
		
		for (Wall wall : walls){
			Wall newWall = new Wall(wall.p0.x * multiplier, wall.p0.y * multiplier,
					wall.p1.x * multiplier, wall.p1.y * multiplier);
			
			newWalls.add(newWall);
			
		}
		return newWalls;
	}

}
