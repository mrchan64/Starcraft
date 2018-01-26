import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import bc.*;

public class LineOfScrimmage {
	
	static ArrayList<UnitType> validUnits;
	static Direction[] directions;
	static VectorField[][] storedField;
	static Unit[][] enemies;		//updates
	static MapLocation[][] squares;
	static int[][] terrain;
	static boolean [][] added;
	static MapLocation[] centers;
	static MapLocation[] targets;
	static Team team;
	static int scrimDist = 100;
	static int lastNeighbored = -1;
	static ScrimQueue queue;
	
	public static void initLine(GameController gc){
		validUnits = CommandUnits.validUnits;
		directions = Start.directions;
		storedField = CommandUnits.storedField;
		enemies = CommandUnits.enemies;
		squares = CommandUnits.squares;
		team = CommandUnits.team;
		terrain = VectorField.terrain;
		added = new boolean[VectorField.width][VectorField.height];
		VecUnit units = gc.startingMap(gc.planet()).getInitial_units();
		int size = (int) units.size();
		centers = new MapLocation[size/2];
		targets = new MapLocation[size/2];
		Unit unit;
		int mycounter = 0;
		int theircounter = 0;
		for(int i = 0; i<size; i++){
			unit = units.get(i);
			if(unit.team() == team){
				centers[mycounter] = unit.location().mapLocation();
				mycounter++;
			}else{
				targets[theircounter] = unit.location().mapLocation();
				theircounter++;
			}
		}
		queue = new ScrimQueue();
		MapLocation test;
		size /=2;
		for(int i = 0; i<size; i++){
			test = targets[i];
			for(int j = 0; j<size; j++){
				while(!isValidSquare(test.getX(), test.getY())){
					if(test == centers[j])break;
					test = test.add(test.directionTo(centers[j]));
				}
				queue.add(test);
			}
		}
	}
	
	public static void runTurn(GameController gc, ArrayList<Unit> availableUnits){
		long time = System.currentTimeMillis();
		ScrimQueue newqueue = new ScrimQueue();
		int x, y;
		int counter = 0;
		Unit close;
		while(queue.size()>0){
			MapLocation curr = queue.poll();
			if(availableUnits.size()>0){
				if(counter>lastNeighbored)addNeighbors(curr);
				try{
					close = gc.senseUnitAtLocation(curr);
					if(close.team() == team && validUnits.contains(close.unitType())){
						int size = availableUnits.size();
						for(int i = 0; i<size; i++){
							if(availableUnits.get(i).equals(close)){
								availableUnits.remove(i);
								break;
							}
						}
					}
				}catch(Exception e){
					x = curr.getX();
					y = curr.getY();
					close = getClosestUnit(x,y,availableUnits);
					Factories.moveToClosestDirection(gc, close, storedField[x][y].getDirection(close.location().mapLocation()));
				}
			}
			newqueue.add(curr);
			counter++;
		}
		queue = newqueue;
		System.out.println("scrim round took "+(System.currentTimeMillis()-time));
	}
	
	private static Unit getClosestUnit(int x, int y, ArrayList<Unit> availableUnits){
		if(storedField[x][y] == null){
			storedField[x][y] = new VectorField();
			storedField[x][y].setTarget(squares[x][y]);
		}
		VectorField vf = storedField[x][y];
		int closestMag = Integer.MAX_VALUE;
		Unit closestUnit = null;
		int mag;
		for(Unit unit: availableUnits){
			mag = vf.getMagnitude(unit.location().mapLocation());
			if(mag<closestMag){
				closestMag = mag;
				closestUnit = unit;
			}
		}
		availableUnits.remove(closestUnit);
		return closestUnit;
	}
	
	private static void addNeighbors(MapLocation ml) {
		MapLocation test;
		for(Direction dir: directions){
			if(dir==Direction.Center)continue;
			test = ml.add(dir);
			if(isValidSquare(test.getX(),test.getY()))queue.add(test);
		}
		lastNeighbored++;
	}
	
	private static boolean isValidSquare(int x, int y) {
		if(x<0 || x>=VectorField.width || y<0 || y>=VectorField.width)return false;
		if(terrain[x][y]!=1)return false;
		if(added[x][y])return false;
		for(MapLocation enemy : targets){
			if(enemy.distanceSquaredTo(squares[x][y])<scrimDist)return false;
		}
		return true;
	}
	
	public static class ScrimQueue extends PriorityQueue<MapLocation>{
		
		public ScrimQueue() {
			super(new ScrimSorter());
		}
		
		@Override
		public boolean add(MapLocation ml){
			if(this.contains(ml))return false;
			added[ml.getX()][ml.getY()] = true;
			return super.add(ml);
		}
	}
	
	public static class ScrimSorter implements Comparator<MapLocation>{

		@Override
		public int compare(MapLocation map1, MapLocation map2) {
			double h1 = heuristic(map1);
			double h2 = heuristic(map2);
			if(h1 < h2)return -1;
			if(h2 < h1)return 1;
			return 0;
		}
		
		public double heuristic(MapLocation ml){
			int distClosestTarget = Integer.MAX_VALUE;
			int distClosestCenter = Integer.MAX_VALUE;
			int test;
			for(MapLocation enemy : targets){
				test = (int)enemy.distanceSquaredTo(ml);
				if(test<distClosestTarget)distClosestTarget = test;
			}
			for(MapLocation center : centers){
				test = (int)center.distanceSquaredTo(ml);
				if(test<distClosestCenter)distClosestCenter = test;
			}
			return distClosestTarget - ((double)distClosestCenter / 4);
		}
		
	}
}
