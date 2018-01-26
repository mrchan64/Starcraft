import bc.*;

import java.util.ArrayList;

public class CommandUnits {
	
	static ArrayList<UnitType> validUnits = new ArrayList<UnitType>();
	static VectorField[][] storedField;
	static Unit[][] enemies;		//updates
	static boolean[][] hasEnemy;	//updates
	static MapLocation[][] squares;
	static int centerMassX;
	static int centerMassY;
	static MapLocation centerMass;
	static Team team;
	
	static int radius;
	static MapLocation center;
	
	public static void initCommand(GameController gc){
		storedField = new VectorField[VectorField.width][VectorField.height];
		enemies = new Unit[VectorField.width][VectorField.height];
		hasEnemy = new boolean[VectorField.width][VectorField.height];
		squares = findKarbonite.mapLocations;
		centerMass = new MapLocation(VectorField.planet, 0, 0);
		team = gc.team();
		validUnits.add(UnitType.Ranger);
		validUnits.add(UnitType.Knight);
		validUnits.add(UnitType.Mage);
		VecUnit temp = gc.myUnits();
		MapLocation loc;
		int x1, y1, x2, y2;
		for(int i = 0; i<temp.size(); i++){
			Unit unit = temp.get(i);
			loc = unit.location().mapLocation();
			x1 = loc.getX();
			y1 = loc.getY();
			x2 = VectorField.width - 1 - x1;
			y2 = VectorField.height - 1 - y1;
			hasEnemy[x1][y2] = true;
			hasEnemy[x2][y2] = true;
			hasEnemy[x2][y1] = true;
		}
		
	}

	public static void runTurn(GameController gc){
		if(gc.getTimeLeftMs()<=2000){
			runEasyTurn(gc);
			return;
		}
		//long time = System.currentTimeMillis();
		VecUnit units = gc.myUnits();
		ArrayList<Unit> availableUnits = new ArrayList<Unit>();
		ArrayList<Long> availableUnitsrange = new ArrayList<Long>();
		int size = (int) units.size();
		centerMassX = 0;
		centerMassY = 0;
		
		//get center of mass
		Unit unit;
		UnitType unitType;
		MapLocation loc;
		for(int i = 0; i<size; i++){
			unit = units.get(i);
			unitType = unit.unitType();
			if(validUnits.contains(unitType)){
				if(unit.location().isInGarrison() || unit.location().isInSpace())continue;
				loc = unit.location().mapLocation();
				centerMassX+=loc.getX();
				centerMassY+=loc.getY();
				if(unitType == UnitType.Ranger && unit.rangerIsSniping()!=0)continue;
				int id = unit.id();
				long range = 0;
				//if(unitType == UnitType.Ranger && gc.isBeginSnipeReady(id))range = unit.abilityRange();
				//else if(unitType == UnitType.Mage && gc.isBlinkReady(id))range = unit.abilityRange();
				/*else */if(unitType == UnitType.Knight && gc.isJavelinReady(id))range = unit.abilityRange();
				else if(gc.isAttackReady(id)) range = unit.attackRange();
				availableUnitsrange.add(range);
				availableUnits.add(unit);
			}
		}
		if(size!=0){
			centerMassX /= size;
			centerMassY /= size;
			centerMass = new MapLocation(VectorField.planet, centerMassX, centerMassY);
		}else
			return;
		//update enemies and prioritize them
		int esize;
		ArrayList<MapLocation> priorityEnemies = new ArrayList<MapLocation>();
		for(int i = 0; i<VectorField.width; i++){
			for(int j = 0; j<VectorField.height; j++){
				if(gc.canSenseLocation(squares[i][j])){
					
					try{
						unit = gc.senseUnitAtLocation(squares[i][j]);
						if(unit.team() != team){
							enemies[i][j] = unit;
							hasEnemy[i][j] = true;
						}
					}catch(Exception e){
						enemies[i][j] = null;
						hasEnemy[i][j] = false;
					}
				}
				if(hasEnemy[i][j]){
					esize = priorityEnemies.size();
					for(int k = 0; k<esize; k++){
						loc = priorityEnemies.get(k);
						if(compareUnits(i,j,loc.getX(),loc.getY())){
							priorityEnemies.add(k, squares[i][j]);
							break;
						}
						if(k+1==esize){
							priorityEnemies.add(squares[i][j]);
						}
					}
					if(esize == 0)priorityEnemies.add(squares[i][j]);
				}
			}
		}
		
		//TODO what to do when there are no enemies
		LineOfScrimmage.runTurn(gc, availableUnits);
		boolean hi = true;
		if(hi)return;
		
		//assign units that can attack priority enemies
		ArrayList<ArrayList<Unit>> assignedToEnemy = new ArrayList<ArrayList<Unit>>();
		ArrayList<Integer> totalDamageToEnemy = new ArrayList<Integer>();
		ArrayList<Unit> temp;
		int ind, dmg;
		for(MapLocation enemy : priorityEnemies){
			int x = enemy.getX();
			int y = enemy.getY();
			temp = getClosestCanAttack(availableUnits, availableUnitsrange, x, y);
			assignedToEnemy.add(temp);
			dmg = 0;
			for(Unit mine: temp){
				if(mine.unitType() == UnitType.Ranger && mine.location().mapLocation().distanceSquaredTo(squares[x][y])<=mine.rangerCannotAttackRange())
				dmg+=mine.damage();
				ind = availableUnits.indexOf(mine);
				availableUnits.remove(ind);
				availableUnitsrange.remove(ind);
			}
			totalDamageToEnemy.add(dmg);
		}
		
		//assign leftover units
		size = priorityEnemies.size();
		Unit enemy;
		for(int i = 0; i<size; i++){
			if(availableUnits.size() == 0)break;
			loc = priorityEnemies.get(i);
			int x = loc.getX();
			int y = loc.getY();
			enemy = enemies[x][y];
			if(availableUnits.size() == 0)break;
			int damage = totalDamageToEnemy.get(i);
			if(enemy != null && damage >= enemy.health())continue;
			temp = getClosestLeftOver(availableUnits, damage, x, y);
			assignedToEnemy.get(i).addAll(temp);
			for(Unit mine: temp){
				if(availableUnits.size() == 0)break;
				ind = availableUnits.indexOf(mine);
				availableUnits.remove(ind);
				availableUnitsrange.remove(ind);
			}
		}
		
		if(priorityEnemies.size()>0 && availableUnits.size()>0){
			int toEach = (availableUnits.size() / priorityEnemies.size())+1;
			for(ArrayList<Unit> assigned:assignedToEnemy){
				if(availableUnits.size()==0)break;
				for(int i = 0; i<toEach; i++){
					assigned.add(availableUnits.remove(0));
					if(availableUnits.size()==0)break;
				}
			}
		}
		
		//assign the instructions
		for(int i = 0; i<size; i++){
			loc = priorityEnemies.get(i);
			temp = assignedToEnemy.get(i);
			for(Unit mine : temp){
				attackMove(gc, mine, loc.getX(), loc.getY());
			}
		}
		//System.out.println("Run Combat Turn took "+(System.currentTimeMillis()-time)+"ms and time left is "+gc.getTimeLeftMs()+"ms");
	}
	
