package arc.model.target;

import java.util.ArrayList;

public class TargettingProjection {

	// locations where an targeting projection would put the robot
	ArrayList<Triple<Double, Double, Long>> forward;
	long start_time;
	long end_time;
	
	public TargettingProjection(double[] x, double[] y, long[] t) {
		for(int i = 0; i < x.length && i < y.length; i++) {
			forward.add(new Triple<Double, Double, Long>(new Double(x[i]), new Double(y[i]), new Long(t[i])));
		}
		start_time = t[0];
		end_time = t[t.length-1];
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
