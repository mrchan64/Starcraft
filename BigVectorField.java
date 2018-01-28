import bc.*;

public class BigVectorField extends VectorField {
	public static MapLocation targetLocation;

	@Override
	public BigVectorField() {

	}

	@Override
	public void setTarget(MapLocation start){
		targetLocation = start;	
	}

	@Override
	public Direction getDirection(MapLocation check){
		return check.directionTo(check);
	}
	
	@Override
	public int getMagnitude(MapLocation check){
		return (int) check.distanceSquaredTo(check);
	}
}