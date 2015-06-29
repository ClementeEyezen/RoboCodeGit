package arc.model.motion;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;

public abstract class TransitionMotionType extends MotionType {

	public TransitionMotionType(TimeCapsule tc) {
		super(tc);
	}

	@Override
	public abstract void update();

	@Override
	public abstract void update(ScannedRobotEvent sre);

	@Override
	public abstract void update(AdvancedRobot ar);

	@Override
	public MotionProjection project(TimeCapsule tc, long time_forward) {
		double[] x = new double[(int) time_forward];
		double[] y = new double[(int) time_forward];
		long[] t = new long[(int) time_forward];
		
		TimeCapsule.StateVector state1 = tc.last(2).get(1);
		TimeCapsule.StateVector state0 = tc.last(2).get(0);
		for(int i = 0; i < time_forward; i++) {
			x[i] = state1.x();
			y[i] = state1.y();
			t[i] = (long) state1.time();
			
			state0 = state1.deepCopy();
			state1 = transition(state1, state0);
		}
		
		return new MotionProjection(x, y, t);
	}
	
	public TimeCapsule.StateVector transition(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1) {
		// defines a transition from an initial state s0 to the returned state s1;
		double head2 = heading(s0, s1);
		double vel2 = velocity(s0, s1);
		double x = s1.x()+vel2*Math.cos(head2);
		double y = s1.y()+vel2*Math.sin(head2);
		return data.sv_create(s1.time()+1, -1, -1, -1, head2, vel2, x, y);
	}
	
	public abstract double heading(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1);
	
	public abstract double velocity(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1);
}
