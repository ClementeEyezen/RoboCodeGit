package arc.model.target;

import java.util.ArrayList;
import java.util.List;

import arc.model.RobotModel;
import arc.model.TimeCapsule;
import arc.model.motion.MotionType;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

public class TargettingModel {
	
	private TargettingType most_likely;
	private List<TargettingType> models;
	private RobotModel parent;
	
	public TargettingModel(RobotModel parent) {
		// TODO constructor
		// models/calculates the targetting of the parent robot
		// two functions:
		// 		provide domain knowledge about how the robot could target/shoot
		// 		provide maximum likelihood fitting to a Targetting Type
		this.parent = parent;
		models = new ArrayList<TargettingType>();
		models.add(new HeadOn());
		most_likely = models.get(0);
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
	public void test(TimeCapsule history) {
		// TODO
		// called if HBBE event didn't happen
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
	
	class HeadOn extends TargettingType {

		@Override
		public TargettingProjection project(TimeCapsule tc, Wave w) {
			return new TargettingProjection(0.0, w, this);
		}
	}
}