	private static void runEasyTurn(GameController gc){
		
	}
	
	// returns true if higher priority
	private static boolean compareUnits(int x1, int y1, int x2, int y2){
		if(enemies[x1][y1] == null || enemies[x2][y2] == null){
			return enemies[x2][y2]==null;
		}
		if(enemies[x1][y1].unitType()==enemies[x2][y2].unitType()){
			int dist1 = (int) centerMass.distanceSquaredTo(squares[x1][y1]);
			int dist2 = (int) centerMass.distanceSquaredTo(squares[x2][y2]);
			return dist1<=dist2;
		}
		int p1 = 7;
		int p2 = 7;
		UnitType unitType = enemies[x1][y1].unitType();
		if(unitType == UnitType.Healer)p1 = 0;
		else if(unitType == UnitType.Ranger)p1 = 1;
		else if(unitType == UnitType.Mage)p1 = 2;
		else if(unitType == UnitType.Knight)p1 = 3;
		else if(unitType == UnitType.Factory)p1 = 4;
		else if(unitType == UnitType.Rocket)p1 = 5;
		else if(unitType == UnitType.Worker)p1 = 6;
		unitType = enemies[x2][y2].unitType();
		if(unitType == UnitType.Healer)p1 = 0;
		else if(unitType == UnitType.Ranger)p1 = 1;
		else if(unitType == UnitType.Mage)p1 = 2;
		else if(unitType == UnitType.Knight)p1 = 3;
		else if(unitType == UnitType.Factory)p1 = 4;
		else if(unitType == UnitType.Rocket)p1 = 5;
		else if(unitType == UnitType.Worker)p1 = 6;
		return p1 <= p2;
	}
	
