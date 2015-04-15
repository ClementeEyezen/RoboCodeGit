package cepl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;
import cepl.dataStorage.Wave;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

public class DataCollection extends Control {

	long last_scan_time;

	public ArrayList<BotBin> robots;
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

	public void update(HitByBulletEvent hbbe)
	{
		Wave closest = nearest_wave(hbbe.getName(), 
				hbbe.getBullet().getX(), hbbe.getBullet().getY());
		System.out.println("qqqqqqq closest = "+closest);
		if (closest != null)
		{
			System.out.println("hit by bullet from "+hbbe.getBullet().getName());
			//determine the head on bearing for that shot
			double real_bearing = Math.atan2(hbbe.getBullet().getY()-closest.wave_y, 
					hbbe.getBullet().getX()-closest.wave_x);
			//get the bullet hit bearing
			double bullet_robo_heading = hbbe.getBullet().getHeadingRadians();
			double bullet_real_heading = -bullet_robo_heading + Math.PI/2;
			//save the offset to that wave
			closest.true_hit_bearing = bullet_real_heading;
			closest.relative_hit_bearing = bullet_real_heading-real_bearing;
			closest.complete = true;

			double hit_angle = Math.atan2(hbbe.getBullet().getY()-closest.wave_y,hbbe.getBullet().getX()-closest.wave_x);
			//positive left
			double delta_from_head_on = closest.hot_head_on-hit_angle;
			double max_escape_angle = Math.atan(8/closest.bullet_velocity)+0;
			if (delta_from_head_on < -max_escape_angle)
			{
				delta_from_head_on = -Math.PI*(-2)-delta_from_head_on;
			}
			else if (delta_from_head_on > max_escape_angle)
			{
				delta_from_head_on = Math.PI*2-delta_from_head_on;
			}
			double percent = delta_from_head_on/max_escape_angle;
			for(BotBin robot : robots)
			{
				if(hbbe.getName().equals(robot.name))
				{
					robot.increment_hitbin(percent);
				}
			}
		}
	}

	public Wave nearest_wave(String robot_name, double hit_x, double hit_y)
	{
		double nearest_delta = Double.MAX_VALUE;
		Wave nearest_wave = null;
		for(Wave w : shoreline)
		{
			if (robot_name.equals(w.name) && !w.complete)
			{
				double distance = Math.sqrt((hit_x-w.wave_x)*(hit_x-w.wave_x)+
						(hit_y-w.wave_y)*(hit_y-w.wave_y));
				double delta = Math.abs(distance-w.radius);
				if (distance < nearest_delta)
				{
					nearest_delta = delta;
					nearest_wave = w;
				}
			}
		}
		return nearest_wave;
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
								robot,
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
				g.drawOval((int) (current.wave_x - current.radius), //x
						(int) (current.wave_y - current.radius),  //y
						(int) (2*current.radius), //width
						(int) (2*current.radius)); //height
				//calculate hitbin arcs
				double escape_angle_rad = Math.atan(8/current.bullet_velocity);
				double start_angle_rad = current.hot_head_on;
				double neg_angle_deg = ((start_angle_rad-escape_angle_rad+2*Math.PI)%Math.PI)/Math.PI*180;
				double unit_angle_deg = (escape_angle_rad/(current.robot.hitbin.length/2))/Math.PI*180;
				for(int j = 0; j < current.robot.hitbin.length; j++)
				{
					double hit_percent = (double) (current.robot.hitbin[j])/(double) (current.robot.sum+.000000001);
					//System.out.println("hitbin["+j+"] = "+current.robot.hitbin[j]+" percent = "+hit_percent);
					//System.out.println("hit_percent = "+hit_percent);
					g.setColor(new Color(255,0,0,255));//(int)(255*Math.min(1,Math.log(hit_percent+1)+.307)))); //rgba
					g.drawArc((int) (current.wave_x - current.radius), //x
							(int) (current.wave_y - current.radius), //y
							(int) (2*current.radius), //width
							(int) (2*current.radius), //height
							(int) (neg_angle_deg+i*unit_angle_deg), // arc start (deg)
							(int) Math.max(1,unit_angle_deg)); //arc swing (deg)
					if(hit_percent >0)
						System.out.println("qq hitbin["+j+"] percent "+hit_percent+
								" unit: "+(int)unit_angle_deg+" start:"
								+(int)(neg_angle_deg+i*unit_angle_deg));
				}
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
