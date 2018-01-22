import bc.*;

public class UnitBuildOrder {
	public static int numFact = Player.numFactories;
	public static int unitCost = 20;
	public static Direction prefDir = Direction.South;
	VecUnitID unitsMaking;

	public static boolean canBuildUnit() {
		if (numFact > 0) {
			return true;
		}
		return false;
	}

	public static void buildUnit(GameController gc, UnitType type, Unit factory) {
		int factoryId = factory.id();
		if (canBuildUnit() && gc.canProduceRobot(factoryId, type)) {
			gc.produceRobot(factoryId, type);
		}
	}

	public static void deployUnits(GameController gc, Unit factory) {
		int factoryId = factory.id();
		if (gc.canUnload(factoryId, Direction.South)) {
			prefDir = Direction.South;
		}
		else if (gc.canUnload(factoryId, Direction.North)) {
			prefDir = Direction.North;
		}
		else if(gc.canUnload(factoryId, Direction.West)) {
			prefDir = Direction.West;
		}
		else {	
			prefDir = Direction.South;
		}
		gc.unload(factoryId, prefDir);
	}

	public static void deployUnitsWithRally(GameController gc, Unit factory, Direction dir, MapLocation loc) {
		unitsMaking = factory.structureGarrison();
		int factoryId = factory.id();
		VectorField field = new VectorField();
		for (int i = 0; i < unitsMaking.size(); i++) {
			deployUnits(gc, factory);
			Unit deployedUnit = unitsMaking.get((long)i);
			int deployedUnitId = deployedUnit.id();
			Direction moveTowards = field.getDirection(loc);
			if(gc.canMove(deployedUnitId, moveTowards) && gc.isMoveReady(deployedUnitId)) {
				gc.moveRobot(deployedUnitId, moveTowards);
		}
}