// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {

	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;

	public static ArrayList<Unit> availableUnits;


	public static void main(String[] args) {

		GameController gc = new GameController();
		VectorField.initWalls(gc);
		findKarbonite.initKarb(gc);
		Upgrades.upgradeUnits(gc);

        VecUnit units = gc.myUnits();
        Unit unit;
        int unitId;
        MapLocation unitLoc;
        Unit[] closestUnits;
        int stage = 0;
        
        VectorField toFactory = new VectorField();
        MapLocation factory = new MapLocation(Planet.Earth, 0, 0);

        while (true) {
        	
        		Start.factories = new ArrayList<>();
        		UnitBuildOrder.builtFacts = new ArrayList<>();
        		numFactories = 0;
            Combat.rangerList = new ArrayList<>(); 
			
            findKarbonite.updateFieldKarb(gc);
        		
        		units = gc.myUnits();
        		
        		for(int i = 0; i < units.size(); i++) {
        			
        			unit = units.get(i);
        			unitLoc = unit.location().mapLocation();
              
        			if (unit.unitType() == UnitType.Ranger) {
					for (int j = 0; j < Combat.combatList.size(); j++) {
						if (!Combat.combatList.get(j).equals(unit)) {
							Combat.rangerList.add(unit);
						}
					}
				}
        			
        			if(unit.unitType() == UnitType.Factory) {
        				numFactories++;

        				if(unit.health() < 300) {
        					Start.factories.add(unit);
        				}
        				else {
        					UnitBuildOrder.builtFacts.add(unit);
        				}
        			}
        			
	    			if(unit.unitType() != UnitType.Worker) continue;
    				
	    			for(Direction dir : Start.directions) {
	    				if(gc.canHarvest(unit.id(), dir)) {
	    						
	    					gc.harvest(unit.id(), dir);
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
	        				
	        				closestUnits = Factories.getClosest(gc, availableUnits, fac, toFactory);
	        				Factories.sendUnits(gc, closestUnits, fac, toFactory);
	      
	        				for(int i = 0; i < closestUnits.length; i++) {
	        					
	        					unit = closestUnits[i];
	        					for(int j = 0; j < availableUnits.size(); j++) {
	        						
	        						if(availableUnits.get(j).equals(unit)) {
	        							availableUnits.remove(unit);
	        							j--;
	        						}
	        					}
	        				}
	        			}
	        		}
		    			
	        		for(int i = 0; i < availableUnits.size(); i++) {
		    			
		    			unit = availableUnits.get(i);
		    			unitId = unit.id();
		    			unitLoc = unit.location().mapLocation();
		    			
		    			if(gc.isMoveReady(unitId) && (!unitLoc.isAdjacentTo(factory) || Start.factories.size() == 0)) {
		    				Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unitLoc));
		    			}
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