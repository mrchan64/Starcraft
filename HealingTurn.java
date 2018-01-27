import java.util.ArrayList;

import bc.*;

public class HealingTurn {
	public static void runTurn(GameController gc, ArrayList<Unit> availableHealers, VecUnit allUnits){
		if(availableHealers.size() == 0)return;
		int size = (int) allUnits.size();
		MapLocation lowestHealth = null;
		int health = Integer.MAX_VALUE;
		Unit unit;
		for(int i = 0; i<size; i++){
			unit = allUnits.get(i);
			if(unit.location().isInGarrison() || unit.location().isInSpace()) continue;
			if(unit.health()<health){
				health = (int) unit.health();
				lowestHealth = unit.location().mapLocation();
			}
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
		MapLocation loc;
		int tsize, unitId;
		Unit tlowest;
		int thealth;
		VecUnit surr;
		Unit mine;
		for(int i = 0; i<size; i++){
			unit = availableHealers.get(i);
			unitId = unit.id();
			loc = unit.location().mapLocation();
			surr = gc.senseNearbyUnitsByTeam(loc, unit.attackRange(), CommandUnits.team);
			if(gc.isHealReady(unit.id())){
				tsize = (int) surr.size();
				tlowest = null;
				thealth = Integer.MAX_VALUE;
				for(int j = 0; j<tsize; j++){
					mine = surr.get(j);
					if(mine.health()<thealth){
						thealth = (int) mine.health();
						tlowest = mine;
					}
				}
				if(tlowest != null && gc.canHeal(unitId, tlowest.id()))gc.heal(unitId, tlowest.id());
			}
			Factories.moveToClosestDirection(gc, unit, vf.getDirection(loc));
		}
	}
}
