import java.util.ArrayList;

import bc.*;

public class HealingTurn {
	public static void runTurn(GameController gc, ArrayList<Unit> availableHealers, VecUnit allUnits){
		if(availableHealers.size() == 0)return;
		int size = (int) allUnits.size();
		double temp;
		MapLocation lowestHealth = null;
		double health = 2;
		Unit unit;
		for(int i = 0; i<size; i++){
			unit = allUnits.get(i);
			if(unit.unitType()==UnitType.Factory)continue;
			if(unit.unitType()==UnitType.Rocket)continue;
			if(unit.unitType()==UnitType.Healer)continue;
			if(unit.location().isInGarrison() || unit.location().isInSpace())continue;
			temp = healthPerc(unit);
			if(temp<health){
				health = temp;
				lowestHealth = unit.location().mapLocation();
			}
		}
		if(lowestHealth==null){
			System.out.println("Kite Healing Error???");
			return ;
		}
		int x = lowestHealth.getX();
		int y = lowestHealth.getY();
		VectorField vf = CommandUnits.storedField[x][y];
		if(vf == null){
			vf = new VectorField();
			vf.setTarget(CommandUnits.squares[x][y]);
			CommandUnits.storedField[x][y] = vf;
		}

		size = availableHealers.size();
		int tsize, unitId;
		Unit tlowest;
		double thealth;
		VecUnit surr;
		Unit mine;
		MapLocation loc;
		Direction dir;
		for(int i = 0; i<size; i++){
			unit = availableHealers.get(i);
			unitId = unit.id();
			loc = unit.location().mapLocation();
			surr = gc.senseNearbyUnitsByTeam(loc, unit.attackRange(), CommandUnits.team);
			if(gc.isHealReady(unitId)){
				tsize = (int) surr.size();
				tlowest = null;
				thealth = 2;
				for(int j = 0; j<tsize; j++){
					mine = surr.get(j);
					if(mine.unitType()==UnitType.Factory)continue;
					if(mine.unitType()==UnitType.Rocket)continue;
					if(mine.unitType()==UnitType.Healer)continue;
					temp = healthPerc(mine);
					if(temp<thealth){
						thealth = healthPerc(mine);
						tlowest = mine;
					}
				}
				if(tlowest != null && gc.canHeal(unitId, tlowest.id()))gc.heal(unitId, tlowest.id());
			}
			dir = vf.getDirection(loc);
			if(!Kiting.kite(gc, unit, dir))Factories.moveToClosestDirection(gc, unit, dir);
		}
	}
	
	public static double healthPerc(Unit unit){
		double max = unit.maxHealth();
		double h = unit.health();
		return h/max;
	}
}
