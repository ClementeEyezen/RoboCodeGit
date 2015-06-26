package arc.model.motions;

import java.util.Random;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class ProbableAction extends MotionType {
	
	Random random;

	public ProbableAction(TimeCapsule data_source) {
		super(data_source);
		random = new Random();
	}

	/* UPDATE */
	
	@Override
	public void update() {}
	@Override
	public void update(ScannedRobotEvent sre) {
		include_new_state(data.last().get(0));
	}
	@Override
	public void update(AdvancedRobot ar) {
		include_new_state(data.last().get(0));
	}
	public void include_new_state(TimeCapsule.StateVector sv) {
		// TODO Fill in method
	}

	/* PROJECT */
	
	@Override
	public MotionProjection project(TimeCapsule tc, long time_forward) {
		double[] x = new double[(int) time_forward];
		double[] y = new double[(int) time_forward];
		long[] t = new long[(int) time_forward];
		
		x[0] = tc.last().get(0).x();
		y[0] = tc.last().get(0).y();
		t[0] = tc.last_time();
		
		return new MotionProjection(x, y, t);
	}
	
	
	
	
	
	/* TEST Probable Action */

	public static boolean test() {
		return true;
	}
	
	public static void main(String[] args) {
		boolean t = test();
		if(t)
			System.out.println("Succesful test.");
		else
			System.out.println("Failed test.");
	}

}
