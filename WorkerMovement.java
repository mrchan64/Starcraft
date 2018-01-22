import java.util.*;

public class WorkerMovement {

	public static Stack<Square> trace = new Stack<Square>();
	private static ArrayList<Square> workList = new ArrayList<Square>();
	static Square[][] map;
	static boolean found = false;
	
	public static void BFS(Square start, Square goal){
		Square last = null;
		workList.add(start);
		while(!found){
			last = step();
			if(last == goal){
				found = true;
			}

		}
		trace.push(last);
		while(last.previous != null){
			trace.push(last.previous);
			last = last.previous;
		}
		
	}


	public static void findKarbonite(Square start) {
		Square last = null;
		workList.add(start);
		while(!found){
			last = step();
			if(last.getType() == 2){
				found = true;
			}
		}
		trace.push(last);
		while(last.previous != null){
			trace.push(last.previous);
			last = last.previous;
		}
		found = false;
	}
	
	public static Square step(){
		Square previous = (Square) workList.remove(0);
		ArrayList<Square> neighbors = neighbors(previous);
		Square neighbor;
		previous.setVisited();
		for(int i = 0; i < neighbors.size(); i++){
			neighbor = neighbors.get(i);
			if(neighbor.type != 3 && !neighbor.isVisited()){
				neighbor.setPrevious(previous);
				workList.add(neighbor);
			}
		}
		return previous;
		
		
		
	}
	
	public static ArrayList<Square> neighbors(Square square){
		ArrayList<Square> neighbors = new ArrayList<Square>();
		int x = square.x;
		int y = square.y;

		int height = Pathfinding.eHeight;
		int width = Pathfinding.eWidth;
		
		if (x+1 < width) {
			neighbors.add(Pathfinding.earthMap[y][x+1]);
		}
		if (x+1 < width && y+1 < height) {
			neighbors.add(Pathfinding.earthMap[y+1][x+1]);
		}
		if (x+1 < width && y-1 >= 0) {
			neighbors.add(Pathfinding.earthMap[y-1][x+1]);
		}
		if (x-1 >= 0 && y-1 >= 0) {
			neighbors.add(Pathfinding.earthMap[y-1][x-1]);
		}
		if (x-1 >= 0) {
			neighbors.add(Pathfinding.earthMap[y][x-1]);
		}
		if (x-1 >= 0 && y+1 < height) {
			neighbors.add(Pathfinding.earthMap[y+1][x-1]);
		}
		
		if (y+1 < height) {
			neighbors.add(Pathfinding.earthMap[y+1][x]);
		}
		if (y-1 >= 0) {
			neighbors.add(Pathfinding.earthMap[y-1][x]);
		}
		return neighbors;
	}

	
}
