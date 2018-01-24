import bc.*;
import java.util.ArrayList;

public class Rocket {
	
    public static VectorField toRocket;
    public static MapLocation rocketLoc;
    public static Unit unit;
    public static int unitId;
    public static MapLocation unitLoc;
    public static Unit[] closestUnits;
	
	public static void runTurn(GameController gc, ArrayList<Unit> units) {
		
		if(Start.rockets.size() == 0) {
			buildRocket(gc, units);
			return;
		}
		
		for(Unit rocket : Start.rockets) {
			
			toRocket = new VectorField();
			rocketLoc = rocket.location().mapLocation();
			toRocket.setTarget(rocketLoc);
			
			if(rocket.health() < 200) {
				
				closestUnits = Factories.getClosest(gc, Player.availableUnits, rocket, toRocket);

				Factories.sendUnits(gc, closestUnits, rocket, toRocket);

				for(int i = 0; i < closestUnits.length; i++) {
					
					unit = closestUnits[i];
					for(int j = 0; j < Player.availableUnits.size(); j++) {
						
						if(Player.availableUnits.get(j).equals(unit)) {
							Player.availableUnits.remove(unit);
							j--;
						}
					}
				}
			}
		}
			
		for(int i = 0; i < Player.availableUnits.size(); i++) {
			
			unit = Player.availableUnits.get(i);
			unitId = unit.id();

			if (unit.location().isInGarrison() || unit.location().isInSpace()) {
				continue;
			}
			unitLoc = unit.location().mapLocation();

			if(gc.isMoveReady(unitId) && (!unitLoc.isAdjacentTo(rocketLoc) || Start.rockets.size() == 0)) {
				Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unitLoc));
			}
		}
	}
	
	private static boolean buildRocket(GameController gc, ArrayList<Unit> units) {
		if (units.size() == 0) {
			return false;
		}

		Unit unit;
		int unitId;
		Unit idealUnit = units.get(0);
		int idealUnitId = idealUnit.id();
		MapLocation attempt;
		MapLocation attemptAround;
		Direction idealDir = Direction.North;
		Direction[] allDirs = Direction.values();
		int bestOption = 0;
		int numOccupiable = 0;
		
		int x, y;
		
		for(int i = 0; i < units.size(); i++) {
			unit = units.get(i);
			unitId = unit.id();

			for(Direction dir : allDirs) {
				if(dir == Direction.Center) continue;
				if(gc.canBlueprint(unitId, UnitType.Rocket, dir)) {
					
					attempt = unit.location().mapLocation().add(dir);
					for(Direction dir1 : allDirs) {
						try {
							if(dir1 == Direction.Center) continue;
							
							attemptAround = attempt.add(dir1);
							x = attemptAround.getX();
							y = attemptAround.getY();
							
							if(VectorField.terrain[x][y] == 1) {
								numOccupiable++;
							}
						}
						catch(Exception E) {
							// do nothing
						}
					}
					
					if(numOccupiable == 8) {
						gc.blueprint(unitId, UnitType.Rocket, dir);
						return true;
					}
					
					else if (numOccupiable > bestOption) {
						idealDir = dir;
						bestOption = numOccupiable;
						idealUnit = unit;
						idealUnitId = idealUnit.id();
					}
				}
				
				numOccupiable = 0;
			}
		}

		if(gc.canBlueprint(idealUnitId, UnitType.Rocket, idealDir)) {
			gc.blueprint(idealUnitId, UnitType.Rocket, idealDir);
			System.out.println("Rocket blueprinted");
			return true;
		}
		
		return false;
	}


}