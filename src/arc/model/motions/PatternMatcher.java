package arc.model.motions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class PatternMatcher extends MotionType {

	PatternSpace ps;
	int len;
	public PatternMatcher(int pattern_length) {
		ps = new PatternSpace();
		len = pattern_length;
	}
	
	@Override
	public MotionProjection project(TimeCapsule tc, long start_time,
			long time_forward) {
		TimeCapsule.StateVector[] input_pattern = new TimeCapsule.StateVector[len];
		for(int i = len-1; i >= 0; i--) {
			input_pattern[len-1-i]=tc.get_last(i);
		}
		return ps.search(input_pattern, (int)time_forward);
	}

}

class PatternSpace {
	long last_time;
	
	ArrayList<TimeCapsule.StateVector> history;
	
	public PatternSpace() {
		last_time = 0;
		history = new ArrayList<TimeCapsule.StateVector>();
	}
	
	public void update(TimeCapsule tc) {
		if(tc.current_time() > last_time) {
			int i = 0;
			TimeCapsule.StateVector sv = tc.get_last(i);
			while(sv.time() > last_time) {
				update(sv);
				i++;
				sv = tc.get_last(i);
			}
			last_time = tc.current_time();
		}
	}
	private void update(TimeCapsule.StateVector sv) {
		
		return;
	}
	public MotionProjection search(TimeCapsule.StateVector[] pattern, int time_forward) {
		// overall method to search/return the closest pattern
		// 7x linked list traversal
		// circular buffer of 7 distances. each distance += error for the next step from pattern
		// min distance stored, along with end of pattern. At the end of each pattern/7 step, it checks distance.
		//   if it is less than min, it puts itself as the 
		

		if(history.size() <= pattern.length+time_forward) {
			return null;
		}
		
		int zero = 0;
		double min_distance = Double.POSITIVE_INFINITY;
		int best_match_index = 0;
		double[] distance = new double[pattern.length];
		for(int i = 0; i < history.size() - time_forward - 1; i++ ) {
			if(i >= pattern.length) {
				zero = (zero + 1)%pattern.length;
			}
			for(int j = 0 ; j < pattern.length; j++) {
				// TODO start here w/ rotating distance calulation
				distance[j] += distance(pattern[pattern.length-1-j], history.get(i));
			}
		}
		else {
			return create_Projection(TimeCapsule current, best_match_index);
		}
	}
	public double distance(TimeCapsule.StateVector one, TimeCapsule.StateVector two) {
		// normalized distance between state vectors
		return 1.0;
	}
}