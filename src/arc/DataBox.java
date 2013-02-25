package arc;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class DataBox 
{
	ScannedRobotEvent lastScan;
	ArcBasicBot self;
	public DataBox(ArcBasicBot r)
	{
		self = r;
	}
	public ArcBasicBot getRobot()
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
