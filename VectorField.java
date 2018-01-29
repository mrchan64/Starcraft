import java.util.ArrayList;
import bc.*;

public class VectorField {
	MapLocation[][] squares;
	Direction[][] dirs;
	int[][] magnitude;
	static int[][] terrain;
	boolean[][] checked;
	ArrayList<MapLocation> working = new ArrayList<MapLocation>();
	ArrayList<MapLocation> done;
	static int width;
	static int height;
	static Planet planet;
	
	static boolean largeMap;

	Direction[] directions = Direction.values();
	
	public VectorField(){
		if(largeMap)return;
		squares = new MapLocation[width][height];
		dirs = new Direction[width][height];
		magnitude = new int[width][height];
		checked = new boolean[width][height];
		for(int i = 0; i<width; i++){
			for(int j = 0; j<height; j++){
				squares[i][j] = new MapLocation(planet, i, j);
				dirs[i][j] = Direction.Center;
				magnitude[i][j] = Integer.MAX_VALUE;
			}
		}
	}
	
	public static void initWalls(GameController gc) {
		Planet mine = gc.planet();
		planet = mine;
		PlanetMap pm = gc.startingMap(mine);
		height = (int) pm.getHeight();
		width = (int) pm.getWidth();
		terrain = new int[width][height];
		for(int i = 0; i<width; i++){
			for(int j = 0; j<height; j++){
				terrain[i][j] = (int)pm.isPassableTerrainAt(new MapLocation(mine, i, j));
			}
		}
	}
	
	public static void findMapSize(){
		largeMap = findKarbonite.avaSq>30*30;
	}
	
	public void setTargets(ArrayList<MapLocation> start){
		int l = start.size();
		done = new ArrayList<MapLocation>();
		for(int i = 0; i<l;i++){
			MapLocation ml = start.get(i);
			done.add(ml);
			if(largeMap)continue;
			int x = ml.getX();
			int y = ml.getY();
			magnitude[x][y] = 0;
			checked[x][y] = true;
		}
		if(largeMap)return;
		for(int i = 0; i<l;i++){
			MapLocation ml = done.get(i);
			if(!largeMap)addNeighbors(ml);
		}
	}
	
	public void setTarget(MapLocation start){
		done = new ArrayList<MapLocation>();
		done.add(start);
		if(largeMap)return;
		int x = start.getX();
		int y = start.getY();
		magnitude[x][y] = 0;
		addNeighbors(start);
	}
	
	public Direction getDirection(MapLocation check){
		if(largeMap)return simpleDir(check);
		int x = check.getX();
		int y = check.getY();
		populateTo(x, y);
		return dirs[x][y];
	}
	
	public int getMagnitude(MapLocation check){
		if(largeMap)return simpleMag(check);
		int x = check.getX();
		int y = check.getY();
		populateTo(x, y);
		return magnitude[x][y];
	}
	
	private void populateTo(int x, int y){
		while(magnitude[x][y]==Integer.MAX_VALUE){
			if(!step()) return;
		}
	}
	
	private boolean step(){
		if(working.size()==0)return false;
		MapLocation check = working.remove(0);
		int x = check.getX();
		int y = check.getY();
		int foundmag = Integer.MAX_VALUE;
		MapLocation foundloc = null;
		int anglediff = Integer.MAX_VALUE;
		Direction founddir = null;
		Direction tempdir = null;
		for(int i = -1; i<=1; i++){
			for(int j = -1; j<=1; j++){
				if(i==0 && j==0)continue;
				if(x+i>=width || x+i<0 || y+j>=height || y+j<0)continue;
				if(terrain[x+i][y+j]!=1)continue;
				tempdir = check.directionTo(squares[x+i][y+j]);
				int angle = angleBetween(dirs[x+i][y+j], tempdir);
				if(magnitude[x+i][y+j]<foundmag || (magnitude[x+i][y+j]==foundmag && angle < anglediff)){
					foundmag = magnitude[x+i][y+j];
					foundloc = squares[x+i][y+j];
					anglediff = angle;
					founddir = tempdir;
				}
			}
		}
		if(foundloc==null){
			foundloc = new MapLocation(Planet.Earth, 0, 0);
			founddir = Direction.Center;
		}
		dirs[x][y] = founddir;
		magnitude[x][y] = foundmag+1;
		done.add(check);
		addNeighbors(check);
		return true;
	}
	
	private void addNeighbors(MapLocation loc){
		int x = loc.getX();
		int y = loc.getY();
		checked[x][y]=true;
		for(int i = -1; i<=1; i++){
			for(int j = -1; j<=1; j++){
				if(x+i>=width || x+i<0 || y+j>=height || y+j<0)continue;
				if(terrain[x+i][y+j]!=1)continue;
				if(!checked[x+i][y+j]){
					working.add(squares[x+i][y+j]);
					checked[x+i][y+j] = true;
				}
			}
		}
	}
	
	private int angleBetween(Direction dir1, Direction dir2){
		if(dir1 == Direction.Center || dir2 == Direction.Center)return 0;
		int ind1 = 0;
		int ind2 = 0;
		for(int i = 0; i<8; i++){
			if(directions[i] == dir1)ind1 = i;
			if(directions[i] == dir2)ind2 = i;
		}
		int diff1 = (ind1-ind2+8)%8;
		int diff2 = (ind2-ind1+8)%8;
		if(diff1<diff2)return diff1;
		return diff2;
	}
	
	private Direction simpleDir(MapLocation ml){
		int distance = Integer.MAX_VALUE;
		Direction dir = Direction.Center;
		int dist;
		for(MapLocation target : done){
			dist = (int) ml.distanceSquaredTo(target);
			if(dist<distance){
				distance = dist;
				dir = ml.directionTo(target);
			}
		}
		return dir;
	}
	
	private int simpleMag(MapLocation ml){
		int distance = Integer.MAX_VALUE;
		int dist;
		for(MapLocation target : done){
			dist = (int) ml.distanceSquaredTo(target);
			if(dist<distance){
				distance = dist;
			}
		}
		return distance;
	}
	
	public String toString(){
		String ret = "";
		for(int j = height-1; j>=0; j--){
			String row1 = "";
			String row2 = "";
			String row3 = "";
			for(int i = 0; i<width; i++){
				row1+=magnitude[i][j]+" ";
				switch(dirs[i][j]){
				case North:
					row3+="N  ";
					break;
				case South:
					row3+="S  ";
					row2+="| ";
					break;
				case Northeast:
					row3+="NE ";
					break;
				case Southwest:
					row3+="SW ";
					row2+="/ ";
					break;
				case Northwest:
					row3+="NW ";
					break;
				case Southeast:
					row3+="SE ";
					row2+="\\ ";
					break;
				case West:
					row3+="W  ";
					break;
				case East:
					row3+="E  ";
					row2+="- ";
					break;
				case Center: 
					row2+="o ";
					row3+="C  ";
				}
			}
			//ret+=/*row1+"\n"+*/row2+"\n";
			ret+=row3+"\n";
		}
		return ret;
	}
}
