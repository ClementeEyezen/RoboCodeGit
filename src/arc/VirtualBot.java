package arc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class VirtualBot 
{
	//a virtual robot to model a robot currently visible on the playing field
	//contains an arraylist of points last seen
	ArrayList<Point3D> location; //represented by lines
	ArrayList<Double> energy; //represented by color
	ArrayList<Double> heading; //represented by lines
	ArrayList<Double> velocity; //represented by box size
	String name; //the name of the robot that is being tracked
	Robot image; //the robot that the virtual robot represents
	public VirtualBot(Robot r)
	{
		image = r;
		name = r.getName();
		location = new ArrayList<Point3D>();
		energy = new ArrayList<Double>();
		heading = new ArrayList<Double>();
		velocity = new ArrayList<Double>();
	}
	public VirtualBot(String nam)
	{
		image = new Robot();
		name = nam;
		location = new ArrayList<Point3D>();
		energy = new ArrayList<Double>();
		heading = new ArrayList<Double>();
		velocity = new ArrayList<Double>();
	}
	public void addLocation(double x, double y, long turn)
	{
		System.out.println("x:"+x+" y:"+y+" turn:"+turn);
		Point3D jerry = new Point3D(x,y,turn);
		location.add(jerry);
	}
	public String getName() 
	{
		return name;
	}
	public void update(ScannedRobotEvent sre, Point myRobot, double myHeading)
	{
		if (sre.getName().equals(name))
		{
			//components of a Point3D
			long time = sre.getTime();
			double bearing = sre.getBearingRadians()+myHeading; //direction to the other robot
			double rotBearing = -(bearing-Math.PI/2);
			System.out.println("bearing "+sre.getBearing()+" rotated to "+(-sre.getBearing()+90));
			double distance = sre.getDistance(); //distance to the other robot
			System.out.println("at distance "+distance);
			double xloc = myRobot.getPoint()[0];
			double dx = distance*Math.cos(rotBearing);
			System.out.println("myloc:"+xloc+" relative x: "+dx+" result ==> "+(xloc+dx));
			xloc+=dx;//add the other robot's location
			double yloc = myRobot.getPoint()[1];
			double dy = distance*Math.sin(rotBearing);
			yloc+=dy;
			addLocation(xloc, yloc, time); //add the robot's location
			double energy1 = sre.getEnergy();
			energy.add(energy1); //add the robot's energy
			heading.add(sre.getHeadingRadians()); //add the robot's heading
			velocity.add(sre.getVelocity()); //add the robot's velocity
			System.out.println("Opponent "+sre.getName()+" updated to version "+location.size());
		}
		if (location.size()>100)
		{
			location.remove(0);
			energy.remove(0);
			heading.remove(0);
			velocity.remove(0);
		}
	}
	public void drawData(Graphics2D g)
	{
		System.out.println("Drawing Virtualbot data");
		g.setColor(Color.GREEN);
		for (int i = 0; i<location.size()-1; i++)
		{
			if (energy.get(i)>=70)
			{
				g.setColor(Color.GREEN);
			}
			else if (energy.get(i)>=50)
			{
				g.setColor(Color.YELLOW);
			}
			else if (energy.get(i)>=20)
			{
				g.setColor(Color.ORANGE);
			}
			else
			{
				g.setColor(Color.RED);
			}
			int x1 = (int) location.get(i).getPoint()[0];
			int y1 = (int) location.get(i).getPoint()[1];
			int x2 = (int) location.get(i+1).getPoint()[0];
			int y2 = (int) location.get(i+1).getPoint()[1];
			//draw a line along the robot path
			g.drawLine(x1, y1, x2, y2);
			//draw a block to represent location, size proportional to speed
			g.drawRect(x1, y1, (int) (double) velocity.get(i), (int) (double) velocity.get(i));
		}
	}
}
