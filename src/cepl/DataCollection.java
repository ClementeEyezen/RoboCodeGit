package cepl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;
import cepl.dataStorage.Wave;
import robocode.ScannedRobotEvent;

public class DataCollection extends Control {

	long last_scan_time;

	ArrayList<BotBin> robots;
	ArrayList<Wave> shoreline;

	BotBin selfie;

	public DataCollection()
	{
		robots = new ArrayList<BotBin>();
		shoreline = new ArrayList<Wave>();
		id = "DataCollection";
	}

	@Override
	public void setRobot(Ceepl s) {
		source = s;
		selfie = new BotBin(source.getName());
	}

	@Override
	public void update(ScannedRobotEvent sre) {
		//System.out.println("Scanned Robot Event");

		if (sre.getTime() > this.last_scan_time) {last_scan_time = sre.getTime();}

		String name = sre.getName();
		boolean name_found = false;
		BotBin scanned_robot = null;
		for(int i = 0 ; i < robots.size() ; i++ )
		{
			//System.out.println("Robots list = "+robots);
			//System.out.println("Robot = "+robots.get(i));
			//System.out.println("Robot.name = "+robots.get(i).name);
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

		double robo_bearing = (sre.getBearingRadians()+source.getHeadingRadians())%(Math.PI*2);
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
		//System.out.println("End Scanned Robot Event");
	}

	@Override
	public void update() 
	{
		//check for radar misses
		DataPoint prior = null;
		if (last_scan_time <= source.getTime()- 8)
		{
			//System.out.println("Radar Reset = true");
			source.reset_radar = true;
		}
		else
		{
			source.reset_radar = false;
		}

		//add information about self
		if(selfie.info.size()>1)
		{
			prior = selfie.info.get(selfie.info.size()-1);
		}
		DataPoint dp = new DataPoint(source.getX(), source.getY(), source.getEnergy(), source.getVelocity(), 
				source.getHeadingRadians(), source.getTime(), prior, false);
		selfie.addData(dp);
		
		//look for waves
		long current_time = source.getTime();
		for (BotBin robot : robots)
		{
			try
			{
				if (robot.info.size()>1 && robot.info.get(0) != null 
						&& robot.info.get(robot.info.size()-1).time == current_time)
				{
					if (robot.info.get(robot.info.size()-1).energy < robot.info.get(robot.info.size()-2).energy)
					{
						DataPoint robot_of_choice = robot.info.get(robot.info.size()-1);
						shoreline.add(new Wave(
								robot.name,
								robot_of_choice.x, 
								robot_of_choice.y, 
								robot.info.get(robot.info.size()-1).time, 
								Math.abs(robot_of_choice.energy - 
										robot.info.get(robot.info.size()-2).energy),
										source));
					}
				}
			}
			catch (NullPointerException npe)
			{

			}
		}
		//update wave list
		for(int i = 0; i < shoreline.size(); i++)
		{
			shoreline.get(i).update(source.getTime());
		}
	}

	@Override
	public String toFile() {
		String complete = new String();

		//self
		complete += "<BotBin name="+selfie.name+
		" fill%= "+selfie.percent_fill()+">"+"\n";
		for(int j = 0; j < selfie.info.size(); j++)
		{
			complete += selfie.info.get(j).toString()+"\n";
		}
		complete += "</BotBin>";
		//others
		for(int i = 0; i < robots.size(); i++)
		{
			complete += "<BotBin name="+robots.get(i).name+
			" fill%= "+robots.get(i).percent_fill()+">"+"\n";
			for(int j = 0; j < robots.get(i).info.size(); i++)
			{
				complete += robots.get(i).info.get(j).toString()+"\n";
			}
			complete += "</BotBin>";
		}

		return complete;
	}

	public void onPaint(Graphics2D g) {
		//paint existing data by robots
		BotBin current_robot;
		for(int i = 0; i < robots.size(); i++)
		{
			current_robot = robots.get(i);
			for (int j = 0; j < current_robot.info.size(); j++)
			{
				DataPoint dp = current_robot.info.get(j);
				if (dp.generated_data) {
					Color filled = new Color((int) 0, (int) (i/robots.size()*255), (int) 255, 
							Math.max(0,(int) (255-(current_robot.info.size()-j)))); 
					g.setColor(filled);
				}
				else
				{
					Color scanned = new Color((int) 255, (int) (i/robots.size()*255), (int) 0, 
							Math.max(0,(int) (255-(current_robot.info.size()-j))));
					g.setColor(scanned);
				}
				g.fillRect((int) (dp.x-1),(int) (dp.y-1), 3, 3);
			}
		}
		//paint waves
		for (int i = 0 ; i < shoreline.size(); i++)
		{
			Wave current = shoreline.get(i);
			if (!current.complete)
			{
			//set color to wave
			g.setColor(new Color (0, (int)(255/5*0),255, (int)(255)));
			g.drawOval((int) (current.wave_x - current.radius), 
					(int) (current.wave_y - current.radius), 
					(int) (2*current.radius), (int) (2*current.radius));
			//set color to headon
			g.setColor(new Color(0,(int)(255/5*1),255,(int)(255)));
			g.drawLine((int)(current.wave_x), (int)(current.wave_y), 
					(int)(current.wave_x+current.radius*Math.cos(current.hot_head_on)),
					(int)(current.wave_y+current.radius*Math.sin(current.hot_head_on)));
			//set color to linear
			g.setColor(new Color(0,(int)(255/5*2),255,(int)(255)));
			g.drawLine((int)(current.wave_x), (int)(current.wave_y), 
					(int)(current.wave_x+current.radius*Math.cos(current.hot_linear)),
					(int)(current.wave_y+current.radius*Math.sin(current.hot_linear)));
			//set color to curve_linear
			g.setColor(new Color(0,(int)(255/5*3),255,(int)(255)));
			g.drawLine((int)(current.wave_x), (int)(current.wave_y), 
					(int)(current.wave_x+current.radius*Math.cos(current.hot_curve_linear)),
					(int)(current.wave_y+current.radius*Math.sin(current.hot_curve_linear)));
			//set color to prior
			//g.setColor(new Color(0,(255/5*1),255,(int)(255/2)));
			//draw prior line (not in wave)
			}
		}
		
	}
}
