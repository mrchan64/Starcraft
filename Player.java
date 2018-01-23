// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {

	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;
	public static ArrayList<Unit> availableUnits;
	
    public static void main(String[] args) {

	public static void main(String[] args) {

		GameController gc = new GameController();
		VectorField.initWalls(gc);
		findKarbonite.vectFieldKarb(gc);
		Upgrades.upgradeUnits(gc);

        VecUnit units = gc.myUnits();
        Unit unit;
        int unitId;
        MapLocation unitLoc;
        Unit[] closestUnits;
        int stage = 0;
        
        VectorField toFactory = new VectorField();
        MapLocation factory = new MapLocation(Planet.Earth, 0, 0);
    		Combat.rangerList = new ArrayList<>(); 

			  units = gc.myUnits();

			  for(int i = 0; i < units.size(); i++) {

				unit = units.get(i);

				if (unit.unitType() == UnitType.Ranger) {
					for (int j = 0; j < Combat.combatList.size(); j++) {
						if (!Combat.combatList.get(j).equals(unit)) {
							Combat.rangerList.add(unit);
						}
					}
				}

        
        while (true) {
        	
        		Start.factories = new ArrayList<>();
        		UnitBuildOrder.builtFacts = new ArrayList<>();
        		numFactories = 0;
        		
        		units = gc.myUnits();
        		
        		for(int i = 0; i < units.size(); i++) {
        			
        			unit = units.get(i);
        			unitLoc = unit.location().mapLocation();
        			
        			if(unit.unitType() == UnitType.Factory) {
        				numFactories++;

        				if(unit.health() < 300) {
        					Start.factories.add(unit);
        					System.out.println("Factory size: "+Start.factories.size());
        				}
        				else {
        					UnitBuildOrder.builtFacts.add(unit);
        					System.out.println("Completed Factory size: "+UnitBuildOrder.builtFacts.size());
        				}
        			}
        			
	    			if(unit.unitType() != UnitType.Worker) continue;
    				
	    			for(Direction dir : Start.directions) {
	    				if(gc.canHarvest(unit.id(), dir)) {
	    						
	    					gc.harvest(unit.id(), dir);
	    					Start.minedKarbonite.add(unitLoc.add(dir));
	    						
	    					System.out.println("Karbonite Mined: " + gc.karbonite());
	    					findKarbonite.updateFieldKarb();
	    					break;
	    				}
	    			}
        		}
        		
        		if(stage >= 2) {
        			
        			// rockets!!!!!
        		}
        		
        		availableUnits = new ArrayList<>();
    		
        		for(int i = 0; i < units.size(); i++) {
        			
        			unit = units.get(i);
        			
        			if(unit.unitType() == UnitType.Worker) availableUnits.add(units.get(i));
        		}
	        
	        if(stage >= 1) {

	        		for(Unit fac : Start.factories) {
		    			
	        			toFactory = new VectorField();
		    			toFactory.setTarget(fac.location().mapLocation());
		    			
	        			if(fac.health() < 300) {
	        				
	        				System.out.println("starting sending units to factories");
		    				// IF CANT BUILD THEN REPAIR
	        				System.out.println("factory " + fac.id() + "health: " + fac.health());
	        				closestUnits = Factories.getClosest(gc, availableUnits, fac, toFactory);
	        				System.out.println("num of closest units: " + closestUnits.length);
	        				Factories.sendUnits(gc, closestUnits, fac, toFactory);
	        				
	        				System.out.println("AVAIL UNITS BEFORE: " + availableUnits.size());
	        				for(int i = 0; i < closestUnits.length; i++) {
	        					
	        					unit = closestUnits[i];
	        					for(int j = 0; j < availableUnits.size(); j++) {
	        						
	        						//System.out.println(availableUnits.get(j) == null);
	        						//System.out.println(unit == null);
	        						if(availableUnits.get(j).equals(unit)) {
	        							availableUnits.remove(unit);
	        							j--;
	        						}
	        					}
	        				}
	        			}
        				
        				System.out.println("AVAIL UNITS NOW: " + availableUnits.size());
	        		}
		    			
	        		for(int i = 0; i < availableUnits.size(); i++) {
		    			
		    			unit = availableUnits.get(i);
		    			unitId = unit.id();
		    			unitLoc = unit.location().mapLocation();
		    			
		    			if(gc.isMoveReady(unitId) && (!unitLoc.isAdjacentTo(factory) || Start.factories.size() == 0)) {
		    				Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unitLoc));
		    			}
	    			}

	    			for (int i = 0; i < builtFacts.size(); i++) {
	    				builtFacts.get(i).queueUnitsAllFactories(gc, UnitType.Ranger);
	    			}
		    		
	        		if(gc.round() >= 500 && gc.karbonite() >= 75) {
		    			
		    				stage = 2;
		    		}
	        	}
	        
	        if(stage >= 0) {

        			if(stage == 0) {
        				stage += Start.runTurn(gc, availableUnits);
        			}
        		
        			else if(numFactories - 1 < findKarbonite.avaSq / 100) {
        				Start.runTurn(gc, availableUnits);
        			}
        		}
	        
	        	gc.nextTurn();
        }
    }

}