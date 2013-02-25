package arc;

import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class DataBox 
{
	ScannedRobotEvent lastScan;
	ArcBasicBot self;
	ArrayList<VirtualBot> opponents;
	public DataBox(ArcBasicBot r)
	{
		self = r;
		opponents = new ArrayList<VirtualBot>();
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
