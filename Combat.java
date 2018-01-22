import bc.*;
import java.util.*;
public class Combat {

	//what action to take when fighting
	//overall commands
	
	//2d array of where enemy is
	//radius = (width/2)^2 + (height/2)^2
	
	static GameController gc;
	static Team team;
	static boolean combatState = false;
	static VecUnit enemies;
	static int rangerCount = 0;
	static int mageCount = 0;
	static int knightCount = 0;
	
	
	static int width = VectorField.width;
	static int height = VectorField.height;
	static int[][] world = new int[width][height];
	static int radius = (width/2) * (width/2) + (height/2) * (height/2) + 5;
	static MapLocation center = new MapLocation(VectorField.planet, width/2, height/2);
	
	
	
	public static int[][] updateEnemyPositions(ArrayList<Unit> units){
		int posX;
		int posY;
		UnitType type;
		Unit enemy;

		world = new int[VectorField.width][VectorField.height];
		enemies = gc.senseNearbyUnitsByTeam(center, radius, team);
		
		for(int i = 0; i < enemies.size(); i++){
			enemy = enemies.get(i);
			type = enemy.unitType();
			if(type == UnitType.Ranger){
				rangerCount++;
			}else if(type == UnitType.Mage){
				mageCount++;
			}else if (type == UnitType.Knight){
				knightCount++;
			}
			
			posX = enemy.location().mapLocation().getX();
			posY = enemy.location().mapLocation().getY();
			world[posX][posY] = 2;

		}
	
		return world;
		
	}
	
	public static boolean inVision(Unit unit, Unit target){
		if(unit.location().mapLocation().distanceSquaredTo(target.location().mapLocation()) <
				unit.visionRange()){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean inRange(Unit unit, Unit target){
		if(unit.location().mapLocation().distanceSquaredTo(target.location().mapLocation()) <
				unit.attackRange()){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean inCombat(Unit unit){
		for(int i = 0; i < enemies.size(); i++){
			if(inVision(unit, enemies.get(i))){
				return true;
			}
		}
		return false;
	}
	
	
	public static MapLocation setRallyPoint(Unit unit){
		return unit.location().mapLocation();
	}
	
	//determines what unit to target
 	public static Unit target(Unit unit, VecUnit list){
		Unit target = null;
		Unit healer = null;
		long distanceH = 0;
		Unit ranger = null;
		long distanceR = 0;
		Unit mage = null;
		long distanceM = 0;
		Unit other = null;
		long distanceO = 0;
		Unit curr;
		UnitType cType;
		UnitType uType = unit.unitType();
		MapLocation location = unit.location().mapLocation();
		long distance;
		if(inCombat(unit)){
			for(int i = 0; i < list.size(); i++ ){
				curr = list.get(i);
				cType = curr.unitType();
				distance = location.distanceSquaredTo(curr.location().mapLocation());
				//nearest mage
				if(uType == UnitType.Ranger){
					if(cType == UnitType.Healer){
						if(healer == null){
							healer = curr;
							distanceH = distance;
						}else if(distance < distanceH){
							healer = curr;
							distanceH = distance;
						}
					}
					if(cType == UnitType.Mage){
						if(mage == null){
							mage = curr;
							distanceM = distance;
						}else if(distance < distanceM){
							mage = curr;
							distanceM = distance;
						}
						//nearest ranger
					}else if(cType == UnitType.Ranger){
						if(ranger == null){
							ranger = curr;
							distanceR = distance;
						}else if(distance < distanceR){
							ranger = curr;
							distanceR = distance;
						}
						//nearest other
					}else{
						if(other == null){
							other = curr;
							distanceO = distance;
						}else if(distance < distanceO){
							other = curr;
							distanceO = distance;
						}
					}
					if(healer != null){
						target = healer;
					}else if(mage != null){
						target = mage;
					}else if(ranger != null){
						target = ranger;
					}else{
						target = other;
					}
					//target closest
				}else{
					if(target == null){
						target = curr;
						distanceO = distance;
					}else if(distance < distanceO){
						target = curr;
						distanceO = distance;
					}
				}
	
			}
			
		}
		return target;
	}//end target

 	//probably only good for rangers, but collects closest rangers to target
 	public static void concentrateFire(ArrayList<Unit> available, Unit target, boolean snipe){
 		Unit first = available.get(0);
 		//calculates number of rangers needed to one shot
 		int squadSize = (int) target.health() / first.damage();
 		Unit[] squad = new Unit[squadSize];
 		int last = 0;
 		long distance;
 		long d2 = 0;
 		MapLocation targetPos = target.location().mapLocation();
 		Unit curr = available.get(0);
 		Unit temp;
 		
 		squad[0] = curr;
 		
 		if(squadSize > available.size()){
 			squadSize = available.size();
 		}
 		
 		//find closest friendlies to make squad
 		for(int i = 1; i < available.size(); i++){
 			curr = available.get(i);
 			distance = curr.location().mapLocation().distanceSquaredTo(targetPos);
 			for(int j = 0; j < last; j++){
 				d2 = squad[j].location().mapLocation().distanceSquaredTo(targetPos);
 				if(!snipe){
 					if(last == squadSize && distance > d2){
 	 					break;
 	 				}
 	 				if(distance < d2){
 	 					temp = squad[j];
 	 					curr = squad[j];
 	 					squad[j+1] = temp;
 	 				}else{
 	 					squad[j+1] = curr;
 	 				}
 	 				//if we are sniping we want the furthest rangers
 				}else{
 					if(curr.rangerCountdown() == 0 || last == squadSize && distance < d2){
 	 					break;
 	 				}
 	 				if(distance > d2){
 	 					temp = squad[j];
 	 					curr = squad[j];
 	 					squad[j+1] = temp;
 	 				}else{
 	 					squad[j+1] = curr;
 	 				}
 				}
 		
 			}
 			
 			if(last < squadSize){
 				last++;
 			}
 			
 			
 		}
 	}

}
