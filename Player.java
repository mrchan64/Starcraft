// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.ArrayList;

public class Player {

	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;

	public static ArrayList<Unit> availableUnits;
	public static Team team;
	public static Team eTeam;

	public static GameController gc;

    public static VecUnit units;
    static Unit unit;
    public static MapLocation unitLoc;
    public static int stage = 0;
    public static UnitType type;
    public static int health;

	public static void main(String[] args) {

		gc = new GameController();
		VectorField.initWalls(gc);
		findKarbonite.initKarb(gc);
		Upgrades.upgradeUnitsSmall(gc);
		units = gc.myUnits();
        
        if (gc.planet() == Planet.Earth) {
        	findKarbonite.getOppositeSpawn(units.get(0));
		    team = units.get(0).team();
		    if(team== Team.Red){
		    	eTeam = Team.Blue;
		    }else{
		    	eTeam = Team.Red;
		    }
		    Combat.setOppositeSpawn();
        }
        else {
        	team = gc.team();
		    if(team == Team.Red){
		    	eTeam = Team.Blue;
		    }else{
		    	eTeam = Team.Red;
		    }
        }

        while (true) {
        	System.out.println("Currently Round "+gc.round());
        		UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Ranger);

        		Start.factories = new ArrayList<>();
        		UnitBuildOrder.builtFacts = new ArrayList<>();
        		Start.rockets = new ArrayList<>();
        		UnitBuildOrder.builtRocks = new ArrayList<>();
        		availableUnits = new ArrayList<>();
        		numFactories = 0;

			
            findKarbonite.updateFieldKarb(gc);

        		units = gc.myUnits();
        		
        		for(int i = 0; i < units.size(); i++) {
        			
        			unit = units.get(i);
        			type = unit.unitType();
        			health = (int)unit.health();

                if (gc.planet() == Planet.Mars && type == UnitType.Rocket) {
                    UnitBuildOrder.deployUnits(gc, unit);
                }
        			
	         	if (type == UnitType.Ranger) {
	         		Combat.rangerList.add(unit);
				}

        			
	         	else if (type == UnitType.Factory) {
        				numFactories++;

        				if(health < 300) {
        					Start.factories.add(unit);
        				}
        				else {
        					UnitBuildOrder.builtFacts.add(unit);
        				}
        			}
        			
        			else if (type == UnitType.Rocket) {
        				if(health < 200) {
        					Start.rockets.add(unit);
        				}
        				else if(unit.location().isOnMap()) {
        					UnitBuildOrder.builtRocks.add(unit);
        				}
        			}
        			
        			else if (type == UnitType.Worker) 
                        //HAVE TO DIFFERENTIATE PLANETS NOW
        				availableUnits.add(unit);
	         	
        			else continue;
    				
	    			for(Direction dir : Start.directions) {
	    				if(gc.canHarvest(unit.id(), dir)) {
	    						
	    					gc.harvest(unit.id(), dir);

	    					break;
	    				}
	    			}
        		}
        		
        		Combat.commands();
        		
        		if(stage >= 2) {
        			
        			Rocket.runTurn(gc, availableUnits);

                    for (int i = 0; i < UnitBuildOrder.builtRocks.size(); i++) {
                        UnitBuildOrder.loadUnits(gc, UnitBuildOrder.builtRocks.get(i), availableUnits);
                    }

        		}
	        
	        if(stage >= 1) {

                    if (gc.planet()== Planet.Earth && gc.round() < 675) {
                        Factories.runTurn(gc, availableUnits);
                    }

                    if (gc.planet() == Planet.Earth && gc.round() > 500) {
                        if (Start.numWorkers < 3) {
                            UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Worker);
                        }
                    }

	        		if(gc.karbonite() > 100) {

	        			UnitBuildOrder.queueUnitsAllFactories(gc, UnitType.Ranger);
	        		}
		    		
	        		if(gc.round() >= 300 && gc.planet() == Planet.Earth) { //karbonite condition?
		    			
		    			stage = 2;
		    		}
	        	}
	        
	        if(stage >= 0) {
	      		
    				Start.updateNumWorkers(availableUnits);
    				
        			if(stage == 0) {
        				stage += Start.runTurn(gc, availableUnits);
        			}
        			
        			else if(numFactories - 1 < findKarbonite.avaSq / 100 || Start.numWorkers <= (numFactories + 1) * 8) {
        				Start.runTurn(gc, availableUnits);
        			}
        		}
	        
	        	gc.nextTurn();
        }
    }

}