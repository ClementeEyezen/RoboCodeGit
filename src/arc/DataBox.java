package arc;

import robocode.ScannedRobotEvent;

public class DataBox 
{
	ScannedRobotEvent lastScan;
	public void scanEvent(ScannedRobotEvent sre)
	{
		lastScan = sre;
	}
}
