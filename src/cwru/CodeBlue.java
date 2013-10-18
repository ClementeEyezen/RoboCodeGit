package cwru;

import java.awt.Graphics2D;

import robocode.ScannedRobotEvent;

public class CodeBlue extends cwruBase implements Paintable
{
	public void run()
	{
		//================SETUP================
		fileSystem = new LifeBox(this);
		sally = new Sonar(fileSystem, this);
		biggles = new Gunner(fileSystem, this);
		larson = new Sprinter(fileSystem, this);
		lcd = new Projector(fileSystem, this);
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		counter = 0;
		//==========REPEATING ACTIONS==========
		while(true)
		{
			System.out.println("New Turn "+getTime());
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
			
			//execute all movement
			processTime = System.currentTimeMillis();
			execute();
			totalTime = System.currentTimeMillis()-processTime;
			System.out.println("execute Time (millis):"+totalTime);
		}
	}
	public void onPaint(Graphics2D g)
	{
		fileSystem.onPaint(g);
		sally.onPaint(g);
		biggles.onPaint(g);
		larson.onPaint(g);
		lcd.onPaint(g);
	}
}
