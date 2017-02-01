package game.ai;
import java.util.ArrayList;
public class AStarTesting {

	public static void main(String[] args) {
		Boolean[][] walkable = new Boolean[5][5];
		for (int x = 0; x < 5; x ++) {
			for (int y = 0; y < 5; y++) {
				walkable[x][y] = true;
				
			}
		}
		walkable[1][0] = false;
		walkable[2][0] = false;
		walkable[2][1] = false;
		ArrayList<Node> node = new ArrayList<Node>();
		
		node = new AStar(new Node(0,0), new Node(3,0), walkable).findRoute();
		System.out.print(node);	
		
				
	}

}
