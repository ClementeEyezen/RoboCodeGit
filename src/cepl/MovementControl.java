package cepl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;
import cepl.dataStorage.Wave;

import robocode.ScannedRobotEvent;

public class MovementControl extends Control	{

		public MovementControl()
	{
		id = "MovementControl";
	}

	@Override
	public void setRobot(Ceepl s) {
		source = s;
	}

	@Override
	public void update() {
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
		
	}

}