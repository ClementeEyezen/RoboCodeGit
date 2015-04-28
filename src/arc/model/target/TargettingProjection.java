package arc.model.target;

import java.util.ArrayList;

import arc.model.Gaussian;
import arc.model.TimeCapsule;

public class TargettingProjection {

	// Location where an targeting projection would put the robot on wave 
	TargettingType parent;
	Wave w;
	double guess;
	
	public TargettingProjection(double perc_escape, Wave w, TargettingType parent) {
		guess = perc_escape;
		this.w = w;
		this.parent = parent;
	}
	
	// returns a double between 1 and 0 that represents the closeness of the projection
	// called after Wave w expires
	public double test(TimeCapsule tc, boolean hit) {
		
		TimeCapsule.StateVector sv_end = tc.get_last(0);
		TimeCapsule.StateVector sv_start = tc.get_data(w.create_time);
		double actual_percent = w.percent_escape(sv_start.x(), sv_start.y(), sv_end.x(), sv_end.y());
		double perc_delta = Math.abs(guess-actual_percent);
		// theta
		// r = dist from wave
		double real_delta = w.distance(sv_end.x(), sv_end.y()) * perc_delta;
		
		double pos_val = Math.pow( 2*(Gaussian.Phi(18, 0, real_delta)-.5) , 1.8 );
		
		if(hit) {
			// dist (normal inclusion %) -> rating
			// 6  (99%) -> .99
			// 9  (95%) -> .91
			// 18 (70%) -> .50
			// 36 (38%) -> .18
			// 72 (20%) -> .05
			return pos_val;
		}
		else {
			// 6  (99%) -> .01
			// 9  (95%) -> .09
			// 18 (70%) -> .50
			// 36 (38%) -> .82
			// 72 (20%) -> .95
			return 1 - pos_val;
		}
	}
	
	public class Triple<X, Y, T> {
		X x;
		Y y;
		T t;
		public Triple(X one, Y two, T three) {
			x = one;
			y = two;
			t = three;
		}
		
	}
}
