import bc.*;
import java.util.*;

public class UnitBuildOrder {
	public static VecUnitID unitsMaking;
	public static Direction[] dir = Direction.values();
	public static ArrayList<Unit> builtFacts = new ArrayList<>();
	public static ArrayList<Unit> builtRocks = new ArrayList<>();
	public static VectorField toRocket = new VectorField();


	public static void buildUnit(GameController gc, UnitType type, Unit factory) {
		int factoryId = factory.id();
		if (gc.canProduceRobot(factoryId, type)) {
			gc.produceRobot(factoryId, type);
		}
	}

	public static void deployUnits(GameController gc, Unit factory) {
		int factoryId = factory.id();
		for (int i = 0; i < dir.length; i++) {
			try {
				if (gc.canUnload(factoryId, dir[i])) {
					gc.unload(factoryId, dir[i]);
				}
			}
			catch(Exception e) {
				System.out.println("can't deployUnits");
			}
		}
	}

	public static void queueUnitsAllFactories(GameController gc, UnitType type) {
		for (int i = 0; i < builtFacts.size(); i++) {
			Unit unit = builtFacts.get(i);
			deployUnits(gc, unit);
			buildUnit(gc, type, unit);
		}
	}

	public static void loadUnits(GameController gc, Unit rocket, ArrayList<Unit> units) {
		int rocketId = rocket.id();
		MapLocation rocketLoc = rocket.location().mapLocation();
		toRocket.setTarget(rocketLoc);
		Unit[] astros = Factories.getClosest(gc, units, rocket, toRocket);

		for (int i = 0; i < astros.length; i++) {
			int unitId = astros[i].id();
			if (gc.canLoad(rocketId, unitId)) {
				gc.load(rocketId, unitId);
			}
			else if (!gc.canLoad(rocketId, unitId)) {
				int x = 0;
				int y = 0;
				for (int ii = 0; ii < findKarbonite.mWidth; ii++) {
					for (int j = 0; j < findKarbonite.mHeight; j++) {
						if (findKarbonite.availMars[ii][j] == 1) {
							x = ii;
							y = j;
							findKarbonite.availMars[ii][j] = 0;
						}
					}
				}
				MapLocation destination = new MapLocation(Planet.Mars, x, y);
				if (gc.canLaunchRocket(rocketId, destination)) {
					gc.launchRocket(rocketId, destination);
					builtRocks.remove(rocket);
				}
			}
		
			else {
				Factories.sendUnits(gc, astros, rocket, toRocket);
			}
		}
	}

	/*public static void deployUnitsWithRally(GameController gc, Unit factory, Direction dir, MapLocation loc) {
		unitsMaking = factory.structureGarrison();
		int factoryId = factory.id();
		VectorField field = new VectorField();
		field.setTarget(loc);
		for (int i = 0; i < unitsMaking.size(); i++) {
			deployUnits(gc, factory);
			int deployedUnit = unitsMaking.get((long)i);
			Direction moveTowards = field.getDirection(loc);
			if(gc.canMove(deployedUnit, moveTowards) && gc.isMoveReady(deployedUnit)) {
				gc.moveRobot(deployedUnit, moveTowards);
			}
		}
	}*/
}