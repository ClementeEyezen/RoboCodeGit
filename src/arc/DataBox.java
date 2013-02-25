package arc;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class DataBox 
{
	ScannedRobotEvent lastScan;
	AdvancedRobot self;
	public DataBox(AdvancedRobot r)
	{
		self = r;
	}
	public AdvancedRobot getRobot()
	{
		return self;
	}
	public void scanEvent(ScannedRobotEvent sre)
	{
		lastScan = sre;
	}
	public double getGunDirection()
	{
		return self.getGunHeadingRadians();
	}
	public double getRobotDirection()
	{
		return self.getHeadingRadians();
	}
	public double getRadarDirection()
	{
		return self.getRadarHeadingRadians();
	}
}
