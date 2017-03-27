package game.ai;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class AStarTestingJUnit {


	@SuppressWarnings("null")
	@Test
	public void testAStar() {
		boolean [][] map = new boolean[5][5];
		
		for (int x = 0; x < 5; x ++){
			for (int y  = 0; y < 5; y ++){
			
				map[x][y] = false;
				
			}
		}
		 AStar astar = new AStar(map);
		ArrayList<Node> test1 = astar.findRoute(new Node(0,0), new Node(0,4));
		ArrayList<Node> expected1 = new ArrayList<Node>();
		expected1.add(new Node(0,0));
		expected1.add(new Node(0,1));
		expected1.add(new Node(1,2));
		expected1.add(new Node(1,3));
		expected1.add(new Node(0,4));
		assertEquals(expected1.toString(),test1.toString());
		
		AStar astar2 = new AStar(map);
		ArrayList<Node> test2 = astar.findRoute(new Node(0,0), new Node(4,4));
		ArrayList<Node> expected2 = new ArrayList<Node>();
		expected2.add(new Node(0,0));
		expected2.add(new Node(1,1));
		expected2.add(new Node(2,2));
		expected2.add(new Node(3,3));
		expected2.add(new Node(4,4));
		assertEquals(expected2.toString(),test2.toString());
		
	}


}
