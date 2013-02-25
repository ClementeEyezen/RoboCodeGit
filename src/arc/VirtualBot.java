package arc;

import java.util.ArrayList;

import robocode.Robot;

public class VirtualBot 
{
	//a virtual robot to model a robot currently visible on the playing field
	//contains an arraylist of points last seen
	ArrayList<Point3D> stamps;
	String name; //the name of the robot that is being tracked
	Robot image; //the robot that the virtual robot represents
	public VirtualBot(Robot r)
	{
		image = r;
		name = r.getName();
	}
	public void addPoint(double x, double y, double turn)
	{
		stamps.add(new Point3D(x,y,turn));
	}
}
