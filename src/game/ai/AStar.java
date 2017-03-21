package game.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import game.Util;
import game.world.map.PathFindingMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * Simple A* route from (x,y) to (a,b)
 * 
 * run by: AStar route = new Astar(new Node(startX, startY), new Node(goalx, goaly), map);
 * 		   route.findRoute();
 * If preferred could be converted so that it can be executed in one commanded
 * @author George
 */


public class AStar {
	private float[][] heuristicMap;
	private float[][] movementCostMap;
	private float[][] totalCostMap;
	
	private int width;
	private int height;
	private Node start;
	private Node goal;
	

	private PriorityQueue<Node> openSet;
	private HashSet<Node> closeSet;

	private HashMap<Node, Node> previousNodeMap;
	
	
	private ArrayList<Node> finalPath;

/*
 * Constructor of A*Star
 * @param start the start coordinates 
 * @param goal the goal coordinates
 * @param walkable the map
 */
	public AStar(Node start, Node goal, boolean[][] walkable) {
		this.start = start;
		
		this.goal = goal;
		width = walkable.length;
		height = walkable[0].length;
		heuristicMap = new float[width][height];
		movementCostMap = new float[width][height]; // g score
		previousNodeMap = new HashMap<Node, Node>();
		totalCostMap = new float[width][height]; // f score
		
		openSet = new PriorityQueue<>((l, r) -> Float.compare(heuristicMap[l.getX()][l.getY()], heuristicMap[r.getX()][r.getY()]));
		closeSet = new HashSet<>();
		// setting all default values
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float dx = x - start.getX();
				float dy = y - start.getY();
				heuristicMap[x][y] = (float)Math.sqrt(dx * dx + dy * dy);

				movementCostMap[x][y] = Integer.MAX_VALUE;

				totalCostMap[x][y] = Integer.MAX_VALUE;
			}
		}

		// creating obstacles in the map
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y++) {
				if (walkable[x][y]){
					closeSet.add(new Node(x,y));
				}
		
			}
		}
		
		//this.finalPath = findRoute(start, goal);
	}
	/*
	 * finds the path to the goal
	 * @returns an ArrayList of nodes which is the route from start to goal
	 */
	public ArrayList<Node> findRoute() {
		// adding start node to the openSet
		long startTime = System.nanoTime();
		
		openSet.add(start);
		Node currentNode;
		
		ArrayList<Node> route = null;
		movementCostMap[start.getX()][start.getY()] = 0;
		while (!openSet.isEmpty()) {
		
			currentNode = openSet.remove();
			if (closeSet.contains(currentNode))
				continue;
			
			closeSet.add(currentNode);
			
			if (currentNode.equals(goal)) {
				// return completed path
				route = constructPath(currentNode);
				break;
			}
			
			for (int x = currentNode.getX() - 1; x <= currentNode.getX() + 1; x++) {
				for (int y = currentNode.getY() - 1; y <= currentNode.getY() + 1; y++) {
					if (x < width && x >= 0 && y >= 0 ) {
						Node neighbour = new Node(x, y);
						
						if (closeSet.contains(neighbour)) {
							// go to next loop
							continue;
						}
						
						float tentativeGScore = movementCostMap[currentNode.getX()][currentNode.getY()] + 1;
						
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
		double time = dt / (double)Util.NANOS_PER_SECOND;
		System.out.println("[Game]: [AStar]: AStar timing: " + time + "s");
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
	/*
	 * finds the final path
	 * @returns the path from start to goal
	 */
	public ArrayList<Node> getPath() {
		return this.finalPath;
	}
}