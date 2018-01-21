import bc.*;
import java.util.*;

public class Factories {

	public static int runTurn(GameController gc, VecUnit units) {
		
		MapLocation factory = Start.factories.get(0);
		VectorField toFactory = new VectorField(Pathfinding.eWidth, Planet.Earth);
		toFactory.setTarget(factory);
		
		Unit[] closestUnits = new Unit[8];
		int[] magnitudes = new int[8];
		Unit unit = units.get(0);
		int magnitude = toFactory.getMagnitude(unit.location().mapLocation());
		int last = 0;
	
		closestUnits[0] = unit;
		magnitudes[0] = magnitude;
				
		for(int i = 1; i < units.size(); i++) {
			
			unit = units.get(i);
			magnitude = toFactory.getMagnitude(unit.location().mapLocation());
			
			for(int j = last; j >= 0; j--) {
				if(magnitude < magnitudes[j]) {
					if(j + 1 < 8) {
						magnitudes[j+1] = magnitudes[j];
						closestUnits[j+1] = closestUnits[j];
					}
					magnitudes[j] = magnitude;
					closestUnits[j] = unit;
				}
				else {
					if(j + 1 < 8) {
						magnitudes[j+1] = magnitude;
						closestUnits[j+1] = unit;
					}
					break;
				}
			}
		}	
	}
}
