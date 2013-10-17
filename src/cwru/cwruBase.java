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
	boolean runDelayTest = true;
	
	long counter;
	
	public void run()
	{
		//================SETUP================
		fileSystem = new LifeBox(this);
		sally = new Sonar(fileSystem, this);
		biggles = new Gunner(fileSystem, this);
		larson = new Legs(fileSystem, this);
		lcd = new Projector(fileSystem, this);
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		counter = 0;
		//==========REPEATING ACTIONS==========
		while(true)
		{
			//get X, Y coords and store
			cx = this.getX();
			cy = this.getY();
			//perform calculations
			processTime = System.currentTimeMillis();
			sally.process(); //radar movement/data add
			lcd.process(); //projection of movement
			biggles.process(); //gun movement/data add
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
			
			testResponseTime(40,runDelayTest);
			
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
	public void testResponseTime(long startTime, boolean runYN)
	{
		System.out.println("Delay test...");
		long displayed_time = startTime;
		if (runYN == true)
		{
			System.out.println("initiated...");
			long starter = System.currentTimeMillis();
			System.out.println("  Start time = "+starter);
			System.out.println("tst1:"+(System.currentTimeMillis() - starter));
			System.out.println("tst2:"+(counter+startTime));
			while (System.currentTimeMillis() - starter <= counter+startTime)
			{
				//System.out.println("tst1: "+(System.currentTimeMillis() - starter));
				//while the time since the start time < than the desired test time
				//do nothing, display the wait time if it's new
				if (displayed_time<counter+startTime)
				{
					displayed_time = counter+startTime;
					System.out.println("    Current delay: "+displayed_time);
				}
			}
			counter+=0;
			System.out.println("    Cycle "+counter);
		}
	}
}
