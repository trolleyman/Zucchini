package game.ai;
import java.util.ArrayList;

import game.world.pathFindingMap;
public class AStarTesting {

	public static void main(String[] args) {
		
		
		
		float[] lines = new float[12];
		lines[0] = 2f;
		lines[1] = 2f;
		lines[2] = 2f;
		lines[3] = 99f;
		
		lines[4] = 4f;
		lines[5] = 95f;
		lines[6] = 4f;
		lines[7] = 0f;
		
		
		// 1,3 -> 99,98
		// 1,30 -> 30,30
		
		
		boolean [][] map = pathFindingMap.convertLinesToGrid(lines, 100, 100);
		
		ArrayList<Node> node = new ArrayList<Node>();
		node = new AStar(new Node(0,1), new Node(7,1),map).findRoute();
		System.out.println(node);
		
		for (int y = 0 ; y < map[0].length - 1; y ++){
			for (int x = 0; x < map.length - 1; x ++){
				if (node.contains(new Node(x,y))){
					System.out.print("|*");
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

}
