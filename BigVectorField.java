import bc.*;

public class BigVectorField extends VectorField {
	public static MapLocation targetLocation;

	public BigVectorField() {

	}

	@Override
	public void setTarget(MapLocation start){
		targetLocation = start;	
	}

	@Override
	public Direction getDirection(MapLocation check){
		return check.directionTo(targetLocation);
	}
	
	@Override
	public int getMagnitude(MapLocation check){
		return (int) check.distanceSquaredTo(targetLocation);
	}
}