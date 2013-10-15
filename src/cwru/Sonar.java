package cwru;

import java.util.ArrayList;

import robocode.ScannedRobotEvent;

public class Sonar extends Brain
{
	double radarEndTheta;
	ArrayList<ScannedRobotEvent> unprocessedScans;
	//IDArray storageUnit;//this is the active data storage and access for Sonar
	
	public Sonar(LifeBox source)
	{
		super(source);
		unprocessedScans = new ArrayList<ScannedRobotEvent>();
		//source.allocateArray(this, "All_RadarData");
		//storageUnit = source.request(this);
	}
	
	//Sonar is the radar brain.
	//It has two functions
	//		control where the radar looks
	//		control where the radar data goes
	public void process()
	{
		long processTime = System.currentTimeMillis();
		for (ScannedRobotEvent sre : unprocessedScans)
		{
			processScan(sre);
		}
		if (turnCounter < 18)
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
	public void inputScan(ScannedRobotEvent s)
	{
		unprocessedScans.add(s);
	}
	public void processScan(ScannedRobotEvent s)
	{
		//TODO rephrase this to work with the new data structure
		String rName = s.getName();
		for (nameArray n : storageUnit)
		{
			if (rName.equals(n.name()))
			{
				//if the robot matches prior data (robot name)
				
				//0 = time				[state]
				//1 = x					[state]
				//2 = y					[state]
				//3 = energy			[state]
				//4 = bearing radians 	[relative position]
				//5 = distance			[relative position]
				//6 = heading radians	[travel]
				//7 = velocity			[travel]
				RobotBite larry = new RobotBite(s.getName(), s.getTime(), source.mainRobot, s.getEnergy(),	//make a robobit with current data 
						s.getBearingRadians(), s.getDistance(), s.getHeadingRadians(), 
						s.getVelocity());
				n.add(larry);																	//add it to the list of "larry" data
				for (Object o : n)																//update all of the projections to find accuracy
					{
						RobotBite robobit = (RobotBite) o;
						for (Projection p : robobit.projec)
						{
							p.update(s.getTime(), larry.cx, larry.cy);
						}
					}
			}
		}
		storageUnit.add(new nameArray(rName)); //if there is no data matching the scan robot
			//add a new robot to the storage and repeat
		processScan(s);
			//this should find the old one and add the data properly
	}
	public void set()
	{
		source.getRobot().setTurnRadarLeftRadians(radarEndTheta);
	}
}
