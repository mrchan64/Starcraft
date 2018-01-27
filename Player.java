
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

	public static void main(String[] args) {

		gc = new GameController();
		planet = gc.planet();
		team = gc.team();
		boolean workersOnMars = false;
		
		VectorField.initWalls(gc);
		findKarbonite.initKarb(gc);
		Upgrades.upgradeUnitsSmall(gc);
		CommandUnits.initCommand(gc);
		Start.initSpawn(gc);

		while (planet == Planet.Earth) {

			System.out.println("Currently Round " + gc.round());
			UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Ranger);

			Start.factories = new ArrayList<>();
			UnitBuildOrder.builtFacts = new ArrayList<>();
			Start.rockets = new ArrayList<>();
			UnitBuildOrder.builtRocks = new ArrayList<>();
			availableUnits = new ArrayList<>();
			availableCombatUnits = new ArrayList<>();
			numFactories = 0;

			findKarbonite.updateFieldKarb(gc);

			units = gc.myUnits();

			for (int i = 0; i < units.size(); i++) {

				unit = units.get(i);
				type = unit.unitType();
				health = (int) unit.health();

				if (unit.location().isInGarrison() || unit.location().isInSpace())
					continue;
				
				else if (type == UnitType.Factory) {
					numFactories++;

					if (health < 300) {
						Start.factories.add(unit);
					} else {
						UnitBuildOrder.builtFacts.add(unit);
					}
				}

				else if (type == UnitType.Rocket) {
					if (health < 200) {
						Start.rockets.add(unit);
					} else if (unit.location().isOnMap()) {
						UnitBuildOrder.builtRocks.add(unit);
					}
				}

				else if (type == UnitType.Worker) {
					availableUnits.add(unit);

					for (Direction dir : Start.directions) {
						if (gc.canHarvest(unit.id(), dir)) {

							gc.harvest(unit.id(), dir);
							break;
						}
					}
				}
				
				else {
					availableCombatUnits.add(unit);
				}
			}

			CommandUnits.runTurn(gc);
			
			if (stage >= 2) {

				Rocket.runTurn(gc, availableUnits);

				for (int i = 0; i < UnitBuildOrder.builtRocks.size(); i++) {
					
					if(!workersOnMars) {
						Rocket.loadUnits(gc, UnitBuildOrder.builtRocks.get(i), availableUnits);
						workersOnMars = true;
					}
					
					else {
						Rocket.loadUnits(gc, UnitBuildOrder.builtRocks.get(i), availableCombatUnits);
					}
				}
			}

			if (stage >= 1) {

				Factories.runTurn(gc, availableUnits);

				if (gc.round() > 100) {
					if (Start.numWorkers < 8) {
						UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Worker);
					}
				}

				if (gc.karbonite() > 100) {

					UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Ranger);
				}

				if (gc.round() >= 75 && gc.karbonite() > 100) {
					stage = 2;
				}
			}

			if (stage >= 0) {

				Start.updateNumWorkers(availableUnits);

				if (stage == 0) {
					stage += Start.runTurn(gc, availableUnits);
				}

				else if (numFactories <= findKarbonite.avaSq / 50 || Start.numWorkers <= 2 * numFactories + 8) {
					Start.runTurn(gc, availableUnits);
				}
				
				if(gc.round() % 50 == 0) System.gc();
			}

			gc.nextTurn();
		}

		while(planet == Planet.Mars) {
			
			findKarbonite.updateFieldKarb(gc);
			
			ArrayList<Unit> marsUnits = new ArrayList<>();
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
			
			Start.runTurn(gc, marsUnits);
			gc.nextTurn();
			System.out.println("Currently Round " + gc.round());
		}
	}
}