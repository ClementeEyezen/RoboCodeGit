package cwru;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class cwruBase extends AdvancedRobot
{
	LifeBox fileSystem;
	Sonar sally;
	Gunner biggles;
	Legs larson;
	Projector lcd;
	long processTime;
	long totalTime;
	double cx;
	double cy;
	public void run()
	{
		//================SETUP================
		fileSystem = new LifeBox(this);
		System.out.println("fileSystem mainRobot after lifebox created = "+fileSystem.mainRobot);
		sally = new Sonar(fileSystem, this);
		biggles = new Gunner(fileSystem, this);
		larson = new Legs(fileSystem, this);
		lcd = new Projector(fileSystem, this);
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		//==========REPEATING ACTIONS==========
		while(true)
		{
			//get X, Y coords and store
			cx = this.getX();
			cy = this.getY();
			//perform calculations
			processTime = System.currentTimeMillis();
			System.out.println("pre error sonar");
			sally.process(); //radar movement/data add
			System.out.println("pre error Projector");
			lcd.process(); //projection of movement
			System.out.println("pre error Gunner?");
			biggles.process(); //gun movement/data add
			System.out.println("pre error Legs");
			larson.process(); //move movement/data add
			totalTime = System.currentTimeMillis()-processTime;
			System.out.println("TOTAL calc time (millis):"+totalTime);
			
			//set the movement based on calculations
			processTime = System.currentTimeMillis();
			sally.set();
			biggles.set();
			larson.set();
			totalTime = System.currentTimeMillis()-processTime;
			System.out.println("set Action time (millis):"+totalTime);
			
			//execute all movement
			processTime = System.currentTimeMillis();
			execute();
			totalTime = System.currentTimeMillis()-processTime;
			System.out.println("execute Time (millis):"+totalTime);
		}
	}
	public void onScannedRobotEvent(ScannedRobotEvent sre)
	{
		sally.inputScan(sre);
	}
}
