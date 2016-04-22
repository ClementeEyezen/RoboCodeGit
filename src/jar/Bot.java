package jar;

import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

public class Bot {
	// model all robots uniformly
	String name;
	Driver d;
	Raddar r;
	Gunner g;
	
	public Bot(String name) {
		this.name = name;
		d = new Driver(this);
		r = new Raddar(this);
		g = new Gunner(this);
	}
	
	public Bot(AdvancedRobot self) {
		// based on my robot
	}
	
	public Bot(AdvancedRobot self, ScannedRobotEvent sre) {
		// other robot
	}
	
	public void update(AdvancedRobot self) {
		// update self
		if (!self.getName().equals(name)) {
			// don't update
			return;
		}
	}
	
	/* 
	 * Evented Update
	 */
	// scan
	public void update(AdvancedRobot self, ScannedRobotEvent sre) {
		// update for another robot
		if (!self.getName().equals(name)) {
			// don't update
			return;
		}
	}
	// hbbe
	public void update(AdvancedRobot self, HitByBulletEvent hbbe) {
		
	}

	public void update(HitByBulletEvent other) {
		// TODO Auto-generated method stub
		
	}

	public void update(Vis self, BulletHitEvent bhe) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Get info out
	 */
	public double lastX() {
		// TODO
		return 0.0;
	}
	public double lastY() {
		// TODO
		return 0.0;
	}
	public double lastHeading() {
		// TODO
		return 0.0;
	}

	public void update(Vis self, BulletHitBulletEvent bhbe) {
		// TODO Auto-generated method stub
		
	}

	public void update(Vis self, HitRobotEvent hre) {
		// TODO Auto-generated method stub
		
	}
}
