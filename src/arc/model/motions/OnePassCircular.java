package arc.model.motions;

import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.motion.MotionProjection;
import arc.model.motion.MotionType;

public class OnePassCircular extends MotionType {

	TimeCapsule data;
	
	int projection_length = 15;
	
	double average_linear;
	double average_angular;
	
	public OnePassCircular(TimeCapsule data_source) {
		data = data_source;
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
			
			// Trailing moving average of the last few data points
			average_linear = (new_lin+average_linear*projection_length)/(projection_length+1);
			average_angular = (new_ang+average_angular*projection_length)/(projection_length+1);
		}
	}

	@Override
	public MotionProjection project(TimeCapsule tc, long time_forward) {
		// TODO Auto-generated method stub
		return null;
	}

}
