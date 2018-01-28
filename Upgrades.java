import bc.*;

public class Upgrades {

	/*
	 * Worker upgrade - 25
	 * knight - 25
	 * Mage upgrade - 25
	 * Ranger Upgrade - 25
	 * Ranger Upgrade - 100
	 * Ranger Upgrade - 200 = 500
	 * Rocket Upgrade -100 = 300
	 * Rocket - 100 = 00
	 * ROcket - 100 = 700
	 * knight updgrade
	 * knight upgrade
	 * knight upgrade 
	 * mage upgrade
	 */



	public static void upgradeUnits(GameController gc) {
		gc.queueResearch(UnitType.Worker);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
	}

	public static void upgradeUnitsSmall(GameController gc) {
		gc.queueResearch(UnitType.Worker);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Healer);
		gc.queueResearch(UnitType.Healer);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Knight);
	}

	//Remember to implement
	public static void upgradeKnights(GameController gc) {
		gc.queueResearch(UnitType.Worker);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Healer);
		gc.queueResearch(UnitType.Healer);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Ranger);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Rocket);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Mage);
		gc.queueResearch(UnitType.Knight);
		gc.queueResearch(UnitType.Knight);
	}


}