import bc.*;

public class Minesweeper {
	public static int[][] mineMap;
	public static int density;
	public static boolean isDense;
	public static int[][] densityMap;
	public static int bestX;
	public static int bestY;
	public static int highest = 0;

	public static void mineSweep(MapLocation loc, PlanetMap map) {
		if (map.isPassableTerrainAt(loc) == 0) {
			mineMap[loc.getX()][loc.getY()] = 0;
			return;
		}
		int startX = loc.getX();
		int startY = loc.getY();

		MapLocation locAround;
		int x, y;
		int num = 0;
		
		for(Direction dir : Direction.values()) {
			if(dir == Direction.Center) continue;
			try {
				locAround = loc.add(dir);
				x = locAround.getX();
				y = locAround.getY();
				if (x < 0 || y < 0 || x == findKarbonite.mWidth || y == findKarbonite.mHeight) {
					continue;
				}
				if(map.isPassableTerrainAt(locAround) == 1) {
					num++;
				}
			}
			catch(Exception E) {
				// do nothing
			}
		}
		mineMap[startX][startY] = num;
		if (num > highest) {
			highest = num;
		}
		density += num;
	}

	public static void denseMap() {
		if (density/findKarbonite.avaSq > 4) {
			isDense = true;
		}
		isDense = false;
	}

	public static void updateMap(int x, int y) {
		if (y - 1 >= 0) {
			mineMap[x][y-1] -= 1;
			if (x-1 >=0) {
				mineMap[x-1][y-1] -= 1;
				mineMap[x-1][y] -= 1;
			}
			if (x+1 < findKarbonite.mWidth) {
				mineMap[x+1][y-1] -= 1;
				mineMap[x+1][y] -= 1;
			}
		}

		if (y+1 < findKarbonite.mHeight) {
			mineMap[x][y+1] -= 1;
			if (x-1 >=0) {
				mineMap[x-1][y+1] -= 1;
			}
			if (x+1 < findKarbonite.mWidth) {
				mineMap[x+1][y+1] -= 1;
			}
		}
		highest = 0;
		for (int i = 0; i < findKarbonite.mWidth; i++) {
			for (int j = 0; j < findKarbonite.mHeight; j++) {
				if (mineMap[i][j] > highest) {
					highest = mineMap[i][j];
				}
			}
		}

	}

	/*public static void addNeighbors(MapLocation loc) {
		int startX = loc.getX();
		int startY = loc.getY();

		MapLocation locAround;
		int x, y;
		int num = 0;
		
		for(Direction dir : Direction.values()) {
			if(dir == Direction.Center) continue;
			try {
				locAround = loc.add(dir);
				x = locAround.getX();
				y = locAround.getY();
				
				num += mineMap[x][y];
			}
			catch(Exception E) {
				// do nothing
			}
		}
		densityMap[startX][startY] = num;
	}*/

	public static void bestSquare() {
		int max = 0;
		for (int x = 0; x < findKarbonite.mWidth; x++) {
			for (int y = 0; y < findKarbonite.mHeight; y++) {
				if (densityMap[x][y] > max) {
					max = densityMap[x][y];
					bestX = x;
					bestY = y;
				}
			}
		}
		densityMap[bestX][bestY] = 0;
	}
}