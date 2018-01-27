import bc.*;
import java.util.*;

public class UnitBuildOrder {
	public static VecUnitID unitsMaking;
	public static Direction[] dir = Direction.values();
	public static ArrayList<Unit> builtFacts = new ArrayList<>();

	public static UnitType[] denseUnitOrder = { 
		UnitType.Ranger,
		UnitType.Ranger, 
		UnitType.Ranger, 
		UnitType.Mage, 
		UnitType.Healer,
		UnitType.Healer, 
		UnitType.Healer };
	public static UnitType[] sparseUnitOrder = { 
		UnitType.Ranger,
		UnitType.Healer, 
		UnitType.Ranger, 
		UnitType.Knight, 
		UnitType.Mage,
		UnitType.Healer };
	public static UnitType[] closeUnitOrder = { 
		UnitType.Knight,
		UnitType.Knight, 
		UnitType.Knight, 
		UnitType.Healer, 
		UnitType.Healer,
		UnitType.Healer, 
		UnitType.Ranger, 
		UnitType.Ranger };
	public static UnitType[] order;
	public static int index = 0;
	public static ArrayList<Unit> builtRocks = new ArrayList<>();
	public static VectorField toRocket = new VectorField();

	public static void buildUnit(GameController gc, UnitType type, Unit factory) {
		int factoryId = factory.id();
		type = typeToBuild();

		if (gc.round() > 100 && Start.numWorkers < 8) {
			type = UnitType.Worker;
		}

		if (gc.canProduceRobot(factoryId, type)) {
			gc.produceRobot(factoryId, type);
			index = (index + 1) % order.length;
		}
	}

	public static void deployUnits(GameController gc, Unit structure) {
		int structureId = structure.id();
		for (int i = 0; i < dir.length; i++) {
			if (dir[i] == Direction.Center)
				continue;

			if (gc.canUnload(structureId, dir[i])) {
				gc.unload(structureId, dir[i]);
				i--;
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

	private static UnitType typeToBuild() {
		order = sparseUnitOrder;
		return order[index];
	}

	/*
	 * public static void deployUnitsWithRally(GameController gc, Unit factory,
	 * Direction dir, MapLocation loc) { unitsMaking =
	 * factory.structureGarrison(); int factoryId = factory.id(); VectorField
	 * field = new VectorField(); field.setTarget(loc); for (int i = 0; i <
	 * unitsMaking.size(); i++) { deployUnits(gc, factory); int deployedUnit =
	 * unitsMaking.get((long)i); Direction moveTowards =
	 * field.getDirection(loc); if(gc.canMove(deployedUnit, moveTowards) &&
	 * gc.isMoveReady(deployedUnit)) { gc.moveRobot(deployedUnit, moveTowards);
	 * } } }
	 */
}