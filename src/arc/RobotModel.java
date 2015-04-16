package arc;

import java.util.HashMap;

import arc.RobotModel.TimeCapsule.StateVector;
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
	
	public class TimeCapsule {
		RobotModel parent;
		HashMap<Long, StateVector> data;
		public TimeCapsule(RobotModel parent) {
			// stores historical data about the robot
			this.parent = parent;
			data = new HashMap<Long, StateVector>();
		}
		
		public StateVector get_data(long time) {
			return data.get(new Long(time));
		}
		
		public void update(AdvancedRobot self) {
			update(self.getTime(), self.getEnergy(), self.getGunHeading(), self.getGunHeat(), 
					self.getHeading(), self.getVelocity(), self.getX(), self.getY());
		}
		public void update(long time, double ener, 
				double g_hd, double g_ht, double head, double velo,
				double x, double y) {
			data.put(new Long(time), 
					new StateVector((double)time, ener, g_hd, g_ht, head, velo, x, y)
			);
		}
		
		class StateVector {
			double[] state_vec;
			public StateVector() {
				this(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
			}
			public StateVector(double time, double ener, double g_hd, double g_ht, double head, double velo,
					double x, double y) {
				state_vec = new double[8];
				set_time(time);
				set_energy(ener);
				set_gun_heading(g_hd);
				set_gun_heat(g_ht);
				set_heading(head);
				set_velocity(velo);
				set_x(x);
				set_y(y);
			}
			public double time() {
				return state_vec[0];
			}
			public void set_time(double t) {
				state_vec[0] = t;
			}
			public double energy() {
				return state_vec[1];
			}
			public void set_energy(double e) {
				state_vec[1] = e;
			}
			public double gun_heading() {
				return state_vec[2];
			}
			public void set_gun_heading(double h) {
				state_vec[2] = h;
			}
			public double gun_heat() {
				return state_vec[3];
			}
			public void set_gun_heat(double h) {
				state_vec[3] = h;
			}
			public double heading() {
				return state_vec[4];
			}
			public void set_heading(double h) {
				state_vec[4] = h;
			}
			public double velocity() {
				return state_vec[5];
			}
			public void set_velocity(double v) {
				state_vec[5] = v;
			}
			public double x() {
				return state_vec[6];
			}
			public void set_x(double x) {
				state_vec[6] = x;
			}
			public double y() {
				return state_vec[7];
			}
			public void set_y(double y) {
				state_vec[7] = y;
			}
		}
	}
	public class MotionModel {
		
		private MotionType most_likely;
		
		public MotionModel(RobotModel parent) {
			// TODO constructor
			// models/calculates the motions of the parent robot
			// two functions:
			// 		provide domain knowledge about how the robot could move
			// 		provide maximum likelihood fitting to a Motion Type
		}
		public void test(ScannedRobotEvent sre, TimeCapsule history) {
			
			// Test models for fit
			// TODO
			/*
			  sre.getBearingRadians() 
	          sre.getDistance() 
	          sre.getEnergy() 
	          sre.getHeadingRadians() 
	          sre.getName() 
	          sre.getVelocity() 
	          sre.getTime()
			 */
		}
		public void test(AdvancedRobot ar, TimeCapsule history) {
			// TODO test in the case of looking at self
		}
		
		public double max_velocity(TimeCapsule.StateVector state) {
			// TODO implement
		}
		public double min_velocity(TimeCapsule.StateVector state) {
			// TODO implement
		}
		
		abstract class MotionType {
			// TODO implement
		} 
		class StandStill extends MotionType {
			// TODO implement example
		}
		
	}
	public class TargettingModel {
		
		private TargettingType most_likely;
		
		public TargettingModel(RobotModel parent) {
			// TODO constructor
			// models/calculates the targetting of the parent robot
			// two functions:
			// 		provide domain knowledge about how the robot could target/shoot
			// 		provide maximum likelihood fitting to a Targetting Type
		}
		public void test(HitByBulletEvent current, TimeCapsule history) {
			// Test models for fit
			// TODO
			/*
				current.getBearingRadians(); // bearing from robot to bullet
				current.getBullet(); // bullet that hit robot
				current.getBullet().getHeadingRadians(); // heading of bullet
				current.getBullet().getName(); // name of robot that fired
				current.getBullet().getPower(); // power of bullet
				current.getBullet().getVelocity(); // velocity of bullet
				current.getBullet().getVictim(); // name of victim of bullet
				current.getBullet().getX(); // x of bullet
				current.getBullet().getY(); // y of bullet
				current.getHeadingRadians(); // heading of bullet
				current.getName(); // name of robot that fired bullet
				current.getPower(); // power of bullet 
				current.getTime(); // time of event
				current.getVelocity(); // velocity of the bullet
			*/
		}
		public void test(AdvancedRobot ar, TimeCapsule history) {
			// TODO test in the case of self
		}
		
		public double predict_gun_heading(ScannedRobotEvent current, TimeCapsule history) {
			// TODO
			// based on past tests, info where is the gun pointing?
			return 0.0;
		}
		public double predict_gun_heat(ScannedRobotEvent current, TimeCapsule history) {
			// TODO
			// based on past tests, info what is the gun heat?
			return 0.0;
		}
		
		abstract class TargettingType {
			// TODO implement
		} 
		class HeadOn extends TargettingType {
			// TODO implement example
		}
	}
}
