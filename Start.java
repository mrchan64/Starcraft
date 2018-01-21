import bc.*;
import java.util.*;

public class Start {

	public static Direction[] directions = Direction.values();
	public static boolean isRandom = false;
	public static ArrayList<MapLocation> factories = new ArrayList<>();
	
	public static int runTurn(GameController gc, VecUnit units){
		
		Unit unit;
		MapLocation loc;
		int numWorkers = 0;
		int size = (int)units.size();
		int x = 0;
		int y = 0;
		
		for(int i = 0; i < size; i++) {
			unit = units.get(i);
			loc = unit.location().mapLocation();
			x += loc.getX();
			y += loc.getY();
			
			if(unit.unitType() == UnitType.Worker) numWorkers++;
		}
		
		if(size > 0) {
			x /= size;
			y /= size;
		}
			
		loc = new MapLocation(Planet.Earth, x, y);
		for(int i = 0; i < size; i++) {
			unit = units.get(i);
			moveToClosestDirection(gc, unit, loc, Start.isRandom);
			
			for(Direction dir : directions) {
				if(gc.canHarvest(unit.id(), dir)) {
					gc.harvest(unit.id(), dir);
					break;
				}
			}
		}
		
		if(numWorkers <= 15) {
			replicate(gc, units);
		}
		else {
			buildFactory(gc, units);
			return 1;
		}
		
		Start.isRandom = !Start.isRandom;
		return 0;
	}

	private static void moveToClosestDirection(GameController gc, Unit unit, MapLocation loc, boolean isRandom) {
		
		Direction ideal;
		
		int mean = (Pathfinding.eHeight + Pathfinding.eWidth)/2;
		if(!loc.isWithinRange(mean/3, unit.location().mapLocation())) {
			isRandom = true;
		}
		
		if(isRandom) {
			ideal = Pathfinding.getPath(loc);
		}
		
		else {
			ideal = loc.directionTo(unit.location().mapLocation());
		}
		
		int index = linearSearch(directions, ideal);
		Direction actual = ideal;
		int unitId = unit.id();
		
		for(int i = 0; i < 5; i++) {

			actual = directions[(index + i) % 8];
			if(gc.canMove(unitId, actual) && gc.isMoveReady(unitId)) {
				gc.moveRobot(unitId, actual);
				break;
			}

			if(i == 0 || i == 4) continue;
			actual = directions[(index - i + 8) % 8];
			if(gc.canMove(unitId, actual) && gc.isMoveReady(unitId)) {
				gc.moveRobot(unitId, actual);
				break;
			}
		}
	}

	private static int linearSearch(Direction[] array, Direction dir) {
		
		for(int i = 0; i < 8; i++) {
			if(array[i] == dir) return i;
		}
		return 999;
	}
	
	private static void replicate(GameController gc, VecUnit units) {
		
		Unit unit;
		int unitId;
		
		for(int i = 0; i < units.size(); i++) {
			unit = units.get(i);
			unitId = unit.id();
			
			for(Direction dir : Direction.values()) {
				if(gc.canReplicate(unitId, dir)) {
					gc.replicate(unitId, dir);
					break;
				}
			}
		}
	}
	
	private static void buildFactory(GameController gc, VecUnit units) {
		
		Unit unit;
		int unitId;
		Unit idealUnit = units.get(0);
		MapLocation ideal;
		Direction idealDir = Direction.North;
		Direction[] allDirs = Direction.values();
		int bestOption = 0;
		int numOccupiable = 0;
		
		for(int i = 0; i < units.size(); i++) {
			unit = units.get(i);
			unitId = unit.id();

			for(Direction dir : allDirs) {
				if(gc.canBlueprint(unitId, UnitType.Factory, dir)) {
					
					idealDir = dir;
					ideal = unit.location().mapLocation().add(dir);
					for(Direction dir1 : allDirs) {
						if(gc.isOccupiable(ideal.add(dir1)) == 1) {
							numOccupiable++;
						}
					}
					
					if(numOccupiable == 8) {
						gc.blueprint(unitId, UnitType.Factory, dir);
						factories.add(unit.location().mapLocation().add(dir)); 
						return;
					}
					
					else if (numOccupiable > bestOption) {
						idealDir = dir;
						bestOption = numOccupiable;
						idealUnit = unit;
					}
				}
			}
		}
		gc.blueprint(idealUnit.id(), UnitType.Factory, idealDir);
		factories.add(idealUnit.location().mapLocation().add(idealDir)); 
	}
}
