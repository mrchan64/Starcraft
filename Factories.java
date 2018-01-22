import bc.*;
import java.util.*;

public class Factories {

	public static void sendUnits(GameController gc, Unit[] units, MapLocation factory) {
		
		VectorField toFactory = new VectorField();
		MapLocation currLoc;
		Unit unit;
		toFactory.setTarget(factory);
		
		for(int i = 0; i < units.length; i++) {
			
			unit = units[i];
			currLoc = unit.location().mapLocation();
			
			while(!currLoc.isAdjacentTo(factory)) {
				moveUnit(gc, unit, toFactory.getDirection(currLoc));
			}
		}
	}
	
	private static void moveUnit(GameController gc, Unit unit, Direction dir) {
		
		int unitId = unit.id();

		if(gc.canMove(unitId, dir) && gc.isMoveReady(unit.id())) {
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
		int last = 0;
	
		closestUnits[0] = unit;
		magnitudes[0] = magnitude;
		
		for(int i = 1; i < units.size(); i++) {
			
			unit = units.get(i);
			magnitude = toFactory.getMagnitude(unit.location().mapLocation());
			
			for(int j = last; j >= 0; j--) {
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
		}	
		
		return closestUnits;
	}
	
	private static int getOpenSpaces(GameController gc, MapLocation loc) {
		
		int num = 0;
		
		for(Direction dir : Direction.values()) {
			if(gc.isOccupiable(loc.add(dir)) == 1) {
				num++;
			}
		}
		
		return num;
	}
}
