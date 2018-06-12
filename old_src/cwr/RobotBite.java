package cwr;

import java.util.ArrayList;

public class RobotBite 
{
	//0 = time				[state]
	//1 = x					[state]
	//2 = y					[state]
	//3 = energy			[state]
	//4 = bearing radians 	[relative position]
	//5 = distance			[relative position]
	//6 = heading radians	[travel]
	//7 = velocity			[travel]
	String name;
	long cTime;
	double cx;
	double cy;
	cwruBase origin;
	double cEnergy;
	double cBearing_radians;
	double cDistance;
	double cHeading_radians;
	double cVelocity;
	ArrayList<Projection> projec; //forward projections for x
	
	public RobotBite(String name, long time, cwruBase self, 
			double energy, double bearing_radians, double distance,
			double heading_radians, double velocity)
	{
		this.name = name;
		cTime = time;
		origin = self;
		cEnergy = energy;
		cBearing_radians = bearing_radians;
		double myBearing = self.getHeadingRadians();
		//System.out.println("I'm going "+self.getHeadingRadians());
		double adjust_bearing = (bearing_radians+myBearing)%(2*Math.PI);
		//System.out.println("input bearing  "+(bearing_radians));
		//System.out.println("adjust bearing "+(adjust_bearing));
		//System.out.println("math bearing"+(-adjust_bearing+Math.PI/2));
		cDistance = distance;
		cHeading_radians = heading_radians;
		//System.out.println("location heading "+heading_radians);
		cVelocity = velocity;
		
		double myX = self.getX();
		double myY = self.getY();
		double math_bearing = (-adjust_bearing+Math.PI/2)%(2*Math.PI);
		//double math_heading = (-heading_radians+Math.PI/2)%(2*Math.PI);
		/*
		 *            0
		 *           90
		 *  -90 180       0 90
		 *           -90
		 *           180
		 */
		double dX = distance*Math.cos(math_bearing);
		//System.out.println("location dx:" + dX);
		double dY = distance*Math.sin(math_bearing);
		//System.out.println("location dy:" + dY);
		cx = myX+dX;
		cy = myY+dY;
	}
	public void attachProjection(ArrayList<Projection> projList)
	{
		projec = projList;
	}
}
