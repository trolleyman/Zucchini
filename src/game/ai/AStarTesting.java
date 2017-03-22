package game.ai;

import game.world.map.FinalMap;
import game.world.map.PathFindingMap;

import java.util.ArrayList;

public class AStarTesting {
	
	public static void main(String[] args) {
		//if error with zombies not finding player, go through the program and make sure the scale is the same throughout
		PathFindingMap map = new PathFindingMap(new FinalMap(), 3f);
		
		AStar aStar = new AStar(map.grid);
		
		// 1st route
		Node start = new Node(1, 1);
		Node goal = new Node((int) map.width - 5, (int) map.height - 5);
		
		ArrayList<Node> route = aStar.findRoute(start, goal);
		System.out.println(route);
		printMap(map, start, goal, route);
		System.out.println();
		
		// 2nd route
		start = new Node(1, (int) map.height - 2);
		goal = new Node((int) map.width - 2, 1);
		
		route = aStar.findRoute(start, goal);
		System.out.println(route);
		printMap(map, start, goal, route);
		System.out.println();
	}
	
	private static void printMap(PathFindingMap map, Node start, Node goal, ArrayList<Node> route) {
		for (int y = map.grid[0].length - 1; y > -1; y--) {
			for (int x = 0; x < map.grid.length; x++) {
				
				if (start.getX() == x && start.getY() == y) {
					System.out.print("|S");
				} else if (goal.getX() == x && goal.getY() == y) {
					System.out.print("|G");
				} else if (!route.isEmpty() && route.contains(new Node(x, y))) {
					System.out.print("|.");
				} else if (map.grid[x][y]) {
					System.out.print("|x");
				} else {
					System.out.print("| ");
				}
			}
			System.out.println();
		}
	}
}
