import bc.*;
import java.util.*;

public class Rocket {
	ArrayList flightRounds;

	public static boolean canBuildRocket(GameController gc) {
		round = gc.round();
		if (round == 500) {
			return true;
		}
		return false;
	}

	public static void launchRocket(int round) {

	}

	public static void calcOptLaunchRounds() {
		OrbitPattern op = new OrbitPattern();
		flightRounds = new ArrayList();
		for (int i = 500; i < 750; i++) {
			if (op.duration(i) >= 50 && op.duration <= 100) {
				flightRounds.add(i);
			}
		}
	}

}