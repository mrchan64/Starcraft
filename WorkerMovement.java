import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Queue;

import bc.*;

public class WorkerMovement {

	private static Stack trace = new Stack<Square>();
	private static PriorityQueue workList = new PriorityQueue<Square>();
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
	
	public static Square step(){
		Square previous = (Square) workList.poll();
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
		neighbors.add(map[x+1][y]);
		neighbors.add(map[x+1][y+1]);
		neighbors.add(map[x+1][y-1]);
		neighbors.add(map[x-1][y]);
		neighbors.add(map[x-1][y+1]);
		neighbors.add(map[x-1][y-1]);
		neighbors.add(map[x][y+1]);
		neighbors.add(map[x][y-1]);
		return neighbors;

	}
}
