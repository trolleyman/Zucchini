package game.ai;
import java.util.ArrayList;

import game.world.map.PathFindingMap;
import org.joml.Vector2f;

import game.world.map.TestMap;
import game.world.map.Wall;
public class AStarTesting {

	public static void main(String[] args) {
		
		PathFindingMap map = new PathFindingMap(new TestMap(), 5.0f);
		
		ArrayList<Node> node = new ArrayList<Node>();
		node = new AStar(new Node(1,1), new Node((int)map.width - 5,(int)map.height -5),map.grid).findRoute();
		
		System.out.println(node);
		
		for (int y = map.grid[0].length -1 ; y > -1 ; y --){
			for (int x = 0; x < map.grid.length ; x ++){
				
				
				if (!node.isEmpty() && node.contains(new Node(x,y))){
						System.out.print("|.");
				}else if (map.grid[x][y] == true){
					System.out.print("|x");
				}else{
					System.out.print("| ");
				}
				
			}
			System.out.println();
		}
		
		
		System.out.println(map.grid[0][0]);
		System.out.println(map.grid[0][8]);
		
				
	}
}
