import bc.*;
import java.util.ArrayList;
import java.util.Random;

public class Start {

	public static Direction[] directions = Direction.values();
	public static ArrayList<Unit> factories = new ArrayList<>();
	public static ArrayList<Unit> rockets = new ArrayList<>();
	public static Random generator = new Random();
	
	public static int numWorkers;
	public static int maxWorkers;
	public static int spawnsDone;

	public static final int squaresPerWorkerDense = 70;
	public static final int squaresPerWorkerSparse = 6;
	public static final int minWorkers = 11;
	
	public static VectorField toHome;
	
	public static void initSpawn(GameController gc) {
		toHome = new VectorField();
		Unit unit;

		VecUnit units = gc.startingMap(Player.planet).getInitial_units();
		
		for(int i = 0; i < units.size(); i++) {
			
			unit = units.get(i);
			if(unit.team() == Player.team) {
				findKarbonite.spawns.add(unit.location().mapLocation());
			}
		}
		toHome.setTargets(findKarbonite.spawns);
	}
	
	public static void updateMaxWorkers() {
		if(Minesweeper.isDense){
			maxWorkers = findKarbonite.accSq / squaresPerWorkerDense;
		}else{
			maxWorkers = findKarbonite.accSq / squaresPerWorkerSparse;
		}
	}
	
	public static void runTurn(GameController gc, ArrayList<Unit> units){
		
		Unit unit;
		int size = (int)units.size();
		
		updateNumWorkers(units);
		
		if(notEnoughUnits()) {
			
			replicate(gc, units);
		}
		
		if(gc.karbonite() >= 200 && Player.planet == Planet.Earth){
			
			if(spawnsDone < findKarbonite.spawns.size()) {
				Factories.buildSpawnFactory(gc, units, findKarbonite.spawns.get(spawnsDone));
			}
			
			else{
				Factories.buildFactory(gc, units);
			}
		}

		if(karbDepleted() && Player.planet == Planet.Earth){
			Direction ideal;
			MapLocation loc;
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
		
		if(units.size() == 0) return;
		
		Unit unit;
		int unitId;
		int size = units.size();
		int startPoint = generator.nextInt(size);
		
		for(int i = 0; i < size; i++) {
			
			unit = units.get((i + startPoint) % size);
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
				return (numWorkers <= 3 * Player.numFactories + 11) && (numWorkers < findKarbonite.accSq/squaresPerWorkerDense) || (numWorkers<minWorkers);
			else
				return (numWorkers <= 8 * Player.numFactories + 12) && (numWorkers < findKarbonite.accSq/squaresPerWorkerSparse) || (numWorkers<minWorkers);
		}else{
			if(!Minesweeper.isDense)
				return (numWorkers <= Player.numFactories + 6) && (numWorkers < findKarbonite.accSq/squaresPerWorkerDense) || (numWorkers<minWorkers);
			else
				return (numWorkers <= Player.numFactories + 8) && (numWorkers < findKarbonite.accSq/squaresPerWorkerSparse) || (numWorkers<minWorkers);
		}
	}
	
	public static boolean karbDepleted(){
		return Player.round - Player.lastRoundMined > 30 && Player.round > 70;
	}
}
