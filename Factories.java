import bc.*;

public class Factories {

	public static void sendUnits(GameController gc, Unit[] units, MapLocation factory) {
		
		VectorField toFactory = new VectorField();
		MapLocation currLoc;
		Unit unit;
		toFactory.setTarget(factory);
		
		for(int i = 0; i < units.length; i++) {
			unit = units[i];
			currLoc = unit.location().mapLocation();
			
			if(!currLoc.isAdjacentTo(factory)) {
				moveUnit(gc, unit, toFactory.getDirection(currLoc));
				
				System.out.println(toFactory);
			}
			
		}
	}
	
	public static void moveUnit(GameController gc, Unit unit, Direction dir) {
		
		int unitId = unit.id();

		if(gc.canMove(unitId, dir) && gc.isMoveReady(unitId)) {
			gc.moveRobot(unitId, dir);
		}
	}
	
	public static Unit[] getClosest(GameController gc, VecUnit units, MapLocation factory) {
		
		VectorField toFactory = new VectorField();
		toFactory.setTarget(factory);
		
		int numOpenSpaces = getOpenSpaces(gc, factory);
		
		Unit[] closestUnits = new Unit[numOpenSpaces];
		int[] magnitudes = new int[numOpenSpaces];
		Unit unit = units.get(0);
		int magnitude = toFactory.getMagnitude(unit.location().mapLocation());
		int last = 1;
	
		closestUnits[0] = unit;
		magnitudes[0] = magnitude;
		
		for(int i = 1; i < units.size(); i++) {
			
			unit = units.get(i);
			magnitude = toFactory.getMagnitude(unit.location().mapLocation());
			
			for(int j = last-1; j >= 0; j--) {
				if(magnitude < magnitudes[j]) {
					if(j + 1 < numOpenSpaces) {
						magnitudes[j+1] = magnitudes[j];
						closestUnits[j+1] = closestUnits[j];
					}
					magnitudes[j] = magnitude;
					closestUnits[j] = unit;
				}
				else {
					if(j + 1 < numOpenSpaces) {
						magnitudes[j+1] = magnitude;
						closestUnits[j+1] = unit;
					}
					break;
				}
			}
			
			if(last < 7) last++;
		}	
		
		return closestUnits;
	}
	
	public static int getOpenSpaces(GameController gc, MapLocation loc) {
		
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
		
		return num;
	}
}
