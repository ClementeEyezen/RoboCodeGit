package arc;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class ninjaBot extends AdvancedRobot
{
	DataBox dan;
	MoveBrain mary;
	GunBrain gary;
	RadarBrain rarely;
	public void run()
	{
		dan = new DataBox();
		mary = new MoveBrain(dan);
		gary = new GunBrain(dan);
		rarely = new RadarBrain(dan);
	}
	public void onScannedRobot(ScannedRobotEvent sre)
	{
		dan.scanEvent(sre);
	}
	public void moveGunTo(double theta)
	{
		//move the gun to the heading theta
	}
	public void setGunFire(int delay)
	{
		//set the gun to fire a certain amount of steps later
	}
	public void moveRadarTo(double theta)
	{
		//move the gun to the heading theta
	}
	public void driveRobotTo()
	{
		
	}
}
