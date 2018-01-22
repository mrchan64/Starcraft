import bc.*;
import java.util.*;
public class Combat {

	//what action to take when fighting
	//overall commands
	
	static GameController gc;
	static Team team;
	boolean combatState = false;
	HashSet<Unit> enemies = new HashSet<Unit>();
	int rangerCount = 0;
	int mageCount = 0;
	int knightCount = 0;
	
	public VecUnit inVision(Unit unit){
		VecUnit inVision = gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 
				unit.visionRange(), team);
		UnitType type;
		for(int i = 0; i < inVision.size(); i++){
			type = inVision.get(i).unitType();
			if(type == UnitType.Ranger){
				rangerCount++;
			}else if(type == UnitType.Mage){
				mageCount++;
			}else if (type == UnitType.Knight){
				knightCount++;
			}
			enemies.add(inVision.get(i));
		}
		return inVision;
	}
	
	public VecUnit inRange(Unit unit){
		return gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), 
				unit.attackRange(), team);
	}
	
	public boolean inCombat(Unit unit){
		if(inVision(unit).size() > 0){
			combatState = true;
		}else{
			combatState = false;
		}
		return combatState;
	}
	
	public MapLocation setRallyPoint(Unit unit){
		return unit.location().mapLocation();
	}
	
	//determines what unit to target
 	public Unit target(Unit unit){
		Unit target = null;
		Unit ranger = null;
		long distanceR = 0;
		Unit mage = null;
		long distanceM = 0;
		Unit other = null;
		long distanceO = 0;
		Unit curr;
		VecUnit list = inRange(unit);
		MapLocation location = unit.location().mapLocation();
		long distance;
		if(combatState){
			for(int i = 0; i < list.size(); i++ ){
				curr = list.get(i);
				distance = location.distanceSquaredTo(curr.location().mapLocation());
				//nearest mage
				if(unit.unitType() == UnitType.Ranger){
					if(curr.unitType() == UnitType.Mage){
						if(mage == null){
							mage = curr;
							distanceM = distance;
						}else if(distance < distanceM){
							mage = curr;
							distanceM = distance;
						}
						//nearest ranger
					}else if(curr.unitType() == UnitType.Ranger){
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
					if(mage != null){
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
}
