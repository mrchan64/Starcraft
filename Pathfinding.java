import bc.*;
import java.util.*;

public class Pathfinding {
    public static Square[][] earthMap;
    public static Square[][] marsMap;
    public static int pTerrain = 1;
    public static int karbonite = 2;
    public static int notPTerrain = 3;
    public static int factoryLoc = 4;
    public static int eHeight;
    public static int eWidth;


    public static void initMap(PlanetMap map) {        
        int height = (int)map.getHeight();
        int width = (int)map.getWidth();
        eHeight = height;
        eWidth = width;
        Planet plt = map.getPlanet();
        if (plt == Planet.Earth) {
            earthMap = new Square[height][width];
        }
        else {
            marsMap = new Square[height][width];
        }
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (plt == Planet.Earth) {
                    MapLocation loc = new MapLocation(plt, j, i);
                    if (map.initialKarboniteAt(loc) != 0) {
                        //System.out.println("karbonite");
                        earthMap[i][j] = new Square(j, i, karbonite);
                        continue;
                    }
                    else if (map.isPassableTerrainAt(loc) == 1) {
                        //System.out.println("passable");
                        earthMap[i][j] = new Square(j, i, pTerrain);
                        continue;
                    }
                    else {
                        //System.out.println("not passable");
                        Square sq = new Square (j, i, notPTerrain);
                        earthMap[i][j] = sq;
                        //System.out.println(earthMap[i][j]);
                        continue;
                    }
                }
                else {
                    MapLocation loc = new MapLocation(plt, j, i);
                    if (map.initialKarboniteAt(loc) != 0) {
                        marsMap[i][j] = new Square(j, i, karbonite);
                        continue;
                    }
                    else if (map.isPassableTerrainAt(loc) == 1) {
                        marsMap[i][j] = new Square(j, i, pTerrain);
                        continue;
                    }
                    else {
                        marsMap[i][j] = new Square(j, i, notPTerrain);
                        continue;
                    }
                }
            }
        }
    }

    public static void moveOnPath(ArrayList<Direction> list, int unitId, GameController gc) {
        for (int i = 0; i < list.size(); i++) {
            Direction dir = list.get(i);
            if (gc.isMoveReady(unitId) && gc.canMove(unitId, dir)) {
                gc.moveRobot(unitId, dir);
            }
            else {
               break;
            }
        }
    }

    public static Direction getPath(MapLocation loc) {
        int x = loc.getX();
        int y = loc.getY();
        Square start = new Square (x, y, 7); //temp type
        ArrayList<Direction> newList = pathConverter(calcPath(start));
        return newList.remove(0);
    }


    public static ArrayList<Direction> pathConverter(ArrayList<Square> list) {
        ArrayList<Direction> newList = new ArrayList<Direction>();
        for (int i = 1; i < list.size(); i++) {
            Square startSq = list.get(i-1);
            Square nextSq = list.get(i);
            if (startSq.getX() == nextSq.getX() && startSq.getY() == nextSq.getY()) {
                newList.add(Direction.North);
            }
            else if (startSq.getX() < nextSq.getX() && startSq.getY() < nextSq.getY()) {
                newList.add(Direction.Northwest);
            }
            else if (startSq.getX() < nextSq.getX() && startSq.getY() == nextSq.getY()) {
                newList.add(Direction.West);
            }
            else if (startSq.getX() < nextSq.getX() && startSq.getY() > nextSq.getY()) {
                newList.add(Direction.Southwest);
            }
            else if (startSq.getX() == nextSq.getX() && startSq.getY() > nextSq.getY()) {
                newList.add(Direction.South);
            }
            else if (startSq.getX() > nextSq.getX() && startSq.getY() > nextSq.getY()) {
                newList.add(Direction.Southeast);
            }
            else if (startSq.getX() > nextSq.getX() && startSq.getY() == nextSq.getY()) {
                newList.add(Direction.East);
            }
            else if (startSq.getX() > nextSq.getX() && startSq.getY() < nextSq.getY()) {
                newList.add(Direction.Northeast);
            }
            else {
                newList.add(Direction.Center);
            }
        }

        return newList;
    }

    public static ArrayList<Square> calcPath(Square start) {
        WorkerMovement.findKarbonite(start);
        ArrayList<Square> list = new ArrayList<Square>();
        while (!WorkerMovement.trace.empty()) {
            list.add(WorkerMovement.trace.pop());
        }
        return list;
    }

}