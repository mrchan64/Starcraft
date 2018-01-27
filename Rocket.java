import bc.*;
import java.util.ArrayList;

public class Rocket {

	public static VectorField toRocket;
	public static MapLocation rocketLoc;
	public static Unit unit;
	public static int unitId;
	public static MapLocation unitLoc;
	public static Unit[] closestUnits;
	public static ArrayList<Unit> onMars = new ArrayList<>();
	
	public static int loaded;

	public static void runTurn(GameController gc, ArrayList<Unit> units) {

		if (Start.rockets.size() == 0) {
			buildRocket(gc, units);
			return;
		}

		for (Unit rocket : Start.rockets) {

			toRocket = new VectorField();
			rocketLoc = rocket.location().mapLocation();
			toRocket.setTarget(rocketLoc);
			
			closestUnits = Factories.getClosest(gc, Player.availableUnits, rocket, toRocket);

			Factories.sendUnits(gc, closestUnits, rocket, toRocket);

			for (int i = 0; i < closestUnits.length; i++) {

				unit = closestUnits[i];
				for (int j = 0; j < Player.availableUnits.size(); j++) {

					if (unit.equals(Player.availableUnits.get(j))) {
						Player.availableUnits.remove(unit);
						j--;
					}
				}
			}
		}

		for (int i = 0; i < Player.availableUnits.size(); i++) {

			unit = Player.availableUnits.get(i);
			unitId = unit.id();

			if (unit.location().isInGarrison() || unit.location().isInSpace()) {
				continue;
			}
			unitLoc = unit.location().mapLocation();

			if (gc.isMoveReady(unitId) && (!unitLoc.isAdjacentTo(rocketLoc) || Start.rockets.size() == 0)) {
				Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unitLoc));
			}
		}
	}
	
	public static void loadUnits(GameController gc, Unit rocket, ArrayList<Unit> units) {
		
		int rocketId = rocket.id();
		MapLocation rocketLoc = rocket.location().mapLocation();
		toRocket.setTarget(rocketLoc);
		Unit[] astros = Factories.getClosest(gc, units, rocket, toRocket);
		MapLocation destination;

		for (int i = 0; i < astros.length; i++) {
			
			int unitId = astros[i].id();
			if (gc.canLoad(rocketId, unitId)) {
				gc.load(rocketId, unitId);
				Player.availableUnits.remove(gc.unit(unitId));
				loaded++;
				}
			else if(loaded < 8) {
				Factories.sendUnits(gc, astros, rocket, toRocket);
			}
			else if(loaded >= 8) {
				int x = 0;
				int y = 0;
				int random = (int) Math.floor(Math.random() * 10);
				int counter = 0;
				for (int ii = 0; ii < findKarbonite.mWidth; ii++) {
					for (int j = 0; j < findKarbonite.mHeight; j++) {
						if(counter != random) {
							counter++;
							continue;
						}
						if (findKarbonite.availMars[ii][j] == 1) {
							x = ii;
							y = j;
							findKarbonite.availMars[ii][j] = 0;
						}
						destination = new MapLocation(Planet.Mars, x, y);
						if (gc.canLaunchRocket(rocketId, destination)) {
							gc.launchRocket(rocketId, destination);
							onMars.add(rocket);
							UnitBuildOrder.builtRocks.remove(rocket);
							loaded = 0;
							break;
						}
					}
				}
			}
		}
	}

	public static Unit[] getClosest(GameController gc, ArrayList<Unit> units, VectorField toSpawn) {

		if(units.size() == 0) {
			return new Unit[0];
		}
		
		int numRocketUnits = 8 >= units.size() ? units.size() : 8;

		int size = units.size();
		Unit[] closestUnits = new Unit[numRocketUnits];
		int[] magnitudes = new int[numRocketUnits];
		Unit unit = units.get(0);
		Location unitLoc;
		int magnitude;
		int last = 0;
		int place = 0;
		MapLocation currloc;
		
		closestUnits[0] = units.get(0);
		magnitudes[0] = toSpawn.getMagnitude(unit.location().mapLocation());

		for (int i = 0; i < size; i++) {
			
			unit = units.get(i);
			unitLoc = unit.location();
			if (unit.unitType() != UnitType.Worker || unitLoc.isInGarrison() || unitLoc.isInSpace())
				continue;
			
			currloc = unit.location().mapLocation();

			magnitude = toSpawn.getMagnitude(currloc);

			for (place = last; place > 0; place--) {
				if(place == numRocketUnits && magnitudes[place - 1] <= magnitude)
					break;
				else if(place == numRocketUnits && magnitudes[place - 1] > magnitude) {
					magnitudes[place - 1] = magnitude;
					closestUnits[place - 1] = unit;
				}
				else if (magnitudes[place - 1] > magnitude) {
					magnitudes[place] = magnitudes[place - 1];
					closestUnits[place] = closestUnits[place - 1];
					magnitudes[place - 1] = magnitude;
					closestUnits[place - 1] = unit;
				}
				else {
					closestUnits[place] = unit;
					magnitudes[place] = magnitude;
					break;
				}
			}
			last = last >= numRocketUnits ? numRocketUnits : last + 1;
		}
		
		return closestUnits;
	}

	private static boolean buildRocket(GameController gc, ArrayList<Unit> units) {
		
		if (units.size() == 0) {
			return false;
		}

		VectorField toSpawn = new VectorField();
		toSpawn.setTarget(Start.spawn);
		
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
		
		Unit[] rocketUnits = getClosest(gc, units, toSpawn);
		System.out.println("rockets, rocketUnits: " + rocketUnits.length + ", units: " + units.size());
		int x, y;

		for (int i = 0; i < rocketUnits.length; i++) {
			unit = rocketUnits[i];
			unitId = unit.id();

			for (Direction dir : allDirs) {
				if (dir == Direction.Center)
					continue;
				
				if (gc.canBlueprint(unitId, UnitType.Rocket, dir)) {

					attempt = unit.location().mapLocation().add(dir);
					for (Direction dir1 : allDirs) {
						try {
							if (dir1 == Direction.Center)
								continue;

							attemptAround = attempt.add(dir1);
							x = attemptAround.getX();
							y = attemptAround.getY();

							if (VectorField.terrain[x][y] == 1) {
								numOccupiable++;
							}
						} catch (Exception E) {
							// do nothing
						}
					}

					if (numOccupiable >= 8) {
						gc.blueprint(unitId, UnitType.Rocket, dir);
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

		if (gc.canBlueprint(idealUnitId, UnitType.Rocket, idealDir)) {
			gc.blueprint(idealUnitId, UnitType.Rocket, idealDir);
			System.out.println("Rocket blueprinted");
			return true;
		}

		return false;
	}

}