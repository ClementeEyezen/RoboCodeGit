package cwru;

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
	
	public RobotBite(long time, cwruBase self, double energy, double bearing_radians, double distance,
			double heading_radians, double velocity)
	{
		//convert all the above data to arraylists of data
		cTime = time;
		origin = self;
		cEnergy = energy;
		cBearing_radians = bearing_radians;
		cDistance = distance;
		cHeading_radians = heading_radians;
		cVelocity = velocity;
		
		double myX = self.getX();
		double myY = self.getY();
		double math_bearing = (-bearing_radians+Math.PI/2)%(2*Math.PI);
		//double math_heading = (-heading_radians+Math.PI/2)%(2*Math.PI);
		/*
		 *            0
		 *           90
		 *  -90 180       0 90
		 *           -90
		 *           180
		 */
		double dX = velocity*Math.cos(math_bearing);
		double dY = velocity*Math.sin(math_bearing);
		cx = myX+dX;
		cy = myY+dY;
	}
	public void attachProjection(ArrayList<Projection> projList)
	{
		
	}
}
