package arc.model.motions;

import java.util.ArrayList;

import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class CircularMotion extends MotionType {
	@Override
	public MotionProjection project(TimeCapsule tc, long start_time,
			long time_forward) {
		
		double[] x = new double[(int) time_forward];
		double[] y = new double[(int) time_forward];
		long[] t = new long[(int) time_forward];
		
		TimeCapsule.StateVector step0, step1;
		ArrayList<TimeCapsule.StateVector> val = tc.last(2);
		try {
			step1 = val.get(1);
			step0 = val.get(0);
		}
		catch (IndexOutOfBoundsException ioobe) {
			return new MotionProjection(x, y, t);
		}
		
		double accel = step1.velocity()-step0.velocity();
		double omega = step1.heading()-step0.heading();
		
		double dx = step1.velocity()+0*accel * Math.cos(step1.heading()+0*omega);
		double dy = step1.velocity()+0*accel * Math.sin(step1.heading()+0*omega);
		x[0] = step1.x();
		y[0] = step1.y();
		t[0] = start_time;
		
		for(int i = 1; i < time_forward; i++) {
			double new_vel = step1.velocity()+i*accel;
			double new_hea = step1.heading()+i*omega;
			System.out.println("new_vel: "+new_vel+" new_heading: "+new_hea);
			dx = new_vel * Math.cos(new_hea);
			dy = new_vel * Math.sin(new_hea);
			x[i] = x[i-1]+dx;
			y[i] = y[i-1]+dy;
			t[i] = start_time+i;
		}
		
		return new MotionProjection(x, y, t);
	}

}
