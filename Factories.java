import bc.*;
import java.util.ArrayList;
import java.util.Random;

public class Factories {

	public static VectorField toFactory;
	public static MapLocation factory;
	public static Unit unit;
	public static int unitId;
	public static MapLocation unitLoc;
	public static Unit[] closestUnits;
	public static boolean isClose;

	public static void runTurn(GameController gc, ArrayList<Unit> units) {
		
		detectClose(gc);
		
		for (Unit fac : Start.factories) {

			toFactory = new VectorField();
			factory = fac.location().mapLocation();
			toFactory.setTarget(factory);

			closestUnits = Factories.getClosest(gc, Player.availableUnits, factory, toFactory, true);
			sendUnits(gc, closestUnits, fac, toFactory);
			
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
			
			if (gc.isMoveReady(unitId) && !unitLoc.isAdjacentTo(factory)) {
				Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unitLoc));
			}
		}
	}

	public static void sendUnits(GameController gc, Unit[] units, Unit structure, VectorField toStructure) {

		MapLocation currLoc;
		Unit unit;
		int unitId;
		int structureId = structure.id();

		for (int i = 0; i < units.length; i++) {

			unit = units[i];
			unitId = unit.id();
			currLoc = unit.location().mapLocation();

			if (!currLoc.isAdjacentTo(structure.location().mapLocation())) {
				moveToClosestDirection(gc, unit, toStructure.getDirection(currLoc));
			}

			else {
				if (gc.isMoveReady(unitId)) {
					moveAroundUnit(gc, unit, structureId);
				}

				if (gc.canBuild(unitId, structureId)) {
					gc.build(unitId, structureId);
				}

				else if (gc.canRepair(unitId, structureId)) {
					gc.repair(unitId, structureId);
				}
			}
		}
	}

	public static void moveAroundUnit(GameController gc, Unit unit, int structureId) {

		ArrayList<MapLocation> possibleLocs;
		try{
			possibleLocs = adjacent(gc, gc.unit(structureId).location().mapLocation());
		}
		catch(Exception e){
			possibleLocs = new ArrayList<MapLocation>();
		}
		ArrayList<Direction> possibleDirs = new ArrayList<>();

		MapLocation unitLoc = unit.location().mapLocation();
		MapLocation attempt;

		int unitId = unit.id();

		for (int i = 0; i < Start.directions.length; i++) {
			if (Start.directions[i] == Direction.Center)
				continue;

			attempt = unitLoc.add(Start.directions[i]);

			for (int j = 0; j < possibleLocs.size(); j++)
				if (possibleLocs.get(j).equals(attempt)) {
					possibleDirs.add(Start.directions[i]);
				}
		}

		Direction toMove;

		for (int i = 0; i < possibleDirs.size(); i++) {

			toMove = possibleDirs.get(i);
			if (gc.canMove(unitId, toMove)) {
				gc.moveRobot(unitId, toMove);
				break;
			}
		}
	}

	public static ArrayList<MapLocation> adjacent(GameController gc, MapLocation loc) {

		ArrayList<MapLocation> adjacentLocs = new ArrayList<>(8);
		MapLocation newLoc;

		for (int i = 0; i < Start.directions.length; i++) {
			if (Start.directions[i] == Direction.Center)
				continue;

			newLoc = loc.add(Start.directions[i]);
			try {
				if (gc.isOccupiable(newLoc) == 1) {
					adjacentLocs.add(loc.add(Start.directions[i]));
				}
			} catch (Exception e) {
				// skip this loc
			}
		}

		return adjacentLocs;
	}

	public static Unit[] getClosest(GameController gc, ArrayList<Unit> units, MapLocation structureLoc, VectorField toFactory, boolean forFactory) {
		
		if(units.size() == 0) {
			return new Unit[0];
		}

		int size = units.size();
		Unit unit = units.get(0);
		Location unitLoc = unit.location();
		MapLocation currLoc = unitLoc.mapLocation();
		int magnitude;
		int last = 0;
		int place = 0;
		int unitsReady = units.size();
		
		int numOpenSpaces = getOpenSpaces(gc, structureLoc);
		
		int numAdjacent = 0;
		int numInRange;
		int rangeToCheck;
		
		for(Direction dir : Start.directions) {
			try {
				if(gc.isOccupiable(structureLoc.add(dir)) == 1) numAdjacent++;
			}
			catch (Exception E) {
				//do nothing
			}
		}
		
		if(!forFactory) {
			numInRange = 8;
		}
		
		else {
			if(numAdjacent == 0) {
				numInRange = 8;
			}
	
			else {
				Unit structure = gc.senseUnitAtLocation(structureLoc);
				int health = (int)structure.health();
				rangeToCheck = (int)(((300 - health) / numAdjacent / 5) * ((300 - health) / numAdjacent / 5));
				numInRange = (int)(gc.senseNearbyUnitsByTeam(structureLoc, rangeToCheck, Player.team).size());
			}
		}
		
		if (numOpenSpaces > numInRange) numOpenSpaces = numInRange;
		if (numOpenSpaces > unitsReady) numOpenSpaces = unitsReady;
		
		if(numOpenSpaces == 0) return new Unit[0];
		
		Unit[] closestUnits = new Unit[numOpenSpaces];
		int[] magnitudes = new int[numOpenSpaces];
		
		closestUnits[0] = units.get(0);
		magnitudes[0] = toFactory.getMagnitude(currLoc);

		for (int i = 0; i < size; i++) {
			
			unit = units.get(i);
			unitLoc = unit.location();
			if (unit.unitType() != UnitType.Worker || unitLoc.isInGarrison() || unitLoc.isInSpace())
				continue;
			
			currLoc = unitLoc.mapLocation();

			magnitude = toFactory.getMagnitude(currLoc);

			for (place = last; place > 0; place--) {
				if(place == numOpenSpaces && magnitudes[place - 1] <= magnitude)
					break;
				else if(place == numOpenSpaces && magnitudes[place - 1] > magnitude) {
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
			last = last >= numOpenSpaces ? numOpenSpaces : last + 1;
		}
		
		return closestUnits;
	}

	public static int getOpenSpaces(GameController gc, MapLocation loc) {

		MapLocation locAround;
		int x, y;
		int num = 0;

		for (Direction dir : Direction.values()) {
			if (dir == Direction.Center)
				continue;
			try {

				locAround = loc.add(dir);
				x = locAround.getX();
				y = locAround.getY();

				if (VectorField.terrain[x][y] == 1) {
					num++;
				}
			} catch (Exception E) {
				// do nothing
			}
		}

		return num;
	}

	public static void moveToClosestDirection(GameController gc, Unit unit, Direction ideal) {

		Direction actual = ideal;
		int index = Start.linearSearch(Start.directions, ideal);
		int unitId = unit.id();

		if (ideal == Direction.Center) {
			index = (int) (Math.random() * 8);
		}

		for (int i = 0; i < 4; i++) {

			actual = Start.directions[(index + i) % 8];
			if (gc.canMove(unitId, actual) && gc.isMoveReady(unitId)) {
				gc.moveRobot(unitId, actual);
				break;
			}

			if (i == 0)
				continue;

			actual = Start.directions[(index - i + 8) % 8];
			if (gc.canMove(unitId, actual) && gc.isMoveReady(unitId)) {
				gc.moveRobot(unitId, actual);
				break;
			}
		}
	}
	
	public static void buildSpawnFactory(GameController gc, ArrayList<Unit> units, MapLocation spawn) {
		
		Unit unit;
		int unitId;
		int size = units.size();
		Unit idealUnit = units.get(0);
		int idealUnitId = idealUnit.id();
		MapLocation attempt;
		MapLocation attemptAround;
		Direction idealDir = Direction.North;
		Direction[] allDirs = Direction.values();
		int bestOption = 0;
		int numOccupiable = 0;
		VectorField toSpawn = new VectorField();
		toSpawn.setTarget(spawn);
		Unit[] closestUnits = getClosest(gc, units, spawn, toSpawn, false);
		
		if(closestUnits.length == 0) {
			Start.spawnsDone++;
			return;
		}
		
		int x, y;
		
		for(int i = 0; i < closestUnits.length; i++) {
			unit = closestUnits[i];
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
					
					if(numOccupiable > 7) {
						gc.blueprint(unitId, UnitType.Factory, dir);
						Start.spawnsDone++;

						return;
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
			Start.spawnsDone++;
		}
	}
	
	public static void buildFactory(GameController gc, ArrayList<Unit> units) {
		
		Random generator = new Random();
		Unit unit;
		int unitId;
		int size = units.size();
		Unit idealUnit = units.get(0);
		int idealUnitId = idealUnit.id();
		MapLocation attempt;
		MapLocation attemptAround;
		Direction idealDir = Direction.North;
		Direction[] allDirs = Direction.values();
		int bestOption = 0;
		int numOccupiable = 0;
		int startPoint = generator.nextInt(size);
		
		int x, y;
		
		for(int i = 0; i < size; i++) {
			unit = units.get((i + startPoint) % size);
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
					
					if(numOccupiable > 7) {
						gc.blueprint(unitId, UnitType.Factory, dir);
						return;
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
		}
	}
	
	private static void detectClose(GameController gc){
		isClose = false;
		Team t = Player.team == Team.Red ? Team.Red : Team.Blue;
		for(Unit unit: UnitBuildOrder.builtFacts){
			try{
				gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 25, t);
				isClose = true;
			}catch(Exception e){}
		}
	}
}
