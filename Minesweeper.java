import bc.*;

public class Minesweeper {
	public static int[][] mineMap;
	public static int density;
	public static boolean isDense;

	public static void mineSweep(MapLocation loc) {
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
				
				if(VectorField.terrain[x][y] == 1) {
					num++;
				}
			}
			catch(Exception E) {
				// do nothing
			}
		}
		mineMap[startX][startY] = num;
		density += num;
	}

	public static boolean denseMap() {
		if (density/findKarbonite.avaSq > 4) {
			isDense = true;
		}
		isDense = false;
	}

}