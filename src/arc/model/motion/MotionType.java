package arc.model.motion;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.Update;

public abstract class MotionType implements Update {
	
	protected TimeCapsule data;
	
	public MotionType(TimeCapsule tc) {
		data = tc;
	}
	
	public abstract void update();
	public abstract void update(ScannedRobotEvent sre);
	public abstract void update(AdvancedRobot ar);

	public abstract MotionProjection project(TimeCapsule tc, long time_forward);
	
	protected final double correct_angle(double head_or_bear) {
		//return head_or_bear;
		return -1*head_or_bear + Math.PI/2;
	}
}