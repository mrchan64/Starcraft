import bc.*;
import java.util.*;

public class UnitBuildOrder {
	public static int numFact = Player.numFactories;
	public static VecUnitID unitsMaking;
	public static Direction[] dir = Direction.values();
	public static ArrayList<Unit> builtFacts = new ArrayList<>();

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
		for (int i = 0; i < dir.length; i++) {
			if (gc.canUnload(factoryId, dir[i])) {
				gc.unload(factoryId, dir[i]);
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