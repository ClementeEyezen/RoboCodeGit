package arc.model.motion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class MotionProjection {
	ArrayList<Triple<Double, Double, Long>> forward;
	long start_time;
	long end_time;
	
	public MotionProjection(double[] x, double[] y, long[] t) {
		forward = new ArrayList<Triple<Double, Double, Long>>();
		for(int i = 0; i < x.length && i < y.length; i++) {
			forward.add(new Triple<Double, Double, Long>(new Double(x[i]), new Double(y[i]), new Long(t[i])));
		}
		start_time = t[0];
		end_time = t[t.length-1];
	}
	
	public boolean expired(long current_time) {
		return end_time <= current_time;
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
	
	public void onPaint(Graphics2D g, Color c) {}
}
