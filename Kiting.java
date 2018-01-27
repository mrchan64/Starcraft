import java.util.ArrayList;

import bc.*;

public class Kiting {
	
	public static boolean kite(GameController gc, Unit unit, Direction ideal){
		if(unit.unitType() != UnitType.Ranger)return false;
		int unitId = unit.id();
		if(!gc.isMoveReady(unitId))return false;
		Team team = CommandUnits.team;
		if(team == Team.Red){
			team = Team.Blue;
		}else{
			team = Team.Red;
		}
		MapLocation curr = unit.location().mapLocation();
		VecUnit enemies = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), unit.attackRange(), team);
		ArrayList<MapLocation> enemyUnits = new ArrayList<MapLocation>();
		int size = (int) enemies.size();
		Unit enemy;
		MapLocation loc;
		for(int i = 0; i<size; i++){
			enemy = enemies.get(i);
			if(enemy.unitType() == UnitType.Ranger){
				enemyUnits.add(enemy.location().mapLocation());
			}else if(enemy.unitType() == UnitType.Mage){
				loc = enemy.location().mapLocation();
				if(loc.distanceSquaredTo(curr) <= enemy.attackRange()+1){
					enemyUnits.add(loc);
				}
			}
		}
		if(enemyUnits.size() == 0)return false;
		Direction actual = Direction.Center;
		int index = Start.linearSearch(Start.directions, ideal);
		int x, y;
		Direction closestNonTerrain = Direction.Center;
		for(int i = 0; i <= 4; i++) {

			actual = Start.directions[(index + i) % 8];
			loc = curr.add(actual);
			x = loc.getX();
			y = loc.getY();
			if(isValidSquare(gc, enemyUnits, x, y, loc) && gc.canMove(unitId, actual)) {
				gc.moveRobot(unitId, actual);
				break;
			}
			if(VectorField.terrain[x][y] == 1)closestNonTerrain = actual;

			if(i == 0 || i==4) continue;
			
			actual = Start.directions[(index - i + 8) % 8];
			loc = curr.add(actual);
			x = loc.getX();
			y = loc.getY();
			if(isValidSquare(gc, enemyUnits, x, y, loc) && gc.canMove(unitId, actual)) {
				gc.moveRobot(unitId, actual);
				break;
			}
			if(VectorField.terrain[x][y] ==1 )closestNonTerrain = actual;
		}
		if(gc.isMoveReady(unitId) && gc.canMove(unitId, closestNonTerrain)) {
			gc.moveRobot(unitId, closestNonTerrain);
		}
		return true;
	}
	
	private static boolean isValidSquare(GameController gc, ArrayList<MapLocation> enemies, int x, int y, MapLocation test){
		if(VectorField.terrain[x][y]!=1)return false;
		try{
			gc.senseUnitAtLocation(test);
			return false;
		}catch(Exception e){}
		Unit enemy;
		for(MapLocation loc: enemies){
			enemy = CommandUnits.enemies[loc.getX()][loc.getY()];
			if(enemy.unitType() == UnitType.Ranger){
				if(loc.distanceSquaredTo(CommandUnits.squares[x][y])<=enemy.attackRange())return false;
			}else{
				if(loc.distanceSquaredTo(CommandUnits.squares[x][y])<=enemy.attackRange()+1)return false;
			}
		}
		return true;
	}
	
}
