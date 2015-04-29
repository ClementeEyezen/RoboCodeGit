package arc.model.motions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import arc.model.TimeCapsule;
import arc.model.TimeCapsule.StateVector;
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
	public MotionProjection project(TimeCapsule tc, long start_time, long time_forward) {
		ps.update(tc);
		TimeCapsule.StateVector[] input_pattern = new TimeCapsule.StateVector[len];
		for(int i = len-1; i >= 0; i--) {
			System.out.println("tc.get_last("+i+") -> "+tc.get_last(i).toString());
			input_pattern[len-1-i]=tc.get_last(i);
		}
		System.out.println("Input pattern: "+Arrays.deepToString(input_pattern));
		System.out.println("Pattern matcher...PROJECT!!!");
		return ps.search(input_pattern, (int) start_time, (int)time_forward);
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
				System.out.println("Pattern Space: updating with new state vector.");
				update(sv);
				i++;
				sv = tc.get_last(i);
			}
			last_time = tc.current_time();
		}
	}
	private void update(TimeCapsule.StateVector sv) {
		history.add(sv);
		return;
	}
	public MotionProjection search(TimeCapsule.StateVector[] pattern, int start_time, int time_forward) {
		// overall method to search/return the closest pattern
		// projects from the end of the pattern
		// 7x linked list traversal
		// circular buffer of 7 distances. each distance += error for the next step from pattern
		// min distance stored, along with end of pattern. At the end of each pattern/7 step, it checks distance.
		//   if it is less than min, it puts itself as the 
		

		if(history.size() <= pattern.length+time_forward) {
			return null;
		}
		int best_match_index = 0;
		
		System.out.println("create projection (enough history): ");
		return create_projection(pattern[pattern.length-1], best_match_index, start_time, time_forward);
	}
	private MotionProjection create_projection(TimeCapsule.StateVector stateVector,
			int hist_index, int start_time, int time_forward) {
		System.out.println("StateVector to match: "+stateVector.toString());
		double x = stateVector.x();
		double y = stateVector.y();
		double h = stateVector.heading();
		
		double[] px = new double[(int) time_forward];
		double[] py = new double[(int) time_forward];
		long[] pt = new long[(int) time_forward];
		px[0] = x;
		py[0] = y;
		pt[0] = start_time;
		
		double dx;
		double dy;
		double new_vel;
		double new_hea = h;
		for(int i = 1 ; i < time_forward; i++) {
			new_vel = history.get(hist_index+i).velocity();
			new_hea += history.get(hist_index+i).heading()-history.get(hist_index+i-1).heading();
			dx = new_vel * Math.cos(new_hea);
			dy = new_vel * Math.sin(new_hea);
			px[i] = px[i-1]+dx;
			py[i] = py[i-1]+dy;
			pt[i] = start_time+i;
		}
		System.out.println("Pattern Motion Projection created\n x: "+Arrays.toString(px)+"\n y: "+Arrays.toString(py));
		return new MotionProjection(px, py, pt);
	}

	public double error(TimeCapsule.StateVector[] pattern, int start_index) {
		// compares the pattern to the values at the start index
		double p0_v = pattern[0].velocity()/8; // range +- 1
		
		double h0_v = history.get(start_index).velocity()/8; // range +- 1
		
		double accum = 0.0;
		accum += Math.abs(p0_v-h0_v);
		
		// note, because both are adjusted to match original heading, 0 error between heading
		double p_dh;
		double h_dh;
		for(int i = 1; i < pattern.length; i++) {
			// velocity error
			accum += Math.abs(pattern[0].velocity()-history.get(start_index+i).velocity());
			
			// heading change error
			// 0.1745 rad = 10 deg (max turn rate)
			p_dh = (pattern[i].heading()-pattern[i-1].heading())/0.1745;
			h_dh = (history.get(start_index+i).heading()-history.get(start_index+i-1).heading())/0.1745;
			accum += Math.abs(p_dh - h_dh);
		}
		return accum;
	}
}