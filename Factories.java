import bc.*;

public class Factories {

	public static void sendUnits(GameController gc, Unit[] units, MapLocation factory, VectorField toFactory) {
		
		MapLocation currLoc;
		Unit unit;
		int unitId;
		int factoryId = gc.senseUnitAtLocation(factory).id();
		
		for(int i = 0; i < units.length; i++) {
			unit = units[i];
			unitId = unit.id();
			currLoc = gc.unit(unitId).location().mapLocation();
			
			if(!currLoc.isAdjacentTo(factory)) {
				moveToClosestDirection(gc, unit, toFactory.getDirection(currLoc));
			}else{
				if(gc.canBuild(unitId, factoryId)){
					gc.build(unitId, factoryId);
				}
			}
			
		}
	}
	
	public static Unit[] getClosest(GameController gc, VecUnit units, MapLocation factory, VectorField toFactory) {

		
		int numOpenSpaces = getOpenSpaces(gc, factory);
		
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
			
			last = i>numOpenSpaces? numOpenSpaces:i;
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
		for(int i = 0;i<numOpenSpaces; i++){
			System.out.println(closestUnits[i].id());
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
