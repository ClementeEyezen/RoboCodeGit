package cepl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;
import cepl.dataStorage.Wave;

import robocode.ScannedRobotEvent;

public class MovementControl extends Control	{

	ArrayList<Wave> shoreline;

	public MovementControl()
	{
		id = "MovementControl";
		shoreline = new ArrayList<Wave>();
	}

	@Override
	public void setRobot(Ceepl s) {
		source = s;
	}

	@Override
	public void update() {
		//look for waves
		long current_time = source.getTime();
		ArrayList<BotBin> robot_list = source.ssd.robots;
		for (BotBin robot : robot_list)
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

		//movement code
		// state generation min-max AI?
		// wave surfing
		// flat movement on fire (single only)
		// hot bullets

	}

	@Override
	public void update(ScannedRobotEvent sre) {
		update();
	}

	@Override
	public String toFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onPaint(Graphics2D g) {
		for (int i = 0 ; i < shoreline.size(); i++)
		{
			g.setColor(new Color (0, 0,	255, (int)(255/2)));
			Wave current = shoreline.get(i);
			if (!current.complete)
			{
			g.drawOval((int) (current.x - current.radius), 
					(int) (current.y - current.radius), 
					(int) (2*current.radius), (int) (2*current.radius));
			}
		}
		
	}

}