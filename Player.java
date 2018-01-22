// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {
	
	public static boolean factoriesBuilt = true;
	public static int numFactories = 0;
	
    public static void main(String[] args) {

        GameController gc = new GameController();
        VectorField.initWalls(gc);

        VecUnit units;
        Unit[] closestUnits;
        int stage = 0;
        
        Pathfinding.initMap(gc.startingMap(Planet.Earth));
        for(int i = 0; i < Pathfinding.earthMap.length; i++) {
        		for(int j = 0; j < Pathfinding.earthMap.length; j++) {
        			System.out.print(Pathfinding.earthMap[j][i]);
        		}
        		System.out.println();
        }
        while (stage == 0) {
        	
        	System.out.println(gc.round());
        	 	units = gc.myUnits();
        	 	stage += Start.runTurn(gc, units); 
        	 	
        	 	gc.nextTurn();
        }

        MapLocation factory;
        
        while(stage == 1) {
        	
        		if(!factoriesBuilt) {
        			factory = Start.factories.get(numFactories);
        			
        			closestUnits = Factories.getClosest(gc, units, factory);
        			Factories.sendUnits(gc, closestUnits, factory);
        			
        			factoriesBuilt = true;
        		}
        		
        		gc.nextTurn();
        }
        
        while(true) {
        	 	gc.nextTurn();
        }
    }
}