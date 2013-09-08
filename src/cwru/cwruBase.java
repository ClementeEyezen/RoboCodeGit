package cwru;

import robocode.AdvancedRobot;

public class cwruBase extends AdvancedRobot
{
	LifeBox fileSystem;
	Sonar sally;
	Gunner biggles;
	Legs larson;
	
	public void run()
	{
		//================SETUP================
		fileSystem = new LifeBox();
		sally = new Sonar(fileSystem);
		biggles = new Gunner(fileSystem);
		larson = new Legs(fileSystem);
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		
		//==========REPEATING ACTIONS==========
		while(true)
		{
			//recurring actions
			sally.process();
			biggles.process();
			larson.process();
			//end recurring actions
			execute();
		}
	}
}
