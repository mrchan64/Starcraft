import bc.*;
import java.util.*;

public class Factories {

	public static void sendUnits(GameController gc, Unit[] units, Unit structure, VectorField toStructure) {
		
		MapLocation currLoc;
		Unit unit;
		int unitId;
		int structureId = structure.id();
		
		for(int i = 0; i < units.length; i++) {
			unit = units[i];
			unitId = unit.id();
			currLoc = gc.unit(unitId).location().mapLocation();
			
			if(!currLoc.isAdjacentTo(structure.location().mapLocation())) {
				moveToClosestDirection(gc, unit, toStructure.getDirection(currLoc));
			}
			
			else{
				
				if(gc.isMoveReady(unitId)) {
					moveAroundUnit(gc, unit, structureId);
				}
				
				if(gc.canBuild(unitId, structureId)) {
					gc.build(unitId, structureId);
				}
			}
		}
	}
	
	public static void moveAroundUnit(GameController gc, Unit unit, int structureId) {
		
		ArrayList<MapLocation> possibleLocs = adjacent(gc, gc.unit(structureId).location().mapLocation());
		ArrayList<Direction> possibleDirs = new ArrayList<>();
		
		MapLocation unitLoc = unit.location().mapLocation();
		MapLocation attempt;
		
		int unitId = unit.id();
		
		for(int i = 0; i < Start.directions.length; i++) {
			if(Start.directions[i] == Direction.Center) continue;
			
			attempt = unitLoc.add(Start.directions[i]);
			
			for(int j = 0; j < possibleLocs.size(); j++)
				if(possibleLocs.get(j).equals(attempt)) {
					possibleDirs.add(Start.directions[i]);
				}
		}

		Direction toMove;
		
		for(int i = 0; i < possibleDirs.size(); i++) {
		
			toMove = possibleDirs.get(i);
			if(gc.canMove(unitId, toMove)) {
				gc.moveRobot(unitId, toMove);
				break;
			}
		}
	}
	
	public static ArrayList<MapLocation> adjacent(GameController gc, MapLocation loc) {
		
		ArrayList<MapLocation> adjacentLocs = new ArrayList<>(8);
		MapLocation newLoc;
		
		for(int i = 0; i < Start.directions.length; i++) {
			if(Start.directions[i] == Direction.Center) continue;
			
			newLoc = loc.add(Start.directions[i]);
			
			if(gc.isOccupiable(newLoc) == 1) {
				adjacentLocs.add(loc.add(Start.directions[i]));
			}
		}
		
		return adjacentLocs;
	}
	
	public static Unit[] getClosest(GameController gc, ArrayList<Unit> units, Unit structure, VectorField toFactory) {

		
		int numOpenSpaces = getOpenSpaces(gc, structure.location().mapLocation());
		
		int size = (int) units.size();
		Unit[] closestUnits = new Unit[numOpenSpaces];
		int[] magnitudes = new int[numOpenSpaces];
		Unit unit;
		int magnitude;
		int last = 0;
		int place = 0;
		MapLocation currloc;
		
		for(int i = 0; i<size; i++){
			unit = units.get(i);
			if(unit.unitType()!=UnitType.Worker)continue;
			currloc = unit.location().mapLocation();
			
			magnitude = toFactory.getMagnitude(currloc);
			
			for(place = last; place>0; place--){
				if(magnitudes[place-1]<=magnitude)break;
				if(place!=numOpenSpaces){
					magnitudes[place]=magnitudes[place-1];
					closestUnits[place] = closestUnits[place-1];
				}
			}
			if(place!=numOpenSpaces){
				closestUnits[place] = unit;
				magnitudes[place] = magnitude;
				last = last>=numOpenSpaces?numOpenSpaces:last+1;
			}
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
	
	public static void moveToClosestDirection(GameController gc, Unit unit, Direction ideal) {
		int index = Start.linearSearch(Start.directions, ideal);
		Direction actual = ideal;
		int unitId = unit.id();
		
		for(int i = 0; i < 5; i++) {

			actual = Start.directions[(index + i) % 8];
			if(gc.canMove(unitId, actual) && gc.isMoveReady(unitId)) {
				gc.moveRobot(unitId, actual);
				break;
			}

			if(i == 0 || i == 4) continue;
			actual = Start.directions[(index - i + 8) % 8];
			if(gc.canMove(unitId, actual) && gc.isMoveReady(unitId)) {
				gc.moveRobot(unitId, actual);
				break;
			}
		}
	}
}
