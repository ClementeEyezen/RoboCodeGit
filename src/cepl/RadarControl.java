package cepl;

import robocode.ScannedRobotEvent;

public class RadarControl extends Control	{
	
	public RadarControl()
	{
		id = "RadarControl";
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
	public void update(ScannedRobotEvent sre) 
	{
		update();
	}

}
