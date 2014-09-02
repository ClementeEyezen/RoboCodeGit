package cepl;

import cepl.dataStorage.DataPoint;
import robocode.ScannedRobotEvent;

public class RadarControl extends Control	{

	boolean target_mode; //full sweep or not
	boolean multi_mode; //multiple targets
	short turn_count;
	String mode;
	double escape_angle = Math.atan(8.0/17.0);

	public RadarControl()
	{
		id = "RadarControl";
		mode = "default";
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
		}
		//control selection
		if (!target_mode)
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

			if (source.getRadarHeadingRadians()>robo_bearing && source.getRadarHeadingRadians() <robo_bearing+Math.PI)
			{
				//scan left to min_robot_loc
				double delta = source.getRadarHeadingRadians() - min_robot_loc;
				if (delta < -1*Math.PI)
				{
					delta = (360 + delta);
				}
				source.setTurnRadarLeftRadians(delta);
			}
			else
			{
				//scan to right max_robot_loc
				double delta = source.getRadarHeadingRadians() - max_robot_loc;
				if (delta > Math.PI)
				{
					delta = delta - 360;
				}
				source.setTurnRadarLeftRadians(delta);
			}
		}
	}
	public void multi_mode()
	{
		//TODO
		mode = "multi";
	}
}
