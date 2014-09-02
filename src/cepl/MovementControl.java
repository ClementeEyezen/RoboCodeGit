package cepl;

import robocode.ScannedRobotEvent;

public class MovementControl extends Control	{

	public MovementControl()
	{
		id = "MovementControl";
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

	@Override
	public String toFile() {
		// TODO Auto-generated method stub
		return null;
	}

}