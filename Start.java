import bc.*;
import java.util.ArrayList;

public class Start {

	public static Direction[] directions = Direction.values();
	public static ArrayList<Unit> factories = new ArrayList<>();
	public static ArrayList<Unit> rockets = new ArrayList<>();
	
	public static int numWorkers;
	
	public static int runTurn(GameController gc, ArrayList<Unit> units){
		
		Unit unit;
		MapLocation loc;
		int size = (int)units.size();
		int x = 0;
		int y = 0;
		numWorkers = 0;
		
		for(int i = 0; i < size; i++) {
			unit = units.get(i);
			if (unit.location().isOnMap() && !unit.location().isInGarrison() &&
				!unit.location().isInSpace()) {
				loc = unit.location().mapLocation();
				x += loc.getX();
				y += loc.getY();
			}
			else if (unit.location().isInGarrison()) {

			}
		}
		
		updateNumWorkers(units);
		
		if(size > 0) {
			x /= size;
			y /= size;
		}
		
		if(numWorkers <= 8 * (Player.numFactories + 1)) {
			replicate(gc, units);
		}
		
		else {
			if(buildFactory(gc, units)) return 1;
		}
			
		loc = new MapLocation(Planet.Earth, x, y);
		for(int i = 0; i < size; i++) {
			unit = units.get(i);

			goForKarbonite(gc, unit);
		}

		return 0;
	}
	
	public static void updateNumWorkers(ArrayList<Unit> units) {
		for(Unit unit : units) {
			if(unit.unitType() == UnitType.Worker) numWorkers++;
		}
	}

	private static void goForKarbonite(GameController gc, Unit unit) {

		if (unit.location().isInGarrison() || unit.location().isInSpace()) {
			return;
		}

		Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unit.location().mapLocation()));		
	}

	public static int linearSearch(Direction[] array, Direction dir) {
		
		for(int i = 0; i < 8; i++) {
			if(array[i] == dir) return i;
		}
		return 999;
	}
	
	private static void replicate(GameController gc, ArrayList<Unit> units) {
		
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
	
	private static boolean buildFactory(GameController gc, ArrayList<Unit> units) {
		
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
			return true;
		}
		
		return false;
	}
}
