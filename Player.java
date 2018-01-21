// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {
    public static void main(String[] args) {

        GameController gc = new GameController();

        VecUnit units;
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

        while(stage == 1) {
        		units = gc.myUnits();
        		stage += Factories.runTurn(gc, units)
        		
        		gc.nextTurn();
        }
        
        while(true) {
        	 	gc.nextTurn();
        }
    }
}