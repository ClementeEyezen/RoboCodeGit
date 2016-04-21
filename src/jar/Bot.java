package jar;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class Bot {
	// model all robots uniformly
	String name;
	public Bot(String name) {
		this.name = name;
	}
	
	public void update(AdvancedRobot self) {
		// update self
		if (!self.getName().equals(name)) {
			// don't update
			return;
		}
	}
	public void update(AdvancedRobot self, ScannedRobotEvent sre) {
		// update for another robot
		if (!self.getName().equals(name)) {
			// don't update
			return;
		}
	}
}
