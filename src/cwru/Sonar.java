package cwru;

import java.util.ArrayList;

import robocode.ScannedRobotEvent;

public class Sonar extends Brain
{
	double radarEndTheta;
	ArrayList<ScannedRobotEvent> unprocessedScans;
	byte scanIterator;
	ArrayList<Double> xlist = new ArrayList<Double>();
	ArrayList<Double> ylist = new ArrayList<Double>();
	
	//IDArray storageUnit;//this is the active data storage and access for Sonar
	
	public Sonar(LifeBox source)
	{
		super(source);
		unprocessedScans = new ArrayList<ScannedRobotEvent>();
		scanIterator = 0;
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
		while (unprocessedScans.size()>0)
		{
			processScan(unprocessedScans.get(0));
			unprocessedScans.remove(0);
		}
		if (turnCounter < 18)
		{
			radarEndTheta = Double.POSITIVE_INFINITY;
		}
		else
		{
			radarEndTheta = 0; //calculate desired theta
			//move along the list of robots. This will guaruntee seeing every robot 
			//at least once every few scan cycles, and will scan faster for fewer robots
		}
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("RAD calc time (millis):"+totalTime);
	}
	public double move_to_next_enemy()
	{
		double danny = 0;
		xlist = new ArrayList<Double>();
		ylist = new ArrayList<Double>();
		for (RoboCore rc : source.ronny)
		{
			if (rc.lastX>=0 && rc.lastY>=0)//if the robot's been scanned
			{
				xlist.add(rc.lastX);
				ylist.add(rc.lastY);
			}
		}
		//at this point, I have a list of the scanned robots 
		//	(all will be scanned by first two spins)
		double math_bearing = Math.atan2(ylist.get(scanIterator)-source.getRobot().getY(),
				xlist.get(scanIterator)-source.getRobot().getX());
		double robo_bearing = -math_bearing+(Math.PI/2);
		danny = robo_bearing-source.getRobot().getGunHeadingRadians();
		if (danny<Math.PI/4)
		{
			scanIterator = (byte) ((scanIterator+1)%(Math.min(xlist.size(), ylist.size())));
		}
		return danny;
	}
	public void inputScan(ScannedRobotEvent s)
	{
		unprocessedScans.add(s);
	}
	public void processScan(ScannedRobotEvent s)
	{
		//String rName = s.getName();
		/*for (nameArray n : storageUnit)
		{
			if (rName.equals(n.name()))
			{*/
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
				/*n.add(larry);																	//add it to the list of "larry" data
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
*/	
		//take the new robotbite larry, add to Lifebox
		source.store(larry);
	}
	public final void set()
	{
		source.getRobot().setTurnRadarLeftRadians(radarEndTheta);
	}
}
