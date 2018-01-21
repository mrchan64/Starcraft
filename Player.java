// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {
    public static void main(String[] args) {

        GameController gc = new GameController();

        VecUnit units;
        int stage = 0;
        
        while (stage == 0) {
        	 	units = gc.myUnits();
        	 	stage += Start.runTurn(gc, units); 
        	 	
        	 	gc.nextTurn();
        }

        while(stage == 1) {
        		units = gc.myUnits();
        		

        		gc.nextTurn();
        }
        
        while(true) {
        	 	gc.nextTurn();
        }
    }
}