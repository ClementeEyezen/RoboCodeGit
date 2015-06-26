package arc.model;

import java.awt.Graphics2D;
import java.util.ArrayList;

import robocode.AdvancedRobot;

public class TimeCapsule implements Update {
	
	RobotModel parent;
	
	long last_time;
	ArrayList<StateVector> data;
	
	/*
	 * Constructor	
	 * last_time()
	 * last() - returns last data point
	 * last(int n) - returns the last n data points
	 * before(long time) - returns the last data point before time
	 * before(long time, int n) - returns the last n data points before time
	 * after(long time) - returns the first data point after time
	 * after(long time, int n) - returns the next n data points after time
	// METHOD TODO
	 */
	
	// CONSTRUCTOR
	
	public TimeCapsule(RobotModel parent) {
		// stores historical data about the robot
		this.parent = parent;
		data = new ArrayList<StateVector>();
	}
	
	// MAIN METHOD / for test
	
	public static void main(String[] args) {
		ArrayList<Long> datar = new ArrayList<Long>();
		int start = 1;
		int end = 9;
		for(long i = start; i <= end; i+=3) {
			datar.add(i);
		}
		System.out.println("datar: "+datar);
		for(int i = start-2; i <= end+2; i++) {
			int before = search_test(datar, i, true, 0, datar.size());
			int after = search_test(datar, i, false, 0, datar.size());
			System.out.println(i+" before: "+((before >= 0) ? datar.get(before) : -1)+" after: "+((after >= 0) ? datar.get(after) : -1));
		}
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
	
	public ArrayList<StateVector> last() {
		return last(1);
	}
	
	public ArrayList<StateVector> last(int n) {
		// ordered earliest to latest
		n = (n <= 0) ? 1 : n;
		ArrayList<StateVector> toReturn = new ArrayList<StateVector>();
		for(int i =0 ; i < n; i++) {
			if(data.size()-1-i >= 0) { // in valid range
				toReturn.add(0, data.get(data.size()-1-i));
			}
			else {
				break;
			}
		}
		return toReturn;
	}
	
	public ArrayList<StateVector> at(long time) {
		if((long) before(time).get(0).time() == time)
			return before(time);
		else 
			return new ArrayList<StateVector>();
	}
	
	public ArrayList<StateVector> before(long time) {
		return before(time, 1);
	}
	
	public ArrayList<StateVector> before(long time, int n) {
		int index = search(time, true);
		// location of last element before/including the given time
		ArrayList<StateVector> toReturn = new ArrayList<StateVector>();
		for(int i = 0; i < n; i++) {
			if(index - i >= 0) {
				toReturn.add(0, data.get(index - i));
			}
			else {
				break;
			}
		}
		return toReturn;
	}
	
	public ArrayList<StateVector> after(long time) {
		return after(time, 1);
	}
	
	public ArrayList<StateVector> after(long time, int n) {
		int index = search(time, false);
		// location of last element before/including the given time
		ArrayList<StateVector> toReturn = new ArrayList<StateVector>();
		for(int i = 0; i < n; i++) {
			if(index + i < data.size()) {
				toReturn.add(data.get(index + i));
			}
			else {
				break;
			}
		}
		return toReturn;
	}
	
	// UTILITIES
	
	private int search(long time, boolean before) {
		// performs binary search to find the element at time.
		// 	if there is not an element at the time, it returns the index
		// 	before (or after if bool is false)
		return search_help(time, before, 0, data.size());
	}
	
	private int search_help(long goal, boolean before, int start, int end) {
		
		if(start >= data.size() || end <= 0) {
			//System.out.println("out of range. s: "+start+" e: "+end);
			if(start == data.size() && before)
				return start-1;
			else if(end == 0 && !before) {
				return end;
			}
			return -1;
		}
		
		long test_time = (long) data.get((start+end)/2).time();
		// termination conditions
		if(end - start <= 0) {
			if (before) {
				//System.out.println("terminate before. s: "+start+" e: "+end);
				if (start-1 >= 0)
					return start-1;
				else
					return -1;
			}
			else {
				//System.out.println("terminate after. s: "+start+" e: "+end);
				if (end < data.size())
					return end;
				else 
					return -1;
			}
		}
		if (test_time == goal) {
			//System.out.println("terminate match. s: "+start+" e: "+end);
			return (start+end)/2;
		}
		// continuation conditions
		else if (test_time > goal) {
			return search_help(goal, before, start, (start+end)/2);
		}
		else {
			return search_help(goal, before, (start+end)/2+1, end);
		}
	}
	
	private static int search_test(ArrayList<Long> data, long goal, boolean before, int start, int end) {
		// termination conditions
		
		if(start >= data.size() || end <= 0) {
			//System.out.println("out of range. s: "+start+" e: "+end);
			if(start == data.size() && before)
				return start-1;
			else if(end == 0 && !before) {
				return end;
			}
			return -1;
		}
		
		long test_time = data.get((start+end)/2);
		
		if(end - start <= 0) {
			if (before) {
				//System.out.println("terminate before. s: "+start+" e: "+end);
				if (start-1 >= 0)
					return start-1;
				else
					return -1;
			}
			else {
				//System.out.println("terminate after. s: "+start+" e: "+end);
				if (end < data.size())
					return end;
				else 
					return -1;
			}
		}
		if (test_time == goal) {
			//System.out.println("terminate match. s: "+start+" e: "+end);
			return (start+end)/2;
		}
		// continuation conditions
		else if (test_time > goal) {
			return search_test(data, goal, before, start, (start+end)/2);
		}
		else {
			return search_test(data, goal, before, (start+end)/2+1, end);
		}
	}
	
	/* TIME CAPSULE QUICK ACCESS */
	
	public long last_time() {
		ArrayList<StateVector> l = last();
		return (l.size() > 0) ? 
					((long) l.get(0).time()) : 
					((long) 0);
	}
	
	public long last_x() {
		ArrayList<StateVector> l = last();
		return (l.size() > 0) ? 
					((long) l.get(0).x()) : 
					((long) 0);
	}
	
	public long last_y() {
		ArrayList<StateVector> l = last();
		return (l.size() > 0) ? 
					((long) l.get(0).y()) : 
					((long) 0);
	}
	
	public long last_heading() {
		ArrayList<StateVector> l = last();
		return (l.size() > 0) ? 
					((long) l.get(0).heading()) : 
					((long) 0);
	}
	
	/* VISUALS */
	
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