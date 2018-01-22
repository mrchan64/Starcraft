import bc.*;
import java.util.*;

public class Start {

	public static Direction[] directions = Direction.values();
	public static boolean toKarbonite = false;
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
			moveToClosestDirection(gc, unit, loc, Start.toKarbonite);
			
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
			if(buildFactory(gc, units)) return 1;
		}
		
		Start.toKarbonite = !Start.toKarbonite;
		return 0;
	}

	private static void moveToClosestDirection(GameController gc, Unit unit, MapLocation loc, boolean toKarbonite) {
		
		Direction ideal;
		
		int mean = (Pathfinding.eHeight + Pathfinding.eWidth)/2;
		if(!loc.isWithinRange(mean/3, unit.location().mapLocation())) {
			toKarbonite = true;
		}
		
		if(toKarbonite) {
			ideal = Direction.North;
			//ideal = Pathfinding.getPath(loc);
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
	
	private static boolean buildFactory(GameController gc, VecUnit units) {
		
		Unit unit;
		int unitId;
		Unit idealUnit = units.get(0);
		int idealUnitId = idealUnit.id();
		MapLocation attempt;
		MapLocation attemptAround;
		Direction idealDir = Direction.North;
		Direction[] allDirs = Direction.values();
		int bestOption = 0;
		int numOccupiable = 0;
		
		int x, y;
		
		for(int i = 0; i < units.size(); i++) {
			unit = units.get(i);
			unitId = unit.id();

			for(Direction dir : allDirs) {
				if(dir == Direction.Center) continue;
				if(gc.canBlueprint(unitId, UnitType.Factory, dir)) {
					
					attempt = unit.location().mapLocation().add(dir);
					for(Direction dir1 : allDirs) {
						try {
							if(dir1 == Direction.Center) continue;
							
							attemptAround = attempt.add(dir1);
							x = attemptAround.getX();
							y = attemptAround.getY();
							
							if(VectorField.terrain[x][y] == 1) {
								numOccupiable++;
							}
						}
						catch(Exception E) {
							// do nothing
						}
					}
					
					if(numOccupiable == 8) {
						gc.blueprint(unitId, UnitType.Factory, dir);
						factories.add(unit.location().mapLocation().add(dir));
						Player.numFactories++;
						Player.factoriesBuilt = false;
						return true;
					}
					
					else if (numOccupiable > bestOption) {
						idealDir = dir;
						bestOption = numOccupiable;
						idealUnit = unit;
						idealUnitId = idealUnit.id();
					}
				}
				
				numOccupiable = 0;
			}
		}
		
		if(gc.canBlueprint(idealUnitId, UnitType.Factory, idealDir)) {
			gc.blueprint(idealUnitId, UnitType.Factory, idealDir);
			factories.add(idealUnit.location().mapLocation().add(idealDir));
			Player.numFactories++;
			Player.factoriesBuilt = false;
			return true;
		}
		
		return false;
	}
}
