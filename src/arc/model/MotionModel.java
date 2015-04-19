package arc.model;

import java.util.ArrayList;
import java.util.List;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class MotionModel {
	
	private MotionType most_likely;
	
	private List<MotionType> models;
	
	private RobotModel parent;
	
	public MotionModel(RobotModel parent) {
		// TODO constructor
		// models/calculates the motions of the parent robot
		// two functions:
		// 		provide domain knowledge about how the robot could move
		// 		provide maximum likelihood fitting to a Motion Type
		
		models = new ArrayList<MotionType>();
		models.add(new StandStill());
		most_likely = models.get(0);
		this.parent = parent;
	}
	public void update() {
		for(MotionType mt : models) {
			mt.update(parent.tc);
		}
	}
	public void test(ScannedRobotEvent sre, TimeCapsule updated_history) {
		
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
		if(!sre.getName().equals(parent.name)) return;
		for(MotionType mt : models) {
			mt.update_rating(updated_history);
		}
	}
	public void test(TimeCapsule updated_history) {
		// TODO test in the case of looking at self
		/*
		  	ar.getX() 
        	ar.getY() 
	        ar.getEnergy() 
	        ar.getHeadingRadians() 
	        ar.getName() 
	        ar.getVelocity() 
	        ar.getTime()
		 */
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
	
	class StandStill extends MotionType {
		@Override
		public MotionProjection project(TimeCapsule tc, long start_time, long time_forward) {
			double[] x = new double[(int)time_forward];
			double[] y = new double[(int)time_forward];
			long[] t = new long[(int)time_forward];
			
			TimeCapsule.StateVector start_data = tc.get_last(0);
			
			for(int i = 0; i < (int)time_forward; i++) {
				x[i] = start_data.x();
				x[i] = start_data.y();
				t[i] = start_time + i;
			}
			return new MotionProjection(x,y,t);
		}
	}
}