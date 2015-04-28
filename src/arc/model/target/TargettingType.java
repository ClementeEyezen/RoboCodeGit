package arc.model.target;

import java.util.ArrayList;

import robocode.HitByBulletEvent;
import arc.model.TimeCapsule;
import arc.model.target.TargettingProjection;

public abstract class TargettingType {

	ArrayList<TargettingProjection> past_projections;
	double running_rating;
	
	public void update(TimeCapsule tc, double self_x, double self_y) {
		// if detect shot
		TimeCapsule.StateVector sv1 = tc.get_last(0);
		TimeCapsule.StateVector sv2 = tc.get_last(1);
		
		boolean detected_shot = sv1.energy() < sv2.energy(); // TODO make smarter
		if(detected_shot) {
			
			Wave w = new Wave(sv1.x(), sv1.y(), (long) sv1.time(), sv2.energy()-sv1.energy(), 
					self_x, self_y);
			past_projections.add(project(tc,w));
		}
	}
	
	public abstract TargettingProjection project(TimeCapsule tc, Wave w);
	
	public final double update_rating(TimeCapsule tc, HitByBulletEvent hbbe) {
		double new_value = past_projections.remove(0).test(tc, true);
		if(running_rating == 0) {
			running_rating = new_value;
		}
		else {
			running_rating = (running_rating*20+new_value)/(20+1);
		}
		return running_rating;
	}
	
	public final double update_rating(TimeCapsule tc) {
		double new_value = past_projections.remove(0).test(tc, false);
		if(running_rating == 0) {
			running_rating = new_value;
		}
		else {
			running_rating = (running_rating*30+new_value)/(30+1);
		}
		return running_rating;
	}
	protected final double correct_angle(double head_or_bear) {
		//return head_or_bear;
		return -1*head_or_bear + Math.PI/2;
	}
}