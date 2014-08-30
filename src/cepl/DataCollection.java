package cepl;

import java.util.ArrayList;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;
import robocode.ScannedRobotEvent;

public class DataCollection extends Control 	{
	
	ArrayList<BotBin> robots;
	
	BotBin selfie;
	
	public DataCollection()
	{
		robots = new ArrayList<BotBin>();
		selfie = new BotBin(source.getName());
		id = "DataCollection";
	}
	
	@Override
	public void setRobot(Ceepl s) {
		source = s;
	}

	@Override
	public void update(ScannedRobotEvent sre) {
		String name = sre.getName();
		boolean name_found = false;
		BotBin scanned_robot = null;
		for(int i = 0 ; i < robots.size() ; i++ )
		{
			if (robots.get(i).name.equals(name))
			{
				name_found = true;
				scanned_robot = robots.get(i);
			}
		}
		if (!name_found)
		{
			robots.add(new BotBin(name));
			scanned_robot = robots.get(robots.size()-1);
		}
		
		DataPoint prior;
		if (scanned_robot.info.size() >= 1)
		{
			prior = scanned_robot.info.get(scanned_robot.info.size()-1);
		}
		else
		{
			prior = null;
		}
		
		double myX = source.getX();
		double myY = source.getY();
		
		double robo_bearing = sre.getBearingRadians();
		double real_bearing = -robo_bearing + Math.PI/2;
		double robo_distance = sre.getDistance();
		
		double eX = myX + robo_distance*Math.cos(real_bearing);
		double eY = myY + robo_distance*Math.sin(real_bearing);
		
		DataPoint dp = new DataPoint(eX, eY, sre.getEnergy(), sre.getVelocity(), sre.getHeadingRadians()
				, sre.getTime(), prior, false);
		
		scanned_robot.addData(dp);
		
		if (robots.size() > 1)
		{
			source.melee_mode = true;
		}
		
	}

	@Override
	public void update() 
	{
		DataPoint prior = null;
		if(selfie.info.size()>1)
		{
			prior = selfie.info.get(selfie.info.size()-1);
		}
		
		DataPoint dp = new DataPoint(source.getX(), source.getY(), source.getEnergy(), source.getVelocity(), 
				source.getHeadingRadians(), source.getTime(), prior, false);
		selfie.addData(dp);
	}
	
}
