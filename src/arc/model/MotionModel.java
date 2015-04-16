package arc.model;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class MotionModel {
	
	private MotionType most_likely;
	
	public MotionModel(RobotModel parent) {
		// TODO constructor
		// models/calculates the motions of the parent robot
		// two functions:
		// 		provide domain knowledge about how the robot could move
		// 		provide maximum likelihood fitting to a Motion Type
	}
	public void test(ScannedRobotEvent sre, TimeCapsule history) {
		
		// Test models for fit
		// TODO
		/*
		  sre.getBearingRadians() 
          sre.getDistance() 
          sre.getEnergy() 
          sre.getHeadingRadians() 
          sre.getName() 
          sre.getVelocity() 
          sre.getTime()
		 */
	}
	public void test(AdvancedRobot ar, TimeCapsule history) {
		// TODO test in the case of looking at self
	}
	
	public double max_velocity(TimeCapsule.StateVector state) {
		if(state.velocity() >= 0) {
			return Math.min(8, state.velocity()+1);
		}
		else if (state.velocity() <= -2) {
			return state.velocity()+2;
		}
		else {
			return (state.velocity()+2)/2;
		}
		
	}
	public double min_velocity(TimeCapsule.StateVector state) {
		if(state.velocity() <= 0) {
			return Math.max(-8, state.velocity()-1);
		}
		else if (state.velocity() >= 2) {
			return state.velocity()-2;
		}
		else {
			return (state.velocity()-2)/2;
		}
	}
	
	abstract class MotionType {
		// project the motion of the robot forward using this motion type
		abstract Projection project(TimeCapsule tc, long start_time, long time_forward);
	} 
	class StandStill extends MotionType {

		@Override
		Projection project(TimeCapsule tc, long start_time, long time_forward) {
			double[] x = new double[(int)time_forward];
			double[] y = new double[(int)time_forward];
			long[] t = new long[(int)time_forward];
			for(int i = 0; i < (int)time_forward; i++) {
				x[i] = tc.data.get(new Integer((int)start_time)).x();
				x[i] = tc.data.get(new Integer((int)start_time)).y();
				t[i] = start_time + i;
			}
			return new Projection(x,y,t);
		}
	}
	
}