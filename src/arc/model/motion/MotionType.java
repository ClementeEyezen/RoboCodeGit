package arc.model.motion;

import java.util.ArrayList;

import arc.model.TimeCapsule;

public abstract class MotionType {
	// project the motion of the robot forward using this motion type
	// keeps track of projections and rating for the MotionType
	ArrayList<MotionProjection> past_projections;
	double running_rating;
	public void update(TimeCapsule tc) {
		past_projections.add(project(tc,tc.current_time(),20));
	}
	public abstract MotionProjection project(TimeCapsule tc, long start_time, long time_forward);
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
}