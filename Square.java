
public class Square {

	Square previous;
	int x;
	int y;
	boolean visited;
	boolean onFinalPath;
	boolean workList;
	int type;
	public Square(int x, int y, int type){
		this.x = x;
		this.y = y;
		visited = false;
		previous = null;
		this.type = type;
			
	}
	public boolean isVisited(){
		if(visited){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Sets visited to true
	 */
	public void setVisited(){
		visited = true;
	}
	
	/**
	 * Set visited to false
	 */
	public void clearVisited(){
		visited = false;
		previous = null;
		onFinalPath = false;
		workList= false;
	}
	
	/**
	 * Gets previous square
	 * @return Square the previous square
	 */
	public Square getPrevious(){
		return previous;
	}
	
	/**
	 * Sets previous
	 * @param square the square's previous
	 */
	public void setPrevious(Square square){
		previous = square;
	}
}
