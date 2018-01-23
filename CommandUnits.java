import bc.*;
import java.util.ArrayList;

public class CommandUnits {
	
	static ArrayList<UnitType> validUnits = new ArrayList<UnitType>();
	
	public static void initCommand(){
		validUnits.add(UnitType.Ranger);
		validUnits.add(UnitType.Knight);
		validUnits.add(UnitType.Mage);
	}

	public static void runTurn(GameController gc){
		VecUnit units = gc.myUnits();
		ArrayList<Unit> availableUnits = new ArrayList<Unit>();
		int size = (int) units.size();
		
		Unit unit;
		UnitType unitType;
		for(int i = 0; i<size; i++){
			unit = units.get(i);
			unitType = unit.unitType();
			if(validUnits.contains(unitType)){
				availableUnits.add(unit);
			}
		}
	}
}
