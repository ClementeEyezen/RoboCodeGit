package arc.model.motions;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class LinearMotion extends MotionType {

	public LinearMotion(TimeCapsule tc) {
		super(tc);
	}

	@Override
	public MotionProjection project(TimeCapsule tc, long time_forward) {
		double[] x = new double[(int) time_forward];
		double[] y = new double[(int) time_forward];
		long[] t = new long[(int) time_forward];
		
		long start_time = tc.last_time();
		
		TimeCapsule.StateVector start_data = tc.last().get(0);
		int t_offset = (int) start_time - (int) start_data.time();
		if (t_offset < 0) {
			start_data = tc.at(start_time).get(0);
			t_offset = 0;
		}
		if (start_data == null ) {
			System.out.println("Linear Motion: invalid prediction start time (null data at start_time)");
		}
		double dx = start_data.velocity() * Math.cos(start_data.heading());
		double dy = start_data.velocity() * Math.sin(start_data.heading());
		System.out.println(tc+" LinearMotion: heading: "+correct_angle(start_data.heading()));
		
		for(int i = 0; i < time_forward; i++) {
			x[i] = start_data.x() + (i+t_offset)*dx;
			y[i] = start_data.y() + (i+t_offset)*dy;
			t[i] = start_time+i;
		}
		
		return new MotionProjection(x, y, t);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(ScannedRobotEvent sre) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(AdvancedRobot ar) {
		// TODO Auto-generated method stub
		
	}
	
}
