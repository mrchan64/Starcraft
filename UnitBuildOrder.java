import bc.*;
import java.util.ArrayList;
import java.util.Random;

public class UnitBuildOrder {
	public static VecUnitID unitsMaking;
	public static Direction[] dir = Direction.values();
	public static ArrayList<Unit> builtFacts = new ArrayList<>();

	public static UnitType[] sparseUnitOrder = { 
		UnitType.Ranger,
		UnitType.Ranger, 
		UnitType.Ranger, 
		UnitType.Mage, 
		UnitType.Healer,
		UnitType.Healer, 
		UnitType.Healer };
	public static UnitType[] denseUnitOrder = { 
		UnitType.Knight, 
		UnitType.Ranger,
		UnitType.Ranger, 
		UnitType.Ranger,
		UnitType.Mage,
		UnitType.Healer,
		UnitType.Healer};
	public static UnitType[] closeUnitOrder = { 
		UnitType.Knight, 
		UnitType.Knight, 
		UnitType.Knight,
		UnitType.Ranger,
		UnitType.Ranger, 
		UnitType.Knight, 
		UnitType.Healer};
	public static UnitType[] order;
	public static int index = 0;
	public static int closeIndex = 0;
	public static ArrayList<Unit> builtRocks = new ArrayList<>();
	public static VectorField toRocket = new VectorField();

	public static void buildUnit(GameController gc, UnitType type, Unit factory) {
		int factoryId = factory.id();
		type = typeToBuild();

		if (Start.notEnoughUnits() && Start.numWorkers < Start.maxWorkers && Player.round >= 140) {
			if(Math.random() > 0.4)type = UnitType.Worker;
		}

		if (gc.canProduceRobot(factoryId, type)) {
			gc.produceRobot(factoryId, type);
			/*if(!Factories.isClose)*/index++;
			//else closeIndex = (closeIndex + 1) % order.length;
		}
	}

	public static void deployUnits(GameController gc, Unit structure) {
		
		Random generator = new Random();
		int startPoint = generator.nextInt(dir.length);
		int structureId = structure.id();
		int index;
		if(CommandUnits.combatUnitSize > 10)return;
		
		for (int i = 0; i < dir.length; i++) {
			
			index = (i + startPoint) % dir.length;
			if (dir[index] == Direction.Center)
				continue;

			if (gc.canUnload(structureId, dir[index])) {

				gc.unload(structureId, dir[index]);
				i--;
			}
		}
	}
	
	public static void allDeploy(GameController gc){
		for (int i = 0; i < builtFacts.size(); i++) {
			Unit unit = builtFacts.get(i);
			deployUnits(gc, unit);
		}
	}

	public static void queueUnitsAllFactories(GameController gc, UnitType type) {
		for (int i = 0; i < builtFacts.size(); i++) {
			Unit unit = builtFacts.get(i);
			buildUnit(gc, type, unit);
		}
	}

	private static UnitType typeToBuild() {
		if(Minesweeper.isDense && index < sparseUnitOrder.length*2){
			order = closeUnitOrder;
		}
		else order = sparseUnitOrder;
		/*if(Factories.isClose){
			order = closeUnitOrder;
			return order[closeIndex];
		}
		else{*/
			return order[index%order.length];
		//}
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