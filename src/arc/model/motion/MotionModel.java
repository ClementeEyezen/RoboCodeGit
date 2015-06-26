package arc.model.motion;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import arc.model.RobotModel;
import arc.model.TimeCapsule;
import arc.model.Update;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class MotionModel implements Update {
	
	private PriorityQueue<MotionType> models;
	
	private HashMap<MotionType, Queue<MotionProjection>> projections;
	
	private RobotModel parent;
	
	public MotionModel(RobotModel parent, MotionType mt) {
		this.parent = parent;
		models = new PriorityQueue<MotionType>(1); // TODO add ordering
		models.add(mt);
		projections = new HashMap<MotionType, Queue<MotionProjection>>();
		projections.put(mt, new LinkedList<MotionProjection>());
	}
	public MotionModel(RobotModel parent, ArrayList<MotionType> almt) {
		// TODO update for multiple competing robot models
		this(parent, almt.get(0));
	}
	
	/*
	 * UPDATE
	 */
	
	@Override
	public void update() {
		for(MotionType mt : models) {
			mt.update();
			long current_time = parent.current_history().last_time();
			while(projections.get(mt).element().expired(current_time)) {
				test(mt, projections.get(mt).remove());
			}
		}
	}
	public void update(ScannedRobotEvent sre) {
		if(sre.getName().equals(parent.name())) {
			for(MotionType mt: models) {
				// update types
				mt.update(sre);
				
				// create predictions
				MotionProjection mp = mt.project(parent.current_history(), 10);
				
				// put it in the list of projections to check later
				boolean success = projections.get(mt).offer(mp);
				if(!success) {
					System.out.println("Failed to add projection to FIFO for "+mt);
				}
			}
		}
	}
	public void update(AdvancedRobot ar) {
		if(ar.getName().equals(parent.name())) {
			for(MotionType mt: models) {
				// update types
				mt.update(ar);
				
				// create predictions
				MotionProjection mp = mt.project(parent.current_history(), 10);
				
				// put it in the list of projections to check later
				boolean success = projections.get(mt).offer(mp);
				if(!success) {
					System.out.println("Failed to add projection to FIFO for "+mt);
				}
			}
		}
	}
	
	/*
	 * MAKE PREDICTIONS
	 */
	
	public ArrayList<MotionProjection> predict(int time_forward) {
		ArrayList<MotionProjection> result = new ArrayList<MotionProjection>();
		result.add(models.element().project(parent.current_history(), time_forward));
		return result;
	}
	
	public ArrayList<MotionProjection> predict(int time_forward, int num_predictions) {
		ArrayList<MotionProjection> results = new ArrayList<MotionProjection>();
		for(int i = 0; i < num_predictions; i++) {
			results.addAll(predict(time_forward));
		}
		return results;
	}
	
	/*
	 * TESTING PROJECTIONS
	 */
	public double test(MotionType projector, MotionProjection expired_projection) {
		// TODO fix
		return 1.0;
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
		public StandStill(TimeCapsule tc) {
			super(tc);
		}

		@Override
		public MotionProjection project(TimeCapsule tc, long time_forward) {
			long start_time = tc.last_time();
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

		@Override
		public void update() {}

		@Override
		public void update(ScannedRobotEvent sre) {}

		@Override
		public void update(AdvancedRobot ar) {}
	}
	
	public void onPaint(Graphics2D g) {
		// TODO
	}
}