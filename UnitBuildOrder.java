import bc.*;
import java.util.*;

public class UnitBuildOrder {
	public static VecUnitID unitsMaking;
	public static Direction[] dir = Direction.values();
	public static ArrayList<Unit> builtFacts = new ArrayList<>();
	
	public static final int RangerPerc = 5;
	public static final int MagePerc = 2;
	public static final int KnightPerc = 2;
	public static ArrayList<Unit> builtRocks = new ArrayList<>();


	public static void buildUnit(GameController gc, UnitType type, Unit factory) {
		int factoryId = factory.id();
		type = typeToBuild();
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
	
	private static UnitType typeToBuild(){
		int perc = (int) (Math.random()*10);
		if(perc<RangerPerc)return UnitType.Ranger;
		perc -= RangerPerc;
		if(perc<MagePerc)return UnitType.Mage;
		perc -= MagePerc;
		if(perc<KnightPerc)return UnitType.Knight;
		//return UnitType.Healer;
		return UnitType.Knight;
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