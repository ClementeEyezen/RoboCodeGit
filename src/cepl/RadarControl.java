package cepl;

import java.util.ArrayList;
import java.util.Collections;

import cepl.dataStorage.DataPoint;
import robocode.ScannedRobotEvent;

public class RadarControl extends Control	{

	boolean target_mode; //full sweep or not
	boolean multi_mode; //multiple targets
	short turn_count;
	String mode;
	double escape_angle = Math.atan(8.0/17.0);
	
	double first_bearing;
	double second_bearing;
	boolean second_flag;
	boolean default_mode;

	public RadarControl()
	{
		id = "RadarControl";
		mode = "default";
		first_bearing = 0;
		second_bearing = 0;
		second_flag = true;
		default_mode = false;
	}

	@Override
	public void setRobot(Ceepl s) {
		source = s;
	}

	@Override
	public void update() {
		turn_count++;
		//control mode logic
		if(turn_count > 16 && !target_mode) {
			//if it has done at least two full scans, activate target mode
			target_mode = true;
			if(source.ssd.robots.size()>1) {
				// if it has seen multiple robots, activate multi-target mode
				multi_mode = true;
			}
			System.out.println("Target Mode : "+target_mode+" multi : "+multi_mode);
		}
		//control selection
		if (source.reset_radar) {
			//System.out.println("Radar Reset");
		}
		//System.out.println("last scan time     = "+source.ssd.last_scan_time);
		//System.out.println("current robot time = "+source.getTime());
		
		if (!target_mode || default_mode || source.reset_radar)
		{
			dizzy_mode();
		}
		else
		{
			if (!multi_mode)
			{
				single_mode();
			}
			else
			{
				multi_mode();
			}
		}
	}

	@Override
	public void update(ScannedRobotEvent sre) 
	{
		update();
	}

	@Override
	public String toFile() {
		return "<Radar mode="+mode+">";
	}

	public void dizzy_mode()
	{
		mode = "dizzy";
		source.setTurnRadarLeft(Double.MAX_VALUE);
	}
	public void single_mode()
	{
		mode = "single";
		if(source.getRadarTurnRemainingRadians()<.01)
		{
			DataPoint last_self = source.ssd.selfie.info.get(source.ssd.selfie.info.size()-1);
			DataPoint last_else = source.ssd.robots.get(0).info.get(source.ssd.robots.get(0).info.size()-1);
			double myX = last_self.x;
			double myY = last_self.y;
			double eX =  last_else.x;
			double eY =  last_else.y;

			double dx = eX-myX;
			double dy = eY-myY;
			double real_bearing = Math.atan2(dy, dx);
			double robo_bearing = -real_bearing+Math.PI/2;
			while (robo_bearing < 0) robo_bearing += Math.PI*2;
			while (robo_bearing > Math.PI*2) robo_bearing -= Math.PI*2;
			double distance = Math.sqrt(dx*dx+dy*dy);

			double max_move_angle = Math.atan(8.0/distance);

			double min_robot_loc = robo_bearing - max_move_angle;
			double max_robot_loc = robo_bearing + max_move_angle;
			//System.out.println("Robo_bearing = "+robo_bearing*180/Math.PI);
			//TODO check for error with rotated coordinate system
			if (source.getRadarHeadingRadians()>robo_bearing && source.getRadarHeadingRadians() <robo_bearing+Math.PI)
			{
				//scan left to min_robot_loc
				double delta = source.getRadarHeadingRadians() - min_robot_loc;
				if (delta < -1*Math.PI)
				{
					delta = (360 + delta);
				}
				source.setTurnRadarLeftRadians(delta%(2*Math.PI));
			}
			else
			{
				//scan to right max_robot_loc
				double delta = source.getRadarHeadingRadians() - max_robot_loc;
				if (delta > Math.PI)
				{
					delta = delta - 360;
				}
				source.setTurnRadarLeftRadians(delta%(2*Math.PI));
			}
		}
	}
	public void multi_mode()
	{
		//TODO
		mode = "multi";
		if(source.getRadarTurnRemainingRadians()<.01 && second_flag)
		{
			//if it is finished the second swing
			DataPoint last_self = source.ssd.selfie.info.get(source.ssd.selfie.info.size()-1);
			double myX = last_self.x;
			double myY = last_self.y;
			ArrayList<Double> bearing = new ArrayList<Double>();

			for (int i = 0 ; i < source.ssd.robots.size(); i++)
			{
				DataPoint last_else = source.ssd.robots.get(i).info.get(source.ssd.robots.get(i).info.size()-1);
				double eX =  last_else.x;
				double eY =  last_else.y;
				bearing.add(Math.atan2(eY-myY,eX-myX));
			}
			Collections.sort(bearing);
			ArrayList<Double> splits = new ArrayList<Double>();
			for (int i = 0; i < bearing.size()-2; i++)
			{
				splits.add(bearing.get(i+1)-bearing.get(i));
			}
			splits.add(Math.PI*2-bearing.get(bearing.size()-1)+bearing.get(0));
			double max = 0;
			int index = 0;
			for (int i = 0; i < splits.size(); i++)
			{
				if (splits.get(i)>max)
				{
					max = splits.get(i);
					index = i;
				}
			}
			double fudge_factor = (8-max/45)*(8/100); //(radar turns per swing)*(s/turn)
			double min_robot_bearing = bearing.get(index)-fudge_factor;
			double max_robot_bearing;
			if (index < bearing.size()-1)
			{
				max_robot_bearing = bearing.get(index+1)+fudge_factor;
			}
			else
			{
				max_robot_bearing = bearing.get(0)+fudge_factor;
			}
			second_flag = false;
			//Turn to first
			//TODO
			first_bearing = min_robot_bearing;
			second_bearing = max_robot_bearing;
			
			double delta;
			if (first_bearing > source.getRadarHeadingRadians())
			{
				delta = Math.PI*2 - (first_bearing-source.getRadarHeadingRadians());
			}
			else
			{
				delta = source.getRadarHeadingRadians() - first_bearing;
			}
			source.setTurnRadarLeftRadians(delta%(2*Math.PI));
		}
		else if (source.getRadarTurnRemainingRadians()<.01 && !second_flag)
		{
			//if it is finished the first swing
			//then turn to second
			double delta;
			if (second_bearing < source.getRadarHeadingRadians())
			{
				delta = Math.PI*2 - (second_bearing - source.getRadarHeadingRadians());
			}
			else
			{
				delta = source.getRadarHeadingRadians() - second_bearing;
			}
			source.setTurnRadarRightRadians(delta%(2*Math.PI));
		}
		else
		{
			// do nothing, still in motion
		}
	}
}
