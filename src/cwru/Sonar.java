package cwru;

import java.util.ArrayList;

import robocode.ScannedRobotEvent;

public class Sonar extends Brain
{
	double radarEndTheta;
	ArrayList<ScannedRobotEvent> unprocessedScans;
	
	public Sonar(LifeBox source)
	{
		super(source);
		unprocessedScans = new ArrayList<ScannedRobotEvent>();
	}
	
	//Sonar is the radar brain.
	//It has two functions
	//		control where the radar looks
	//		control where the radar data goes
	public void process()
	{
		long processTime = System.currentTimeMillis();
		if (turnCounter < 4)
		{
			radarEndTheta = Double.POSITIVE_INFINITY;
		}
		else
		{
			radarEndTheta = 0; //calculate desired theta
		}
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("RAD calc time (millis):"+totalTime);
	}
	public void set()
	{
		source.getRobot().setTurnRadarLeftRadians(radarEndTheta);
	}
}
