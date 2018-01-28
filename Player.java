
// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.ArrayList;

public class Player {

	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;

	public static ArrayList<Unit> availableUnits;
	public static ArrayList<Unit> availableCombatUnits;
	public static Team team;

	public static GameController gc;

	public static VecUnit units;
	static Unit unit;
	public static MapLocation unitLoc;
	public static int stage = 0;
	public static UnitType type;
	public static int health;
	public static Planet planet;
	public static int lastRoundMined = 0;
	public static int round;
	public static int spawnsDone = 0;
	
	public static boolean workersOnMars = false;

	public static boolean accSquare = true;
	public static MapLocation startingLoc;

	public static void main(String[] args) {
		System.out.println("Currently using Version Branch52");
		gc = new GameController();
		planet = gc.planet();
		team = gc.team();
		
		VectorField.initWalls(gc);
		Start.initSpawn(gc);
		findKarbonite.initKarb(gc);
		Start.updateMaxWorkers();
		Upgrades.upgradeUnitsSmall(gc);
		CommandUnits.initCommand(gc);
		System.out.println("Currently running on a "+(VectorField.largeMap?"large":"small")+" map and running "+(Minesweeper.isDense?"Knight":"Ranged")+" code");

		while (planet == Planet.Earth) {
			//try{
			round  = (int)gc.round();
			System.out.println("Currently Round "+round);

			Start.factories = new ArrayList<>();
			UnitBuildOrder.builtFacts = new ArrayList<>();
			Start.rockets = new ArrayList<>();
			UnitBuildOrder.builtRocks = new ArrayList<>();
			availableUnits = new ArrayList<>();
			availableCombatUnits = new ArrayList<>();
			numFactories = 0;

			findKarbonite.updateFieldKarb(gc);

			units = gc.myUnits();
			ArrayList<Unit> workers = new ArrayList<Unit>();

			for (int i = 0; i < units.size(); i++) {

				unit = units.get(i);
				type = unit.unitType();
				health = (int) unit.health();

				if (unit.location().isInGarrison() || unit.location().isInSpace())
					continue;
				
				else if (type == UnitType.Factory) {
					
					numFactories++;
					
					if (health < unit.maxHealth()) {
						Start.factories.add(unit);
					} else {
						UnitBuildOrder.builtFacts.add(unit);
					}
				}

				else if (type == UnitType.Rocket) {
					
					if (health < unit.maxHealth()) {
						Start.rockets.add(unit);
					} else if (unit.location().isOnMap()) {
						UnitBuildOrder.builtRocks.add(unit);
					}
				}

				else if (type == UnitType.Worker) {
					availableUnits.add(unit);
					workers.add(unit);
				}
				
				else {
					availableCombatUnits.add(unit);
				}
			}
			
			if (stage >= 2) {

				if (round == 749) {
					for (Unit rocket : UnitBuildOrder.builtRocks) {
						int rocketID = (int)rocket.id();
						Rocket.launchRocket(gc, rocketID);
					}
				}

				Rocket.runTurn(gc, availableUnits);

				for (int i = 0; i < UnitBuildOrder.builtRocks.size(); i++) {
					
					if(!workersOnMars) {
						if(Rocket.fighterLoaded) Rocket.loadUnits(gc, UnitBuildOrder.builtRocks.get(i), availableUnits);
						else Rocket.loadClosestFighter(gc, UnitBuildOrder.builtRocks.get(i));
						workersOnMars = true;
					}

					else {
						Rocket.loadCombatUnits(gc, UnitBuildOrder.builtRocks.get(i), availableCombatUnits);
					}
				}
			}

			if (stage >= 1) {

				if (round > 500) {
					stage = 2;
				}

				if (!Rocket.sentFirst && (round >= 150)) {
					Rocket.runFirstTurn(gc, availableUnits);
				}

				if (round > 100 && Start.numWorkers < 8) {
					UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Worker);
				}

				if ((round - lastRoundMined <= 15 || round < 150 || Rocket.sentFirst) && ((double) units.size() < (double) (findKarbonite.accSq * 0.7)) && stage < 2) {

					UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Ranger);
				}

				if (accSquare && round > 150) {

					for (int i = 0; i < units.size(); i++) {
						if (!units.get(i).location().isInGarrison() && !units.get(i).location().isInSpace()) {
							startingLoc = units.get(i).location().mapLocation();
							break;
						}	
					}

					findKarbonite.findAccSq2(gc.startingMap(Planet.Earth), startingLoc);
					accSquare = false;
				}
			}

			if (stage >= 0) {
				
				CommandUnits.runTurn(gc);

				if(availableUnits.size() > 0) {
					
					Factories.runTurn(gc, availableUnits);
					Start.updateNumWorkers(availableUnits);
					
					for(int i = 0; i < findKarbonite.spawns.size(); i++) {
						
						Start.runTurn(gc, availableUnits);
	
						if(Start.spawnsDone == findKarbonite.spawns.size()) {
							stage = stage > 1 ? stage : 1;
						}
					}
				}
				
				for(Unit worker : workers){
					for (Direction dir : Start.directions) {
						if (gc.canHarvest(worker.id(), dir)) {

							gc.harvest(worker.id(), dir);
							lastRoundMined = round;
							break;
						}
					}
				}
			}
			//long time = System.currentTimeMillis();
			//System.out.println("Total: "+rt.totalMemory()+" Free: "+rt.freeMemory());
			//System.out.println("rt takes "+(System.currentTimeMillis()-time));
			if(VectorField.largeMap){
				CommandUnits.resetStoredField();
				System.gc();
			}
			if(round%25==0){
				System.gc();
			}
			//}catch(Exception e){}
			gc.nextTurn();
		}

		while(planet == Planet.Mars) {
			//try{
			round = (int)gc.round();
			
			ArrayList<Unit> marsUnits = new ArrayList<>();

			findKarbonite.updateAsters(gc, round);

			units = gc.myUnits();
			
			for(int i = 0; i < units.size(); i++) {
				
				unit = units.get(i);
				type = unit.unitType();
				
				if(type == UnitType.Rocket) {
					UnitBuildOrder.deployUnits(gc, unit);
				}
				
				else if(type == UnitType.Worker) {
					marsUnits.add(unit);
				}
			}
			findKarbonite.optimizeKarb(gc, marsUnits);
			for (int i = 0; i < findKarbonite.miners.size(); i++) {
				findKarbonite.miners.set(i, 0);
			}
			
			CommandUnits.runTurn(gc);
			Start.runTurn(gc, marsUnits);
			if(round%25==0)System.gc();
			//}catch(Exception e){}
			gc.nextTurn();
			//System.out.println("Currently Round " + round);
		}
	}
}