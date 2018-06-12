package cepl;

import robocode.ScannedRobotEvent;

public abstract class Control {

	Ceepl source;
	String id;
	
	public abstract void update(ScannedRobotEvent sre);
	
	public abstract void update();
	
	public abstract void setRobot(Ceepl s);
	
	public abstract String toFile();

}