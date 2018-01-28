import bc.*;
import java.util.ArrayList;

public class Rocket {

	public static VectorField toRocket;
	public static MapLocation rocketLoc;
	public static Unit unit;
	public static int unitId;
	public static MapLocation unitLoc;
	public static Unit[] closestUnits;
	public static boolean sentFirst = false;
	public static boolean fighterLoaded = false;

	public static void runTurn(GameController gc, ArrayList<Unit> units) {

		if (Start.rockets.size() == 0 || gc.karbonite() > 150) {
			buildRocket(gc, units);
			return;
		}

		for (Unit rocket : Start.rockets) {

			toRocket = new VectorField();
			rocketLoc = rocket.location().mapLocation();
			toRocket.setTarget(rocketLoc);
			
			closestUnits = Factories.getClosest(gc, Player.availableUnits, rocketLoc, toRocket, true);
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
	
	public static void runFirstTurn(GameController gc, ArrayList<Unit> units) {

		if(Start.rockets.size() == 0 && UnitBuildOrder.builtRocks.size() == 0) {
			buildRocket(gc, units);
			return;
		}

		if (UnitBuildOrder.builtRocks.size() > 0) {
			if(fighterLoaded) loadUnits(gc, UnitBuildOrder.builtRocks.get(0), units);
			else loadClosestFighter(gc,UnitBuildOrder.builtRocks.get(0));

			return;
		}
		
		runTurn(gc, units);
	}

	public static void loadCombatUnits(GameController gc, Unit rocket, ArrayList<Unit> units) {
		int rocketId = rocket.id();
		MapLocation rocketLoc = rocket.location().mapLocation();
		toRocket.setTarget(rocketLoc);
		Unit[] rocketUnits = getClosest(gc, units, toRocket);
		Unit unit;
		int unitId;
		MapLocation unitLoc;
		MapLocation rocketLocation = rocket.location().mapLocation();
		boolean adjacent = false;
		int numAdjacent = units.size();

		for (int i = 0; i < rocketUnits.length; i++) {
			
			unit = rocketUnits[i];
			unitId = rocketUnits[i].id();
			unitLoc = unit.location().mapLocation();
			Player.availableCombatUnits.remove(unit);
			adjacent = unitLoc.isAdjacentTo(rocketLocation);
			
			if (!adjacent) {
				Factories.moveToClosestDirection(gc, unit, toRocket.getDirection(unitLoc));
				numAdjacent--;
			}
		}
		if(numAdjacent >= units.size() - 1) {
			for(int i = 0; i < rocketUnits.length; i++) {
				
				unitId = rocketUnits[i].id();
				if(gc.canLoad(rocketId, unitId)) {
					gc.load(rocketId, unitId);
				}
			}
		}

		VecUnitID sizeInRocket = rocket.structureGarrison();
		if(sizeInRocket.size() > 7 || units.size() == 0) {
			sentFirst = true;
			launchRocket(gc, rocketId);
		}
	}
	
	public static void loadUnits(GameController gc, Unit rocket, ArrayList<Unit> units) {

		int rocketId = rocket.id();
		MapLocation rocketLoc = rocket.location().mapLocation();
		toRocket.setTarget(rocketLoc);
		Unit[] oldRocketUnits = getClosest(gc, units, toRocket);
		Unit unit;
		int unitId;
		MapLocation unitLoc;
		MapLocation rocketLocation = rocket.location().mapLocation();
		boolean adjacent = false;
		int numAdjacent = units.size();
		VecUnitID sizeInRocket = rocket.structureGarrison();
		
		int max; 
		
		if(oldRocketUnits.length == 8) max = 7;
		else max = oldRocketUnits.length;
		
		Unit[] rocketUnits = new Unit[max];
		
		for(int i = 0; i < max; i++) {
			rocketUnits[i] = oldRocketUnits[i];
		}
		
		int numPossible = max;

		for (int i = 0; i < rocketUnits.length; i++) {

			unit = rocketUnits[i];
			unitId = rocketUnits[i].id();
			unitLoc = unit.location().mapLocation();
			Player.availableUnits.remove(unit);
			adjacent = unitLoc.isAdjacentTo(rocketLocation);

			if (!adjacent) {
				Factories.moveToClosestDirection(gc, unit, toRocket.getDirection(unitLoc));
				if(toRocket.getDirection(unitLoc) == Direction.Center) numPossible--;
				numAdjacent--;
			}
		}
	
		if(numAdjacent >= units.size() - 1) {
			for(int i = 0; i < rocketUnits.length; i++) {
				
				unitId = rocketUnits[i].id();
				if(gc.canLoad(rocketId, unitId)) {
					gc.load(rocketId, unitId);
				}
			}
		}

		sizeInRocket = rocket.structureGarrison();
		if(sizeInRocket.size() > max || numPossible == 0) {
			sentFirst = true;
			launchRocket(gc, rocketId);
			Player.workersOnMars = true;
		}
	}
	
	public static void loadClosestFighter(GameController gc, Unit rocket) {
		
		Unit closestFighter = null;
		Unit currFighter;
		int fighterId = 0;
		Location fighterLoc = null;
		Location rocketLocation = rocket.location();
		int rocketId = rocket.id();
		
		int magnitude;
		int closestMagnitude = 1000;
		
		toRocket.setTarget(rocketLocation.mapLocation());
		
		for(Unit combatUnit : Player.availableCombatUnits) {
			
			if(combatUnit.unitType() == UnitType.Healer) continue;
			currFighter = combatUnit;
			magnitude = toRocket.getMagnitude(currFighter.location().mapLocation());
			if(magnitude < closestMagnitude) {
				closestMagnitude = magnitude;
				closestFighter = combatUnit;
			}
		}
		
		if(closestFighter == null) {
			fighterLoaded = true;
			return;
		}

		fighterId = closestFighter.id();
		fighterLoc = closestFighter.location();

		if(!fighterLoc.isAdjacentTo(rocketLocation))
			Factories.moveToClosestDirection(gc, closestFighter, toRocket.getDirection(fighterLoc.mapLocation()));
	
		if(gc.canLoad(rocketId, fighterId)) {
			gc.load(rocketId, fighterId);
			fighterLoaded = true;
		}
	}

	public static void launchRocket(GameController gc, int rocketId) {
		
		int startX = (int) Math.floor(Math.random() *findKarbonite.mWidth/2);
		int startY = (int) Math.floor(Math.random() *findKarbonite.mWidth/2);
		MapLocation destination;
		int x, y;
		
		for (int ii = startX; ii < findKarbonite.mWidth; ii++) {
			for (int j = startY; j < findKarbonite.mHeight; j++) {
				if (Minesweeper.mineMap[ii][j] == Minesweeper.highest) {
					x = ii;
					y = j;

					Minesweeper.mineMap[ii][j] = 0;
					destination = new MapLocation(Planet.Mars, x, y);
					if (gc.canLaunchRocket(rocketId, destination)) {
						gc.launchRocket(rocketId, destination);
						UnitBuildOrder.builtRocks.remove(gc.unit(rocketId));
						Minesweeper.updateMap(x, y);
						return;
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
		Unit unit = units.get(0);
		Location unitLoc = unit.location();
		MapLocation currloc = unitLoc.mapLocation();
		int magnitude;
		int last = 0;
		int place = 0;

		Unit[] closestUnits = new Unit[numRocketUnits];
		int[] magnitudes = new int[numRocketUnits];
		
		closestUnits[0] = units.get(0);
		magnitudes[0] = toSpawn.getMagnitude(unit.location().mapLocation());

		for (int i = 0; i < size; i++) {
			
			unit = units.get(i);
			unitLoc = unit.location();
			if (unitLoc.isInGarrison() || unitLoc.isInSpace())
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
		toSpawn.setTarget(findKarbonite.spawns.get(0));
		
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
			return true;
		}

		return false;
	}

}