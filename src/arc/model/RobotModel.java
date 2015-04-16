package arc.model;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

// TODO move the inner classes out, they are public anyway

public class RobotModel {
	
	// Robot Parameters
	AdvancedRobot parent;
	TimeCapsule tc;
	MotionModel mm;
	TargettingModel tm;
	
	double height, width;
	double gun_cooling_rate;
	
	// Robot state
	double energy;
	double gun_heading, gun_heat;
	double radar_heading;
	double heading, velocity;
	double x, y;
	String name;
	
	public RobotModel(AdvancedRobot ar) {
		// Robot model for self
		this(ar.getName(), 
				ar.getHeight(), ar.getWidth(), ar.getEnergy(), 
				ar.getGunCoolingRate(),ar.getGunHeadingRadians(), ar.getGunHeat(),
				ar.getRadarHeadingRadians(),
				ar.getHeadingRadians(), ar.getVelocity(), 
				ar.getX(), ar.getY());
		parent = ar;
	}
	public RobotModel(String name, 
			double heig, double widt, double ener, 
			double g_co, double g_hd, double g_ht, 
			double r_he, 
			double head, double velo,
			double x, double y) {
		this.name = name;
		height = heig;
		width = widt;
		energy = ener;
		gun_cooling_rate = g_co;
		gun_heading = g_hd;
		gun_heat = g_ht;
		radar_heading = r_he;
		heading = head;
		velocity = velo;
		this.x = x;
		this.y = y;
		parent = null;
		this.tc = new TimeCapsule(this);
		this.mm = new MotionModel(this);
		this.tm = new TargettingModel(this);
	}
	
	public void update() {
		tc.update(parent);
	}
	public void update(ScannedRobotEvent sre) {
		if(sre.getName().equals(name)) {
			tc.update(sre.getTime(), sre.getEnergy(), 
					tm.predict_gun_heading(sre, tc), tm.predict_gun_heat(sre, tc), 
					sre.getHeading(), sre.getVelocity(),
					getX(sre), getY(sre));
		}
	}
	public void update(HitByBulletEvent hbbe) {
		tm.test(hbbe, tc);
	}
	
	public double getX(ScannedRobotEvent sre) {
		long time = sre.getTime();
		double frame_x = tc.get_data(time).x();
		double bearing = sre.getBearingRadians();
		double distance = sre.getDistance();
		return frame_x + distance * Math.cos(bearing);
	}
	public double getY(ScannedRobotEvent sre) {
		long time = sre.getTime();
		double frame_x = tc.get_data(time).x();
		double bearing = sre.getBearingRadians();
		double distance = sre.getDistance();
		return frame_x + distance * Math.cos(bearing);
	}
}
