// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {
	
	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;
	
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
        ArrayList<Unit> availableUnits;
        
        VectorField toFactory = new VectorField();
        MapLocation factory = new MapLocation(Planet.Earth, 0, 0);

        
        while (true) {
        	
        		Start.factories = new ArrayList<>();
        		UnitBuildOrder.builtFacts = new ArrayList<>();
        		numFactories = 0;
        		
        		units = gc.myUnits();
        		
        		for(int i = 0; i < units.size(); i++) {
        			
        			unit = units.get(i);
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
        		}
        	
	        	if(stage >= 0) {

	        		if(stage == 0) {
	        			stage += Start.runTurn(gc, units);
	        		}
	        		
	        		else if(numFactories - 1 < findKarbonite.avaSq / 100) {
			    			Start.runTurn(gc, units);
			    	}
	        	}
	        
	        if(stage >= 1) {
	        	
	        		availableUnits = new ArrayList<>();
        		
	        		for(int i = 0; i < units.size(); i++) {
	        			availableUnits.add(units.get(i));
	        		}

	        		for(Unit fac : Start.factories) {

	        			if(!factoriesBuilt) {
		    			
	        				toFactory = new VectorField();
		    				toFactory.setTarget(fac.location().mapLocation());
		    				factoriesBuilt = true;
	        			}
		    			
	        			if(fac.health() < 300) {
		    				// IF CANT BUILD THEN REPAIR
	        				System.out.println(fac.health());
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
		    			
	        		for(int i = 0; i < units.size(); i++) {
		    			
		    			unit = units.get(i);
		    			unitId = unit.id();
		    			unitLoc = unit.location().mapLocation();
		    			
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
		    			
		    			if(gc.isMoveReady(unitId) && (!unitLoc.isAdjacentTo(factory) || Start.factories.size() == 0)) {
		    				Factories.moveToClosestDirection(gc, unit, findKarbonite.karboniteField.getDirection(unitLoc));
		    			}
	    			}
		    		
	        		if(gc.round() >= 500 && gc.karbonite() >= 75) {
		    			
		    				stage = 2;
		    		}
	        	}
	        		
	        if(stage >= 2) {
	        	
	      		//rockets
	        	
	        	}
	        
	        	gc.nextTurn();
        }
    }
}