package arc.model.motion;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import arc.model.RobotModel;
import arc.model.TimeCapsule;
import arc.model.Update;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class MotionModel implements Update {
	
	private List<MotionType> models;
	
	private RobotModel parent;
	
	private MotionModel(RobotModel parent) {
		// constructor
		models = new ArrayList<MotionType>();
		this.parent = parent;
	}
	public MotionModel(MotionType mt) {
		
	}
	
	@Override
	public void update() {
		// TODO
	}
	public void update(ScannedRobotEvent sre) {
		// TODO // Update on scan of other
	}
	public void update(AdvancedRobot ar) {
		// TODO // Update on scan of self
	}
	
	/*
	 * MOTION PROPERTIES
	 */
	
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
	
	/*
	 * EXAMPLE MotionType
	 */
	
	class StandStill extends MotionType {
		@Override
		public MotionProjection project(TimeCapsule tc, long start_time, long time_forward) {
			System.out.println("time forward");
			double[] x = new double[(int)time_forward];
			double[] y = new double[(int)time_forward];
			long[] t = new long[(int)time_forward];
			
			if(tc.last().size() <= 0) {
				return new MotionProjection(x,y,t);
			}
			TimeCapsule.StateVector start_data = tc.last().get(0);
			
			for(int i = 0; i < (int)time_forward; i++) {
				x[i] = start_data.x();
				y[i] = start_data.y();
				t[i] = start_time + i;
			}
			return new MotionProjection(x,y,t);
		}
	}
	
	public void onPaint(Graphics2D g) {
		// TODO
	}
}