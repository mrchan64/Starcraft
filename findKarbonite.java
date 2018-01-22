import bc.*;
import java.util.*;

public class findKarbonite{
    ArrayList<MapLocation> kTargets = new ArrayList<MapLocation>();
    VectorField karboniteField;

    public static void initKarb(GameController gc) {
        int height = VectorField.hegiht;
        int width = VectorField.width;
        Planet planet = VectorField.planet;
        PlanetMap map = gc.startingMap(planet);
        MapLocation loc = new MapLocation(planet, 0, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                loc.setX(j);
                loc.setY(i);
                if (map.initialKarboniteAt(loc) != 0) {
                    kTargets.add(loc);
                    continue;
                }
            }
        }
    }

    public static void vectFieldKarb(GameController gc) {
        VectorField karboniteField = new VectorField();
        initKarb(gc);
        karboniteField.setTargets(kTargets);
    }

   /* public static void updateFieldKarb() {
        ArrayList<MapLocation> mined = Start.minedKarbonite;
        for (int i = 0; i < mined.size(); i++) {
            MapLocation miningLoc = mined.get(i);
            if (kTargets.contains(miningLoc)) {
                kTargets.remove(miningLoc);
            }
        }
        karboniteField.setTargets(kTargets);
    }*/
}