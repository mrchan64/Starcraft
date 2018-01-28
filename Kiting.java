import java.util.ArrayList;

import bc.*;

public class Kiting {
	
	public static int kiteDist = 50;
	public static int modifier = 0;
	
	public static boolean kite(GameController gc, Unit unit, Direction ideal){
		//if(unit.unitType() != UnitType.Ranger)return false;
		if(unit.unitType()==UnitType.Knight)return false;
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
			if(CommandUnits.validUnits.contains(enemy.unitType())){
				enemyUnits.add(enemy.location().mapLocation());
			}
		}
		if(enemyUnits.size() == 0)return false;
		
		if(unit.unitType()==UnitType.Healer)modifier = 8;
		else modifier = 1;
		
		if(CommandUnits.combatUnitSize > 15)modifier -= 1;
		
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

			if(isntTerrain(x,y))closestNonTerrain = actual;

			if(i == 0 || i==4) continue;
			
			actual = Start.directions[(index - i + 8) % 8];
			loc = curr.add(actual);
			x = loc.getX();
			y = loc.getY();
			if(isValidSquare(gc, enemyUnits, x, y, loc) && gc.canMove(unitId, actual)) {
				gc.moveRobot(unitId, actual);
				break;
			}

			if(isntTerrain(x,y))closestNonTerrain = actual;

		}
		if(gc.isMoveReady(unitId) && gc.canMove(unitId, closestNonTerrain)) {
			gc.moveRobot(unitId, closestNonTerrain);
		}
		return true;
	}
	
	private static boolean isValidSquare(GameController gc, ArrayList<MapLocation> enemies, int x, int y, MapLocation test){
		if(x<0 || x>=VectorField.width || y<0 || y>=VectorField.height)return false;
		if(VectorField.terrain[x][y]!=1)return false;
		try{
			gc.senseUnitAtLocation(test);
			return false;
		}catch(Exception e){}
		Unit enemy;
		for(MapLocation loc: enemies){
			enemy = CommandUnits.enemies[loc.getX()][loc.getY()];
			if(loc.distanceSquaredTo(CommandUnits.squares[x][y])<=kiteDist+modifier){
				System.out.println("invalid");
				return false;
			}
		}
		System.out.println("valid");
		return true;
	}
	
	private static boolean isntTerrain(int x, int y){
		if(x<0 || x>=VectorField.width || y<0 || y>=VectorField.height)return false;
		return VectorField.terrain[x][y]==1;
	}
	
}
