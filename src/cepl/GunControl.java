package cepl;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class GunControl extends Control 	{

	AdvancedRobot source;
	
	public GunControl()
	{
		id = "GunControl";
	}
	
	@Override
	public void setRobot(Ceepl s) {
		source = s;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(ScannedRobotEvent sre) {
		update();
	}

}
