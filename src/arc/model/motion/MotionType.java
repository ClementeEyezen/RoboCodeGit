package arc.model.motion;

import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.Update;

public abstract class MotionType implements Update {
	// project the motion of the robot forward using this motion type
	// keeps track of projections and rating for the MotionType
	
	// TODO revise heavily
	
	ArrayList<MotionProjection> past_projections;
	double running_rating;
	public void update() {
		
	}
	public void update(ScannedRobotEvent sre) {
		// TODO write update with known new data
	}
	public void update(AdvancedRobot ar) {
		// TODO write update with known new data
	}
	public void update(TimeCapsule tc) {
		// TODO deprecate
		try {
			past_projections.add(project(tc,20));
		}
		catch (NullPointerException npe) {
			
		}
	}
	public abstract MotionProjection project(TimeCapsule tc, long time_forward);
	
	public final double update_rating(TimeCapsule tc) {
		double new_value = past_projections.remove(0).test(tc);
		if(running_rating == 0) {
			running_rating = new_value;
		}
		else {
			running_rating = (running_rating*20+new_value)/(20+1);
		}
		return running_rating;
	}
	
	protected final double correct_angle(double head_or_bear) {
		//return head_or_bear;
		return -1*head_or_bear + Math.PI/2;
	}
}