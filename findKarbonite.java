import bc.*;
import java.util.*;

public class findKarbonite{
    static ArrayList<MapLocation> kTargets = new ArrayList<MapLocation>();
    static VectorField karboniteField;
    public static int avaSq;
    public static int totalKarbOnMap;
    public static MapLocation spawn;
    
    public static void initKarb(GameController gc) {
        int height = VectorField.height;
        int width = VectorField.width;
        Planet planet = VectorField.planet;
        PlanetMap map = gc.startingMap(planet);
        MapLocation loc;
        
        
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            		loc = new MapLocation(planet, j, i);

                    int karb = (int)map.initialKarboniteAt(loc);

            		if (karb != 0) {
            			kTargets.add(loc);
            			avaSq++;
                        totalKarbOnMap += karb;
            			continue;
                }
            		else if (map.isPassableTerrainAt(loc) == 1) {
            			avaSq++;
            		}
            }
        }
    }
    public static void getOppositeSpawn(Unit unit) {
    	spawn = unit.location().mapLocation();
    	int x = VectorField.width - spawn.getX();
    	int y = VectorField.height -spawn.getY();
    	spawn = new MapLocation(VectorField.planet, x, y);
    }

    public static void vectFieldKarb(GameController gc) {
        findKarbonite.karboniteField = new VectorField();
        initKarb(gc);
        karboniteField.setTargets(kTargets);
    }

   public static void updateFieldKarb() {
	   
	   MapLocation loc;
	   
       karboniteField = new VectorField();
        ArrayList<MapLocation> mined = Start.minedKarbonite;
        for (int i = 0; i < mined.size(); i++) {
            MapLocation miningLoc = mined.get(i);
            for(int j = 0; j < kTargets.size(); j++) {
            	
            		loc = kTargets.get(j);
   
            		if (miningLoc.equals(loc)) {
            			kTargets.remove(loc);
            		}
            }
        }
        karboniteField.setTargets(kTargets);
    }
}
