import bc.*;
import java.util.*;

public class findKarbonite{
    static ArrayList<MapLocation> kTargets = new ArrayList<MapLocation>();
    static VectorField karboniteField;
    public static int avaSq;
    public static int totalKarbOnMap;
    
    public static void initKarb(GameController gc) {
        int height = VectorField.height;
        int width = VectorField.width;
        Planet planet = VectorField.planet;
        PlanetMap map = gc.startingMap(planet);
        MapLocation loc;
        
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            		loc = new MapLocation(planet, j, i);
                    int karb = map.initialKarboniteAt(loc);
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

    public static void vectFieldKarb(GameController gc) {
        findKarbonite.karboniteField = new VectorField();
        initKarb(gc);
        karboniteField.setTargets(kTargets);
    }

   public static void updateFieldKarb() {
        ArrayList<MapLocation> mined = Start.minedKarbonite;
        for (int i = 0; i < mined.size(); i++) {
            MapLocation miningLoc = mined.get(i);
            if (kTargets.contains(miningLoc)) {
                kTargets.remove(miningLoc);
            }
        }
        karboniteField.setTargets(kTargets);
    }
}