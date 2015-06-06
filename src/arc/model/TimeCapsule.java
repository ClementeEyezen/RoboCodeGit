package arc.model;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import robocode.AdvancedRobot;

public class TimeCapsule implements Update {
	
	RobotModel parent;
	
	long last_time;
	ArrayList<StateVector> data;
	
	// METHOD TODO
	/*
	 * Constructor
	 * last_time()
	 * last() - returns last data point
	 * last(int n) - returns the last n data points
	 * before(long time) - returns the last data point before time
	 * before(long time, int n) - returns the last n data points before time
	 * after(long time) - returns the first data point after time
	 * after(long time, int n) - returns the next n data points after time
	 */
	
	// CONSTRUCTOR
	
	public TimeCapsule(RobotModel parent) {
		// stores historical data about the robot
		this.parent = parent;
		data = new ArrayList<StateVector>();
	}
	
	// UPDATE
	
	public void update(AdvancedRobot self) {
		update(self.getTime(), self.getEnergy(), self.getGunHeading(), self.getGunHeat(), 
				self.getHeadingRadians(), self.getVelocity(), self.getX(), self.getY());
	}
	public void update(long time, double ener, 
			double g_hd, double g_ht, double head, double velo,
			double x, double y) {
		data.add(new StateVector((double)time, ener, g_hd, g_ht, head, velo, x, y));
	}
	@Override
	public void update() {
		// TODO created by interface
		// TODO make useful changes
	}
	
	// ACCESS
	
	
	
	// UTILITIES
	// TODO
//	public long last_time() {
//		return last().time();
//	}
	
	public void onPaint(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		return "Time Capsule for "+parent.name;
	}
	
	
	// INNER CLASS
	
	public class StateVector {
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