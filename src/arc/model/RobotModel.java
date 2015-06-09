package arc.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import arc.model.motion.MotionModel;
import arc.model.target.TargettingModel;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;


public class RobotModel implements arc.model.Update{
	
	// Robot Parameters
	AdvancedRobot parent;
	TimeCapsule tc;
	MotionModel mm;
	TargettingModel tm;
	
	Color c;
	
	double height, width;
	public double gun_cooling_rate;
	
	// Robot state
	String name;
	
	/*
	public RobotModel(AdvancedRobot ar) {
		// Robot model for self
		this(ar.getName(), 
				ar.getHeight(), ar.getWidth(), ar.getEnergy(), 
				ar.getGunCoolingRate(),ar.getGunHeadingRadians(), ar.getGunHeat(),
				ar.getRadarHeadingRadians(),
				ar.getHeadingRadians(), ar.getVelocity(), 
				ar.getX(), ar.getY());
		parent = ar;
		c = Color.GREEN;
	}
	*/
	private RobotModel(String name, 
			double heig, double widt, double ener, 
			double g_co, double g_hd, double g_ht, 
			double r_he, // radar heading
			double head, double velo,
			double x, double y) {
		
		// Some unchanging features of the robot
		this.name = name;
		height = heig;
		width = widt;
		gun_cooling_rate = g_co;
		parent = null;
		
		// Initialize TimeCapsule and start tracking state
		this.tc = new TimeCapsule(this);
		tc.update(0, ener, g_hd, g_ht, head, velo, x, y);
		
		// Set up auxilary features
		this.mm = new MotionModel(this);
		this.tm = new TargettingModel(this);
		
		// Generate random color for paint purposes
		Random r = new Random();
		c = new Color((name.hashCode()+r.nextInt(16777215))%(16777215));
	}
	public RobotModel(ScannedRobotEvent fs, AdvancedRobot parent) { // firstScan
		this(fs.getName(), 
				parent.getHeight(), parent.getWidth(), 
				fs.getEnergy(), 
				// TODO update parent. if a more intelligent method is found
				parent.getGunCoolingRate(),parent.getGunHeadingRadians(), parent.getGunHeat(),
				parent.getRadarHeadingRadians(),
				fs.getHeadingRadians(), fs.getVelocity(), 
				// adjusted locations
				getX(fs, parent.getHeadingRadians(), parent.getX()), 
				getY(fs, parent.getHeadingRadians(), parent.getY()));
		this.parent = parent;
	}
	
	public void update() {
		tc.update();
		mm.update();
		tm.update();
	}
	public void update(ScannedRobotEvent sre, double self_h, double self_x, double self_y) {
		if(sre.getName().equals(name)) {
			tc.update(sre.getTime(), sre.getEnergy(), 
					tm.predict_gun_heading(sre, tc), tm.predict_gun_heat(sre, tc), 
					correct_angle(sre.getHeadingRadians()), sre.getVelocity(),
					getX(sre,self_h, self_x), getY(sre, self_h, self_y));
		}
	}
	public void update(HitByBulletEvent hbbe) {
		//TODO use this to retcon in aid of the TargettingModel's predictions
	}
	
	
	// UTILITY FUNCTIONS
	// candidates for private, TODO later
	
	public static double correct_angle(double head_or_bear) {
		//return head_or_bear;
		return -1*head_or_bear + Math.PI/2;
	}
	
	public static double getX(ScannedRobotEvent sre, double self_h, double self_x) { 
		// returns the X position of a scanned robot
		double frame_x = self_x;
		double bearing = correct_angle(self_h+sre.getBearingRadians());
		double distance = sre.getDistance();
		return frame_x + distance * Math.cos(bearing);
	}
	public static double getY(ScannedRobotEvent sre, double self_h, double self_y) {
		// returns the Y position of a scanned robot
		double frame_y = self_y;
		double bearing = correct_angle(self_h+sre.getBearingRadians());
		double distance = sre.getDistance();
		return frame_y + distance * Math.sin(bearing);
	}
	
	// GETTERS and SETTERS
	
	public TimeCapsule.StateVector state() {
		try {
			return current_history().last().get(0);
		}
		catch (IndexOutOfBoundsException ioobe) {
			return null;
		}
		
	}
	
	public TimeCapsule current_history() {
		return tc;
	}
	public String name() {
		return name;
	}
	
	// PAINT METHOD
	
	public void onPaint(Graphics2D g) {
		System.out.println("RobotModel.onPaint(g) called. Change in progress");
		/* TODO change
		g.setColor(this.c);
		try {
			g.drawRect((int) (x-this.width/2), (int) (y-this.height/2),
					(int) this.width, (int) this.height);
		}
		catch (NullPointerException npe) {
			System.out.println("Robot "+name+"painted incorrectly");
		}
		System.out.println("Motion Model Painted for Robot Model "+this);
		mm.onPaint(g);
		tc.onPaint(g);
		//tm.onPaint(g);
		*/
	}
}
