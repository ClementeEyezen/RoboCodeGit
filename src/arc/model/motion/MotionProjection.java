package arc.model.motion;

import java.util.ArrayList;

import arc.model.Gaussian;
import arc.model.TimeCapsule;
import arc.model.TimeCapsule.StateVector;

public class MotionProjection {
	ArrayList<Triple<Double, Double, Long>> forward;
	long start_time;
	long end_time;
	
	public MotionProjection(double[] x, double[] y, long[] t) {
		for(int i = 0; i < x.length && i < y.length; i++) {
			forward.add(new Triple<Double, Double, Long>(new Double(x[i]), new Double(y[i]), new Long(t[i])));
		}
		start_time = t[0];
		end_time = t[t.length-1];
	}
	
	public double test(TimeCapsule tc) {
		ArrayList<Double> error = new ArrayList<Double>();
		double sum = 0;
		for(int i = 0; i < forward.size(); i++) {
			TimeCapsule.StateVector sv = tc.get_data(forward.get(i).t);
			if (sv != null ) {
				double err = error(sv, forward.get(i).x, forward.get(i).y);
				error.add(new Double(err));
				sum += err;
			}
		}
		if (error.size() <= 0) {
			return 0.0;
		}
		else if (error.size() == 1) {
			return Math.max(1,1/error.get(0).doubleValue());
		}
		else {
			//double mean = sum / error.size();
			double stdv = stdev(error);
			// 6  (99%) -> .99
			// 9  (95%) -> .91
			// 18 (70%) -> .50
			// 36 (35?) -> .18
			return Math.pow( 2*(Gaussian.Phi(18, 0, stdv)-.5) , 1.8 );
		}
	}
	public double error(TimeCapsule.StateVector sv, double x, double y) {
		return Math.sqrt(Math.pow(sv.x()-x, 2)+Math.pow(sv.y()-y, 2));
	}
	public double stdev(ArrayList<Double> errors) {
		double accum = 0.0;
		for(int i = 0; i < errors.size(); i++) {
			accum += Math.pow(errors.get(i),2);
		}
		return Math.sqrt(accum/errors.size());
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
