package arc.model.motions;

import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class OnePassCircular extends MotionType {

	int max_time;
	
	double[] average_linear;
	double[] average_angular;
	
	public OnePassCircular(TimeCapsule data_source, int max_length) {
		super(data_source);
		data = data_source;
		max_time = max_length;
		average_linear = new double[max_time];
		average_angular = new double[max_time];
		for(int i = 0; i < max_time; i++) {
			average_linear[i] = 0.0;
			average_angular[i] = 0.0;
		}
	}
	
	@Override
	public void update() {}

	@Override
	public void update(ScannedRobotEvent sre) {
		update(data.last(2));
	}

	@Override
	public void update(AdvancedRobot ar) {
		update(data.last(2));
	}
	
	private void update(ArrayList<TimeCapsule.StateVector> last) {
		// perform the update on the last data
		if(last.size() == 2) {
			double new_lin = last.get(1).velocity();
			double new_ang = (last.get(1).heading() - last.get(0).heading()) / 
					(last.get(1).time() - last.get(0).time());
			
			// Trailing moving average over 0..max_time-1 trailing averages
			for(int i = 0; i < max_time; i++) {
				average_linear[i] = (new_lin+average_linear[i]*i)/(i+1);
				average_angular[i] = (new_ang+average_angular[i]*i)/(i+1);
			}
		}
	}

	@Override
	public MotionProjection project(TimeCapsule tc, long time_forward) {
		double[] x = new double[(int) time_forward];
		double[] y = new double[(int) time_forward];
		long[] t = new long[(int) time_forward];
		
		double h0 = tc.last().get(0).heading();
		double dx = average_linear[(int) time_forward] * Math.cos(h0);
		double dy = average_linear[(int) time_forward] * Math.sin(h0);
		x[0] = tc.last().get(0).x();
		y[0] = tc.last().get(0).y();
		t[0] = tc.last_time();
		
		for(int i = 1; i < time_forward; i++) {
			double new_vel = average_linear[(int) time_forward];
			double new_hea = h0+i*average_angular[(int) time_forward];
			System.out.println("new_vel: "+new_vel+" new_heading: "+new_hea);
			dx = new_vel * Math.cos(new_hea);
			dy = new_vel * Math.sin(new_hea);
			x[i] = x[i-1]+dx;
			y[i] = y[i-1]+dy;
			t[i] = tc.last_time()+i;
		}
		
		return new MotionProjection(x, y, t);
	}

}
