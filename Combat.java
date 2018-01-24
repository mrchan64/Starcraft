import bc.*;
import java.util.*;
public class Combat {

	//what action to take when fighting
	//overall commands
	
	//2d array of where enemy is
	//radius = (width/2)^2 + (height/2)^2
	
	static GameController gc = Player.gc;
	static Team team = Player.team;
	static boolean combatState = false;
	static VecUnit enemies;
	static int rangerCount = 0;
	static int mageCount = 0;
	static int knightCount = 0;
	
	static ArrayList<Unit> rangerList = new ArrayList<>();
	static ArrayList<Unit> combatList = new ArrayList<>();
	
	static VectorField earthField = new VectorField();
	
	static int width = VectorField.width;
	static int height = VectorField.height;
	static int[][] world = new int[width][height];
	static int radius = (width/2) * (width/2) + (height/2) * (height/2) + 5;
	static MapLocation center = new MapLocation(VectorField.planet, width/2, height/2);
	
	
	
	public static void commands(){
		Unit target = null;
		updateEnemyPositions(rangerList);
		for(Unit ranger : rangerList){
			try {
				MapLocation location = ranger.location().mapLocation();
			}
			catch (Exception e) {
				continue;
			}
			int id = ranger.id();
			target = rangeTarget(ranger, enemies);
			if(target == null){
				//System.out.println("null");
				earthField.setTarget(findKarbonite.spawn);
				Direction dir = earthField.getDirection(findKarbonite.spawn);
				if (gc.isMoveReady(id) && gc.canMove(id, dir)){
					gc.moveRobot(id, dir);
					continue;
				}
			    Factories.moveToClosestDirection(gc, ranger, Direction.Center);
			}else if(attack(ranger, target)){
				continue;
			}else{
				advanceOnTarget(ranger, null, target, 0);
			}
			
			
		}
	}
	
	public static void setOppositeSpawn() {
		earthField.setTarget(findKarbonite.spawn);
	}

	
	
	public static int[][] updateEnemyPositions(ArrayList<Unit> units){
		int posX;
		int posY;
		UnitType type;
		Unit enemy;

		world = new int[VectorField.width][VectorField.height];
		enemies = gc.senseNearbyUnitsByTeam(center, 
			radius, 
			Player.eTeam);
		
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
	
	public static boolean targetDestroyed(Unit unit, Unit target){
		if(target.health() == 0){
			combatList.remove(unit);
			rangerList.add(unit);
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
	
	public static VectorField setField(Unit unit, Unit target){
		VectorField field = new VectorField();
		field.setTarget(target.location().mapLocation());
		return field;
	}
	
	//advance until in range or if you want to move closer, advance is how much closer
	public static boolean advanceOnTarget(Unit unit, VectorField field, Unit target, int advance){
		MapLocation location = unit.location().mapLocation();
		Direction direction; 
		if(field != null){
			direction = field.getDirection(location);
		}else{
			direction = location.directionTo(target.location().mapLocation());
		}
		int ID = unit.id();
		if(gc.canMove(ID, direction) && gc.isMoveReady(ID)){
			gc.moveRobot(ID, direction);
		}
		if(advance > 0){
			if(location.distanceSquaredTo(target.location().mapLocation()) > 
				unit.attackRange() + advance){
				gc.moveRobot(ID, direction);
			}
		}else{
			return true;
		}
		return false;
	}
	
	
	//attacks target if it can, otherwise returns false
	public static boolean attack(Unit unit, Unit target){
		int unitID = unit.id();
		int targetID = target.id();
		UnitType type = target.unitType();
		if(gc.isAttackReady(unitID)){
			if(gc.canAttack(unitID, targetID)){
				gc.attack(unitID, targetID);
				if(target.health() == 0){
					if(type == UnitType.Ranger){
						rangerCount--;
					}else if(type == UnitType.Mage){
						mageCount--;
					}else if (type == UnitType.Knight){
						knightCount--;
					}
				}
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	
	}
	
	//chooses a target for healer
	public static Unit healTarget(ArrayList<Unit> units, Unit healer, long range){
		Unit target = null;
		MapLocation healPos = healer.location().mapLocation();
		long targetDist = Long.MAX_VALUE;
		MapLocation currPos;
		long currDist;
		for(Unit unit: units){
			if(unit.health() < unit.maxHealth()){
				currPos = unit.location().mapLocation();
				currDist = healPos.distanceSquaredTo(currPos);
				if(currDist < range && currDist < targetDist){
					targetDist = currDist;
					target = unit;
				}
			}
		}
		
		return target;
	}
	
	//determines what unit to target
 	public static Unit rangeTarget(Unit unit, VecUnit list){
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
		//if(!inCombat(unit)){
			for(int i = 0; i < list.size(); i++ ){

				curr = list.get(i);
				cType = curr.unitType();
				distance = location.distanceSquaredTo(curr.location().mapLocation());
				if (distance <= unit.rangerCannotAttackRange() || distance > unit.attackRange()) {
					continue;
				}
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
			
		//}
			if(target != null){
				
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
 				if(last < squadSize){
 	 				last++;
 	 			}
 		
 			}
 			
 	
 			
 			
 		}
 	
 		for(int i = 0; i < squad.length; i++){
 			combatList.add(squad[i]);
 		}
 	}

}
