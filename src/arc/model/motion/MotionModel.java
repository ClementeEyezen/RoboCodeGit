package arc.model.motion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import arc.model.RobotModel;
import arc.model.TimeCapsule;
import arc.model.TimeCapsule.StateVector;
import arc.model.motions.CircularMotion;
import arc.model.motions.LinearMotion;
import arc.model.motions.PatternMatcher;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class MotionModel {
	
	private MotionType most_likely;
	
	private List<MotionType> models;
	
	private RobotModel parent;
	
	public MotionModel(RobotModel parent) {
		// constructor
		// models/calculates the motions of the parent robot
		// two functions:
		// 		provide domain knowledge about how the robot could move
		// 		provide maximum likelihood fitting to a Motion Type
		
		models = new ArrayList<MotionType>();
		//models.add(new StandStill());
		models.add(new PatternMatcher(7));
		//models.add(new LinearMotion());
		most_likely = models.get(0);
		this.parent = parent;
	}
	public void update() {
		for(MotionType mt : models) {
			mt.update(parent.current_history());
		}
		for(MotionType mt : models) {
			if(mt.running_rating > most_likely().running_rating) {
				most_likely = mt;
			}
		}
	}
	public void test(ScannedRobotEvent sre, TimeCapsule updated_history) {
		
		// Test models for fit
		// TODO check
		/*
			  sre.getBearingRadians() 
	          sre.getDistance() 
	          sre.getEnergy() 
	          sre.getHeadingRadians() 
	          sre.getName() 
	          sre.getVelocity() 
	          sre.getTime()
		 */
		if(!sre.getName().equals(parent.name())) return;
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
	
	public MotionType most_likely() {
		return most_likely;
	}
	public MotionProjection ml_projection(long delta_time) {
		// return the most likely projection for where the robot is going to be through future time
		/*
		 * Example Use:
		 * Gun wants to fire at a power p that will hit the other robot after approx 10 turns
		 * Asks for prediction out to 12 turns
		 * If dist to prediction matches up for 10 turns, then shoot at that point
		 * If dist is too close , check 9, 8 etc.
		 * If dist is too far, check 11, 12 until a match
		 * 
		 * Shoot at the match
		 */
		MotionType likely = most_likely();
		long current_time = parent.current_history().current_time();
		return likely.project(parent.current_history(), current_time, delta_time);
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
			System.out.println("time forward");
			double[] x = new double[(int)time_forward];
			double[] y = new double[(int)time_forward];
			long[] t = new long[(int)time_forward];
			
			TimeCapsule.StateVector start_data = tc.get_last(0);
			
			for(int i = 0; i < (int)time_forward; i++) {
				x[i] = start_data.x();
				y[i] = start_data.y();
				t[i] = start_time + i;
			}
			return new MotionProjection(x,y,t);
		}
	}
	
	public void onPaint(Graphics2D g) {
		try {
			//System.out.println("Paint ML");
			ml_projection(20).onPaint(g, Color.ORANGE);
		}
		catch (NullPointerException npe){
			
		}
	}
}