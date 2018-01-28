import bc.*;
import java.util.ArrayList;

public class Start {

	public static Direction[] directions = Direction.values();
	public static ArrayList<Unit> factories = new ArrayList<>();
	public static ArrayList<Unit> rockets = new ArrayList<>();
	
	public static MapLocation spawn;
	public static int numWorkers;
	public static int maxWorkers;

	public static final int squaresPerWorkerDense = 70;
	public static final int squaresPerWorkerSparse = 6;
	
	public static VectorField toHome;
	
	public static void initSpawn(GameController gc) {
		toHome = new VectorField();
		Unit unit;
		if(Minesweeper.isDense){
			maxWorkers = findKarbonite.accSq / squaresPerWorkerDense;
		}else{
			maxWorkers = findKarbonite.accSq / squaresPerWorkerSparse;
		}
		VecUnit units = gc.startingMap(Player.planet).getInitial_units();
		for(int i = 0; i < units.size(); i++) {
			
			unit = units.get(i);
			if(unit.team() == Player.team) {
				spawn = unit.location().mapLocation();
				findKarbonite.spawns.add(unit.location().mapLocation());
			}
		}
		toHome.setTargets(findKarbonite.spawns);
	}
	
	public static int runTurn(GameController gc, ArrayList<Unit> units){
		
		Unit unit;
		int size = (int)units.size();
		
		updateNumWorkers(units);
		
		if(notEnoughUnits()) {
			
			replicate(gc, units);
		}
		if(gc.karbonite() >= 200 && Player.planet == Planet.Earth){
			if(Factories.buildFactory(gc, units)) return 1;
		}

		if(karbDepleted()){
			Direction ideal;
			MapLocation loc;
			System.out.println("GOING HOME");
			for(int i = 0; i < size; i++) {
				unit = units.get(i);

				loc = unit.location().mapLocation();
				if(toHome.getMagnitude(loc) > 100){
					ideal = Direction.Center;
				}else{
					ideal = toHome.getDirection(loc);
				}
				Factories.moveToClosestDirection(gc, unit, ideal);
			}
		}else{
			for(int i = 0; i < size; i++) {
				unit = units.get(i);
	
				goForKarbonite(gc, unit);
			}
		}

		return 0;
	}
	
	public static void updateNumWorkers(ArrayList<Unit> units) {
		
		numWorkers = 0;
		
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
	
	public static boolean notEnoughUnits(){
		if(!karbDepleted()){
			if(!Minesweeper.isDense)
				return (numWorkers <= 3 * Player.numFactories + 11) && (numWorkers < findKarbonite.accSq/squaresPerWorkerDense);
			else
				return (numWorkers <= 8 * Player.numFactories + 12) && (numWorkers < findKarbonite.accSq/squaresPerWorkerSparse);
		}else{
			if(!Minesweeper.isDense)
				return (numWorkers <= Player.numFactories + 6) && (numWorkers < findKarbonite.accSq/squaresPerWorkerDense);
			else
				return (numWorkers <= Player.numFactories + 8) && (numWorkers < findKarbonite.accSq/squaresPerWorkerSparse);
		}
	}
	
	public static boolean karbDepleted(){
		return Player.round - Player.lastRoundMined > 30 && Player.round > 70;
	}
}