	private static ArrayList<Unit> getClosestCanAttack(ArrayList<Unit> avail, ArrayList<Long> range, int x, int y){
		ArrayList<Unit> ret = new ArrayList<Unit>();
		ArrayList<Long> dist = new ArrayList<Long>();
		if(enemies[x][y]==null)return ret;
		Unit enemy = enemies[x][y];
		long enemyHealth = enemy.health();
		long r, dist1;
		int rets, dmg;
		Unit a, re;
		MapLocation aloc;
		boolean inserted;
		for(int i = 0; i<avail.size(); i++){
			r = range.get(i);
			if(r==0)continue;
			a = avail.get(i);
			aloc = a.location().mapLocation();
			dist1 = aloc.distanceSquaredTo(squares[x][y]);
			if(dist1>r)continue;
			if(a.unitType() == UnitType.Ranger && dist1 <= a.rangerCannotAttackRange()) continue;
			rets = ret.size();
			inserted = false;
			dmg = 0;
			for(int j = 0; j<rets; j++){
				if(!inserted && dist1<= dist.get(j)){
					ret.add(j, a);
					dist.add(j, dist1);
					inserted = true;
				}
				if(!inserted && j+1 == rets){
					ret.add(a);
					dist.add(dist1);
					inserted = true;
				}
				re = ret.get(j);
				dmg+=re.damage();
				if(dmg>=enemyHealth){
					ret.subList(j+1, ret.size()).clear();
					dist.subList(j+1, ret.size()).clear();
					break;
				}
			}
			if(rets == 0){
				ret.add(a);
				dist.add(dist1);
			}
		}
		return ret;
	}
	
	private static ArrayList<Unit> getClosestLeftOver(ArrayList<Unit> avail,int damage, int x, int y){
		// TODO fix this code to cover for opponent units are unreachable
		if(enemies[x][y]==null){
			avail.clear();
			return avail;
		}
		ArrayList<Unit> ret = new ArrayList<Unit>();
		ArrayList<Integer> mag = new ArrayList<Integer>();
		VectorField vf = storedField[x][y];
		int magnitude, rets, dmg;
		long enemyHealth = enemies[x][y].health();
		boolean inserted;
		for(Unit mine: avail){
			if(vf==null){
				vf = new VectorField();
				vf.setTarget(squares[x][y]);
				storedField[x][y] = vf;
			}
			MapLocation loc = mine.location().mapLocation();
			magnitude = vf.getMagnitude(squares[loc.getX()][loc.getY()]);
			rets = ret.size();
			dmg = damage;
			inserted = false;
			for(int i = 0; i<rets; i++){
				if(!inserted && magnitude<=mag.get(i)){
					ret.add(i, mine);
					mag.add(i, magnitude);
					inserted = true;
				}
				if(!inserted && i+1 == rets){
					ret.add(mine);
					mag.add(magnitude);
					inserted = true;
				}
				dmg+=ret.get(i).damage();
				if(dmg>=enemyHealth){
					ret.subList(i+1, ret.size()).clear();
					mag.subList(i+1, ret.size()).clear();
					break;
				}
			}
			if(rets == 0){
				ret.add(mine);
				mag.add(magnitude);
			}
		}
		return ret;
	}
	
	private static void attackMove(GameController gc, Unit unit, int x, int y){
		UnitType unitType = unit.unitType();
		int id = unit.id();
		int enemyId = enemies[x][y].id();
		//if(unitType == UnitType.Ranger && gc.isBeginSnipeReady(id) && gc.canBeginSnipe(id, squares[x][y])) gc.beginSnipe(id, squares[x][y]);
		//if(unitType == UnitType.Mage && gc.isBlinkReady(id) && gc.is)range = unit.abilityRange(); Blink code need fix
		if(unitType == UnitType.Knight && gc.isJavelinReady(id) && gc.canJavelin(id, enemyId))gc.javelin(id, enemyId);
		if(gc.isAttackReady(id) && gc.canAttack(id, enemyId)) gc.attack(id, enemyId);
		
		VectorField vf = storedField[x][y];
		if(vf==null){
			vf = new VectorField();
			vf.setTarget(squares[x][y]);
			storedField[x][y] = vf;
		}
		//Ranger has range minimum;
		Direction dir = vf.getDirection(unit.location().mapLocation());
		if(unitType == UnitType.Ranger && unit.location().mapLocation().distanceSquaredTo(squares[x][y])<=unit.rangerCannotAttackRange()){
			dir = bc.bcDirectionOpposite(dir);
		}
		Factories.moveToClosestDirection(gc, unit, dir);
	}
	
	
}
