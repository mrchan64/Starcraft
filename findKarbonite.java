import bc.*;
import java.util.ArrayList;

public class findKarbonite {

	static VectorField karboniteField;
	public static int avaSq;
    public static int accSq = 1;
	public static int totalKarb;
    public static MapLocation spawn;
	public static int[][] currentKarbs;
	public static MapLocation[][] mapLocations;
	public static MapLocation[][] marsLocs;
	public static int[][] availMars;
	public static ArrayList<MapLocation> karbLocations;

	public static int mHeight;
	public static int mWidth;
	
	public static AsteroidPattern marsAsters;
	public static ArrayList<MapLocation> marsKarb;


    public static MapLocation[][] bfsSquares;
    public static boolean[][] checked;
    static ArrayList<MapLocation> working = new ArrayList<MapLocation>();
    static ArrayList<MapLocation> done = new ArrayList<MapLocation>();
    static ArrayList<MapLocation> spawns = new ArrayList<MapLocation>();

	public static void initKarb(GameController gc) {

		marsAsters = gc.asteroidPattern();
		karboniteField = new VectorField();
		int height = VectorField.height;
		int width = VectorField.width;
		Planet planet = VectorField.planet;
		PlanetMap map = gc.startingMap(planet);
		currentKarbs = new int[width][height];
		mapLocations = new MapLocation[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				mapLocations[i][j] = new MapLocation(planet, i, j);
				currentKarbs[i][j] = (int) map.initialKarboniteAt(mapLocations[i][j]);
				totalKarb += currentKarbs[i][j];
				if (VectorField.terrain[i][j] == 1)
					avaSq++;
			}
		}

        Start.initSpawn(gc);
        findAccSq(map);

		map = gc.startingMap(Planet.Mars);
		mHeight = (int) map.getHeight();
		mWidth = (int) map.getWidth();
		availMars = new int[mWidth][mHeight];
		marsLocs = new MapLocation[mWidth][mHeight];
        Minesweeper.mineMap = new int[mWidth][mHeight];
		planet = Planet.Mars;

		for (int i = 0; i < mWidth; i++) {
			for (int j = 0; j < mHeight; j++) {
				marsLocs[i][j] = new MapLocation(planet, i, j);
                Minesweeper.mineSweep(marsLocs[i][j], map);
				if (map.isPassableTerrainAt(marsLocs[i][j]) == 1) {
					availMars[i][j] = 1;
				} else {
					availMars[i][j] = 0;
				}
			}
		}
       /* for (int i = 0; i < mWidth; i++) {
            for (int j = 0; j < mHeight; j++) {
                System.out.print(Minesweeper.mineMap[i][j]);
            }
            System.out.println();
        }*/
	}

	public static void updateAsters(GameController gc, int round) {
		
		if(marsAsters.hasAsteroid(round)) {
			System.out.println("1 " + marsKarb == null);
			System.out.println("2 " + marsAsters == null);
			System.out.println("3 " + marsAsters.asteroid(round) == null);
			marsKarb.add(
					marsAsters.asteroid(round)
					.getLocation());
		}
		else {
			return;
		}
		
		karboniteField = new VectorField();
		karboniteField.setTargets(marsKarb);
		updateFieldKarb(gc);
	}
	
	public static void getOppositeSpawn(Unit unit) {

		spawn = unit.location().mapLocation();
		int x = VectorField.width - 1 - spawn.getX();
		int y = VectorField.height - 1 - spawn.getY();
		spawn = new MapLocation(VectorField.planet, x, y);
	}

	public static void updateFieldKarb(GameController gc) {

		ArrayList<MapLocation> temp = new ArrayList<>();

		for (int i = 0; i < VectorField.width; i++) {
			for (int j = 0; j < VectorField.height; j++) {
				try {
					currentKarbs[i][j] = (int) gc.karboniteAt(mapLocations[i][j]);
				} catch (Exception E) {
					// do nothing
				}
				if (currentKarbs[i][j] > 0) {
					temp.add(mapLocations[i][j]);
				}
			}
		}

		karboniteField = new VectorField();
		karboniteField.setTargets(temp);
	}

    public static void findAccSq(PlanetMap map) {
        for (int i = 0; i < spawns.size(); i++) {
            working.add(spawns.get(i));
        }
        bfsSquares = new MapLocation[VectorField.width][VectorField.height];
        checked = new boolean[VectorField.width][VectorField.height];
        while(step(map)) {

        }
       // printNumSquares();
    }  


    private static void addNeighbors(MapLocation loc, PlanetMap map){
        int startX = loc.getX();
        int startY = loc.getY();
        checked[startX][startY]=true;

        MapLocation locAround;
        int x, y;
    
        for(Direction dir : Direction.values()) {
            if(dir == Direction.Center) continue;
            try {
                locAround = loc.add(dir);
                x = locAround.getX();
                y = locAround.getY();
                if (x < 0 || y < 0 || x == VectorField.width|| y == VectorField.height) {
                    continue;
                }
                if(map.isPassableTerrainAt(locAround) == 1 && !checked[x][y]) {
                    checked[x][y]= true;
                    accSq++;
                    working.add(locAround);
                }
            }
            catch(Exception E) {
                // do nothing
            }
        }
    }

    private static boolean step(PlanetMap map){
        if(working.size()==0)return false;
        MapLocation check = working.remove(0);
        int x = check.getX();
        int y = check.getY();
        for(int i = -1; i<=1; i++){
            for(int j = -1; j<=1; j++){
                if(i==0 && j==0)continue;
                if(x+i>=VectorField.width || x+i<0 || y+j>=VectorField.height || y+j<0)continue;

                MapLocation locAround = new MapLocation(Planet.Earth, x, y);
                if(map.isPassableTerrainAt(locAround) != 1)continue;
                
            }
        }
        done.add(check);
        addNeighbors(check, map);
        return true;
    }


    public static void printNumSquares() {
        System.out.println("available: "+avaSq);
        System.out.println("access: "+ accSq);
    }
}
