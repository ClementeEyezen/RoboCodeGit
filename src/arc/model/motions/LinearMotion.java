package arc.model.motions;

import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class LinearMotion extends MotionType {

	@Override
	public MotionProjection project(TimeCapsule tc, long start_time,
			long time_forward) {
		double[] x = new double[(int) time_forward];
		double[] y = new double[(int) time_forward];
		long[] t = new long[(int) time_forward];
		
		TimeCapsule.StateVector start_data = tc.get_last(0);
		int t_offset = (int) start_time - (int) start_data.time();
		if (t_offset < 0) {
			start_data = tc.get_data(start_time);
			t_offset = 0;
		}
		if (start_data == null ) {
			System.out.println("Linear Motion: invalid prediction start time (null data at start_time)");
		}
		double dx = start_data.velocity() * Math.cos(correct_angle(start_data.heading()));
		double dy = start_data.velocity() * Math.sin(correct_angle(start_data.heading()));
		System.out.println("LinearMotion: heading: "+correct_angle(start_data.heading()));
		
		for(int i = 0; i < time_forward; i++) {
			x[i] = start_data.x() + (i+t_offset)*dx;
			y[i] = start_data.y() + (i+t_offset)*dy;
			t[i] = start_time+time_forward;
		}
		
		return new MotionProjection(x, y, t);
	}
	
}
