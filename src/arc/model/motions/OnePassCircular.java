package arc.model.motions;

import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class OnePassCircular extends MotionType {

	TimeCapsule data;
	
	int max_time;
	
	double[] average_linear;
	double[] average_angular;
	
	public OnePassCircular(TimeCapsule data_source, int max_length) {
		data = data_source;
		max_time = max_length;
		average_linear = new double[max_time];
		average_angular = new double[max_time];
		for(int i = 0; i < max_time; i++) {
			average_linear[i] = 0.0;
			average_angular[i] = 0.0;
		}
	}
	
	@Override
	public void update() {}

	@Override
	public void update(ScannedRobotEvent sre) {
		update(data.last(2));
	}

	@Override
	public void update(AdvancedRobot ar) {
		update(data.last(2));
	}
	
	private void update(ArrayList<TimeCapsule.StateVector> last) {
		// perform the update on the last data
		if(last.size() == 2) {
			double new_lin = last.get(1).velocity();
			double new_ang = (last.get(1).heading() - last.get(0).heading()) / 
					(last.get(1).time() - last.get(0).time());
			
			// Trailing moving average over 0..max_time-1 trailing averages
			for(int i = 0; i < max_time; i++) {
				average_linear[i] = (new_lin+average_linear[i]*i)/(i+1);
				average_angular[i] = (new_ang+average_angular[i]*i)/(i+1);
			}
		}
	}

	@Override
	public MotionProjection project(TimeCapsule tc, long time_forward) {
		// TODO Auto-generated method stub
		return null;
	}

}
