package arc.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import arc.model.motion.MotionModel;
import arc.model.target.TargettingModel;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;


public class RobotModel {
	
	// Robot Parameters
	AdvancedRobot parent;
	TimeCapsule tc;
	MotionModel mm;
	//TargettingModel tm;
	
	Color c;
	
	double height, width;
	public double gun_cooling_rate;
	
	// Robot state
	double energy;
	double gun_heading;
	public double gun_heat;
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
		c = Color.GREEN;
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
		tc.update(0, ener, g_hd, g_ht, head, velo, x, y);
		this.mm = new MotionModel(this);
		//this.tm = new TargettingModel(this);
		Random r = new Random();
		c = new Color((name.hashCode()+r.nextInt(16777215))%(16777215));
	}
	
	public void update() {
		mm.update();
		tc.update(parent);
		x = parent.getX();
		y = parent.getY();
	}
	public void update(ScannedRobotEvent sre, double self_h, double self_x, double self_y) {
		if(sre.getName().equals(name)) {
			//System.out.println("Scanned "+name+" at "+getX(sre, self_h, self_x)+" , "+getY(sre,self_h, self_y));
			tc.update(sre.getTime(), sre.getEnergy(), 
					0.0, 0.0, //tm.predict_gun_heading(sre, tc), tm.predict_gun_heat(sre, tc), 
					correct_angle(sre.getHeadingRadians()), sre.getVelocity(),
					getX(sre,self_h, self_x), getY(sre, self_h, self_y));
			this.x = getX(sre, self_h, self_x);
			this.y = getY(sre, self_h, self_y);
		}
		else {
			System.out.println("ERR: Robot Model for "+name+" recieved data for "+sre.getName());
		}
	}
	public void update(HitByBulletEvent hbbe) {
		//tm.test(hbbe, tc);
	}
	
	public double correct_angle(double head_or_bear) {
		//return head_or_bear;
		return -1*head_or_bear + Math.PI/2;
	}
	
	public double getX(ScannedRobotEvent sre, double self_h, double self_x) {
		double frame_x = self_x;
		double bearing = correct_angle(self_h+sre.getBearingRadians());
		double distance = sre.getDistance();
		//System.out.println("get x -> fx: "+frame_x+" r: "+distance+" @ theta "+bearing);
		//System.out.println("         := deg = "+bearing*180/Math.PI);
		//System.out.println("         -> x = "+(frame_x + distance * Math.cos(bearing)));
		return frame_x + distance * Math.cos(bearing);
	}
	public double getY(ScannedRobotEvent sre, double self_h, double self_y) {
		double frame_y = self_y;
		double bearing = correct_angle(self_h+sre.getBearingRadians());
		double distance = sre.getDistance();
		//System.out.println("get y -> fy: "+frame_y+" r: "+distance+" @ theta "+bearing);
		//System.out.println("         := deg = "+bearing*180/Math.PI);
		//System.out.println("         -> y = "+(frame_y + distance * Math.sin(bearing)));
		return frame_y + distance * Math.sin(bearing);
	}
	public TimeCapsule current_history() {
		return tc;
	}
	public String name() {
		return name;
	}
	
	public void onPaint(Graphics2D g) {
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
	}
}
