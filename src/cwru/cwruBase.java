package cwru;

import robocode.AdvancedRobot;

public class cwruBase extends AdvancedRobot
{
	LifeBox fileSystem;
	Sonar sally;
	Gunner biggles;
	Legs larson;
	Projector lcd;
	long processTime;
	long totalTime;
	public void run()
	{
		//================SETUP================
		fileSystem = new LifeBox(this);
		sally = new Sonar(fileSystem);
		biggles = new Gunner(fileSystem);
		larson = new Legs(fileSystem);
		lcd = new Projector(fileSystem);
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		//==========REPEATING ACTIONS==========
		while(true)
		{
			//recurring actions
			processTime = System.currentTimeMillis();
			lcd.process(); //projection of movement
			sally.process(); //radar movement/data add
			biggles.process(); //gun movement/data add
			larson.process(); //move movement/data add
			totalTime = System.currentTimeMillis()-processTime;
			System.out.println("TOTAL calc time (millis):"+totalTime);
			//end recurring actions
			processTime = System.currentTimeMillis();
			sally.set();
			biggles.set();
			larson.set();
			totalTime = System.currentTimeMillis()-processTime;
			System.out.println("set Action time (millis):"+totalTime);
			execute();
		}
	}
}
