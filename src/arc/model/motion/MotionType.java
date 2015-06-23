package arc.model.motion;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.Update;

public abstract class MotionType implements Update {
	
	public abstract void update();
	public abstract void update(ScannedRobotEvent sre);
	public abstract void update(AdvancedRobot ar);

	public abstract MotionProjection project(TimeCapsule tc, long time_forward);
}