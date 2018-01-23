import bc.*;
import java.util.*;

public class findKarbonite{

    static VectorField karboniteField;
    public static int avaSq;
    public static int totalKarbOnMap;
    public static MapLocation spawn;
    public static int[][] currentKarbs;
    public static MapLocation[][] mapLocations;
    
    public static void initKarb(GameController gc) {
    	
    		karboniteField = new VectorField();
        int height = VectorField.height;
        int width = VectorField.width;
        Planet planet = VectorField.planet;
        PlanetMap map = gc.startingMap(planet);
        currentKarbs = new int[width][height];
        mapLocations = new MapLocation[width][height];
        
        for(int i = 0; i < width; i++) {
        		for(int j = 0; j < height; j++) {
        			mapLocations[i][j] = new MapLocation(planet, i, j);
        			currentKarbs[i][j] = (int)map.initialKarboniteAt(mapLocations[i][j]);
        			
        			if(VectorField.terrain[i][j] == 1) avaSq++;
        		}
        }
    }
    
    public static void getOppositeSpawn(Unit unit) {

    	spawn = unit.location().mapLocation();
    	int x = VectorField.width-1 - spawn.getX();
    	int y = VectorField.height-1 - spawn.getY();
    	spawn = new MapLocation(VectorField.planet, x, y);
    }



   public static void updateFieldKarb(GameController gc) {
	  
	   ArrayList<MapLocation> temp = new ArrayList<>();
	   
	   for(int i = 0; i < VectorField.width; i++) {
		   for(int j = 0; j < VectorField.height; j++) {
			   try {
				   currentKarbs[i][j] = (int)gc.karboniteAt(mapLocations[i][j]);
			   }
			   catch(Exception E) {
				   // do nothing
			   }
			   if(currentKarbs[i][j] > 0) {
				   temp.add(mapLocations[i][j]);
			   }
		   }
	   }
	   
       karboniteField = new VectorField();
       karboniteField.setTargets(temp);
    }
}
