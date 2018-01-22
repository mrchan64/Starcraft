// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {
	
	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;
	
    public static void main(String[] args) {

        GameController gc = new GameController();
        VectorField.initWalls(gc);

        VecUnit units = gc.myUnits();
        Unit[] closestUnits;
        int stage = 0;
        
        while (stage == 0) {
        	
        	System.out.println(gc.round());
    	 	units = gc.myUnits();
    	 	stage += Start.runTurn(gc, units); 
    	 	
    	 	gc.nextTurn();
        }

        MapLocation factory;
        
        while(stage == 1) {
        	
    		if(!factoriesBuilt) {
    			VectorField toFactory = new VectorField();
    			
    			factory = Start.factories.get(numFactories - 1);
    			toFactory.setTarget(factory);
    			closestUnits = Factories.getClosest(gc, units, factory, toFactory);
    			
    			Unit test = gc.senseUnitAtLocation(factory);
    			
    			while(test.health() < 300) {
    	        	System.out.println(gc.round());
    			
    				Factories.sendUnits(gc, closestUnits, factory, toFactory);
    				gc.nextTurn();
    			}
    			
    			factoriesBuilt = true;
    		}
    		else {
    			gc.nextTurn();
    		}
        }
        
        while(true) {
        	 	gc.nextTurn();
        }
    }
}