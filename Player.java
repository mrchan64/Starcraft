// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {
    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 0, 14);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();
        int rand = (int) (Math.random() * 8);
        PlanetMap pMap = gc.startingMap(Planet.Earth);
        Pathfinding.initMap(pMap);
        for (int i = 0; i < Pathfinding.earthMap.length; i++) {
            for (int j = 0; j < Pathfinding.earthMap.length; j++) {
                System.out.print(Pathfinding.earthMap[i][j]);
            }
            System.out.println();
        }

        
        System.out.println(Pathfinding.getPath(loc));


        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                // Most methods on gc take unit IDs, instead of the unit objects themselves.
             
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}