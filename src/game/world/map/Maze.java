package game.world.map;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class for generating mazes.
 * <p>
 * Co-ordinates are relative to the bottom-left.
 * 
 * @author Callum
 */
public class Maze {
	/** North (Up) */
	private static final byte N = 1;
	/** South (Down) */
	private static final byte S = 2;
	/** East (Right) */
	private static final byte E = 4;
	/** West (Left) */
	private static final byte W = 8;
	
	/** Random number generator */
	private Random rng;
	
	/** Width */
	private int w;
	/** Height */
	private int h;
	
	private int startX;
	private int startY;
	
	private int endX;
	private int endY;
	
	/** Maze data */
	private byte[] data;
	
	public Maze(Random _rng, int _w, int _h, int _startX, int _startY, int _endX, int _endY) {
		this.rng = _rng;
		this.w = _w;
		this.h = _h;
		this.startX = _startX;
		this.startY = _startY;
		this.endX = _endX;
		this.endY = _endY;
		this.data = new byte[w*h];
		
		if (isoob(startX, startY)) {
			throw new IllegalArgumentException("start coordinate must be within maze bounds");
		} else if (isoob(endX, endY)) {
			throw new IllegalArgumentException("end coordinate must be within maze bounds");
		} else if (rng == null) {
			throw new IllegalArgumentException("rng must be initialized");
		}
		
		generateMaze();
	}
	
	private byte getDir(int x, int y) {
		return data[y * w + x];
	}
	private void setDir(int x, int y, byte dir) {
		data[y * w + x] = dir;
	}
	private void addDir(int x, int y, byte dir) {
		data[y * w + x] |= dir;
	}
	private boolean isDir(int x, int y, byte dir) {
		return (getDir(x, y) & dir) != 0;
	}
	
	private int applyDirX(int x, byte dir) {
		if (dir == E) return x + 1;
		if (dir == W) return x - 1;
		return x;
	}
	private int applyDirY(int y, byte dir) {
		if (dir == N) return y + 1;
		if (dir == S) return y - 1;
		return y;
	}
	
	/**
	 * Is the coordinate given out of bounds?
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	private boolean isoob(int x, int y) {
		return x < 0 || x > w-1 || y < 0 || y > h-1;
	}
	
	private void generateMaze() {
		byte startDir = 0;
		if (startX == 0  ) startDir |= W;
		if (startX == w-1) startDir |= E;
		if (startY == 0  ) startDir |= S;
		if (startY == h-1) startDir |= N;
		if (startDir == 0)
			throw new IllegalArgumentException("start co-ordinate needs to be on the edge of the maze");
		
		byte endDir = 0;
		if (endX == 0  ) endDir |= W;
		if (endX == w-1) endDir |= E;
		if (endY == 0  ) endDir |= S;
		if (endY == h-1) endDir |= N;
		
		if (endDir == 0)
			throw new IllegalArgumentException("end co-ordinate needs to be on the edge of the maze");
		
		addDir(startX, startY, startDir);
		
		// Randomized depth first search is the algorithm, essentially.
		dfs(startX, startY);
		
		addDir(endX, endY, endDir);
	}
	
	private int dirCacheLen = 0;
	private byte[] dirCache = new byte[4];
	
	private void pushDir(byte dir) {
		dirCache[dirCacheLen++] = dir;
	}
	private void shuffleDirCache() {
		// Fisher-Yates Shuffle
		for (int i = dirCacheLen-1; i >= 0; i--) {
			int j = rng.nextInt(i+1);
			// Swap elements
			byte temp = dirCache[i];
			dirCache[i] = dirCache[j];
			dirCache[j] = temp;
		}
	}
	
	private void dfs(int x, int y) {
		while (true) {
			// Get possible directions
			dirCacheLen = 0;
			byte dirs = getDir(x, y);
			if ((dirs & N) == 0 && y < h-1 && getDir(x, y+1) == 0) pushDir(N);
			if ((dirs & S) == 0 && y > 0   && getDir(x, y-1) == 0) pushDir(S);
			if ((dirs & E) == 0 && x < w-1 && getDir(x+1, y) == 0) pushDir(E);
			if ((dirs & W) == 0 && x > 0   && getDir(x-1, y) == 0) pushDir(W);
			
			if (dirCacheLen == 0) // If no more exploration opportunities, return
				return;
			
			// Get random direction
			int i;
			if (dirCacheLen == 1) i = 0;
			else                  i = rng.nextInt(dirCacheLen);
			byte dir = dirCache[i];
			
			// Apply direction
			addDir(x, y, dir);
			int xp = applyDirX(x, dir);
			int yp = applyDirY(y, dir);
			addDir(xp, yp, oppositeDirection(dir));
			
			// Call recursively
			dfs(xp, yp);
		}
	}
	
	private byte oppositeDirection(byte dir) {
		switch (dir) {
		case N: return S;
		case S: return N;
		case E: return W;
		case W: return E;
		default: throw new RuntimeException("Illegal direction: " + dir);
		}
	}

	public float[] toLines() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder((3*w+1)*3*h);
		for (int y = h-1; y >= 0; y--) {
			boolean lastY = y == 0;
			
			for (int x = 0; x < w; x++) {
				b.append('+');
				if (isDir(x, y, N)) b.append(' ');
				else                b.append('-');
				
				if (x == w-1)
					b.append('+');
			}
			b.append('\n');
			for (int x = 0; x < w; x++) {
				if (isDir(x, y, W)) b.append(' ');
				else                b.append('|');
				
				if      (x == startX && y == startY) b.append('S');
				else if (x == endX   && y == endY  ) b.append('E');
				else                                 b.append(' ');
				
				if (x == w-1) {
					if (isDir(x, y, E)) b.append(' ');
					else                b.append('|');
				}
			}
			if (lastY) {
				b.append('\n');
				for (int x = 0; x < w; x++) {
					b.append('+');
					if (isDir(x, y, S)) b.append(' ');
					else                b.append('-');
					if (x == w-1)
						b.append('+');
				}
			} else {
				b.append('\n');
			}
		}
		return b.toString();
	}
}
