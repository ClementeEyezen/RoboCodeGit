package cepl;

import java.awt.Graphics2D;

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

	@Override
	public String toFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onPaint(Graphics2D g) {
		// TODO Auto-generated method stub, etc.
		
	}

}
