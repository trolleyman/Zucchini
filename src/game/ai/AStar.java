package game.ai;

import game.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;


/**
 * Simple A* route from (x,y) to (a,b)
 * <p>
 * run by: AStar route = new Astar(new Node(startX, startY), new Node(goalx, goaly), map);
 * route.findRoute();
 * If preferred could be converted so that it can be executed in one commanded
 * @author George
 */
public class AStar {
	private final float[][] heuristicMap;
	private final float[][] movementCostMap;
	private final float[][] totalCostMap;
	private float D2 = 1;
	private float D = 1;
	
	private final int width;
	private final int height;
	
	private final PriorityQueue<Node> openSet;
	private final HashSet<Node> closeSet;
	private final HashSet<Node> closeSetDefault;
	
	private final HashMap<Node, Node> previousNodeMap;
	
	/**
	 * Constructor of A*
	 * @param walkable the map
	 */
	public AStar(boolean[][] walkable) {
		width = walkable.length;
		height = walkable[0].length;
		heuristicMap = new float[width][height];
		movementCostMap = new float[width][height]; // g score
		previousNodeMap = new HashMap<>();
		totalCostMap = new float[width][height]; // f score
		
		openSet = new PriorityQueue<>((l, r) -> Float.compare(heuristicMap[l.getX()][l.getY()], heuristicMap[r.getX()][r.getY()]));
		closeSet = new HashSet<>();
		closeSetDefault = new HashSet<>();
		
		// creating obstacles in the map
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (walkable[x][y]) {
					closeSetDefault.add(new Node(x, y));
				}
			}
		}
	}
	
	/*
	 * finds the path to the goal
	 * @returns an ArrayList of nodes which is the route from start to goal
	 */
	public ArrayList<Node> findRoute(Node start, Node goal) {
		long startTime = System.nanoTime();
		
		// === Reset State ===
		// Reset total cost map
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				movementCostMap[x][y] = Integer.MAX_VALUE;
				totalCostMap[x][y] = Integer.MAX_VALUE;
			}
		}
		
		// Reset sets
		openSet.clear();
		closeSet.clear();
		previousNodeMap.clear();
		
		for (Node n : closeSetDefault) {
			closeSet.add(n);
		}
		
		// === Calculate Route ===
		// adding start node to the openSet
		openSet.add(start);
		Node currentNode;
		
		ArrayList<Node> route = null;
		movementCostMap[start.getX()][start.getY()] = 0;
		while (!openSet.isEmpty()) {
			// Get best current node
			currentNode = openSet.remove();
			if (closeSet.contains(currentNode))
				continue;
			
			closeSet.add(currentNode);
			
			if (currentNode.equals(goal)) {
				// return completed path
				route = constructPath(currentNode);
				break;
			}
			
			// Get neighbours
			for (int x = currentNode.getX() - 1; x <= currentNode.getX() + 1; x++) {
				for (int y = currentNode.getY() - 1; y <= currentNode.getY() + 1; y++) {
					if (x < width && x >= 0 && y >= 0) {
						Node neighbour = new Node(x, y);
						
						if (closeSet.contains(neighbour)) {
							// go to next loop
							continue;
						}
						
						float tentativeGScore = movementCostMap[currentNode.getX()][currentNode.getY()] + 1;
						// Calculate heuristic
						heuristicMap[x][y] = heuristic(goal, x, y);
						
						openSet.add(neighbour);
						
						if (tentativeGScore >= movementCostMap[neighbour.getX()][neighbour.getY()]) {
							// go to next loop
							continue;
						}
						previousNodeMap.put(neighbour, currentNode);
						movementCostMap[neighbour.getX()][neighbour.getY()] = tentativeGScore;
						totalCostMap[neighbour.getX()][neighbour.getY()] = tentativeGScore
								+ heuristicMap[neighbour.getX()][neighbour.getY()];
						
					}
				}
			}
		}
		
		if (route == null) {
			route = new ArrayList<>();
			// no path found
			System.out.println("[Game]: [AStar]: no path found from " + start + " to " + goal);
		}
		long endTime = System.nanoTime();
		long dt = endTime - startTime;
		long time = dt / (Util.NANOS_PER_SECOND / 1000);
		if (time > 50) {
			String s = route.size() == 0 ? "no path found" : "path found (" + route.size() + " nodes)";
			System.err.println("[Game]: [AStar]: Warning: AStar duration: " + time + "ms (" + s + ")");
		}
		return route;
	}
	
	/*
	 * adds the path to an array list
	 * @param currentNode adds the current node to the path
	 * @returns path   
	 */
	private ArrayList<Node> constructPath(Node currentNode) {
		ArrayList<Node> path = new ArrayList<>();
		path.add(currentNode);
		while (previousNodeMap.keySet().contains(currentNode)) {
			currentNode = previousNodeMap.get(currentNode);
			path.add(0, currentNode);
		}
		
		return path;
	}
	
	private float heuristic(Node goal, int x, int y) {
		float dx = Math.abs(x - goal.getY());
		float dy = Math.abs(y - goal.getY());
		return D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
	}
}