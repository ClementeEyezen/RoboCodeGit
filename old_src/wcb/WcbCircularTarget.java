package wcb;

import java.awt.*;

import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/*
 * This robot extends the data collection/surveyor robot DataMiner
 * Circular targeting uses fits a circle to project along the robot's path
 * uses perceived change in position/velocity, instead of measured change in position/velocity so that it takes the net movement, disregarding wide swings
 */
public class WcbCircularTarget extends WcbDataMiner //extends AdvancedRobot
{	//these variables and methods are public
	// Reasoning: trying to make sure that this is modular code, so that circular targetting could be used by any bot in package, not just extensions
	//variables set here, extends the list of DataMiner
	public static double radius[][] = new double[maxNumRobots][maxNumTurns];
	public static double centerX[][] = new double[maxNumRobots][maxNumTurns]; //center of circle as calc'd
	public static double centerY[][] = new double[maxNumRobots][maxNumTurns]; //center of circle as calc'd
	public static double projectX[][] = new double[maxNumRobots][maxNumTurns]; //projected X location
	public static double projectY[][] = new double[maxNumRobots][maxNumTurns]; //projected Y location
	public static double projectEightX[][] = new double[maxNumRobots][maxNumTurns]; //projects forward 1 full swing of radar, to test for accuracy of targeting
	public static double projectEightY[][] = new double[maxNumRobots][maxNumTurns]; //ditto
	public static double TrueGunBearing[][] = new double[maxNumRobots][maxNumTurns]; //most recent true gun bearing for robot to want to point at
	public static String robotLockName = ""; //default lock name
	public static int LorR = 1; //used to set whether the scanned, circular projected robot is turning left or right (1 = left, -1 = right)
	public static double bulletPower = 1;

	//method 2 circle finding variables
	public static double mX1 = 0; //midpoint of line coordinate
	public static double mY1 = 0; //midpoint of line coordinate
	public static double s1 = 0; //slope
	public static double mX2 = 0; //midpoint of line coordinate
	public static double mY2 = 0; //midpoint of line coordinate
	public static double s2 = 0; //midpoint of line coordinate
	public static double intersectX = 0; //intersection of perpendicular bisectors of slope
	public static double intersectY = 0; //intersection of perpendicular bisectors of slope
	public static double radial = 0; //radius of circle, mostly for onPaint

	public static double effectiveHeading; //projected net heading by circ-target
	public static double eightHeading; //projected net heading by circ-target for next 8 ticks
	public static double angle;
	public static double counter = 1;

	public void run()
	{
		while (true)
		{
			setTurnRadarRight(360);
			execute();
		}
		//see Oscill/Oscar, run method implemented there
	}
	public void startCase()
	{
		super.startCase(); //call all prior start case
		// i is the index of each robot, k is the time stamps for 
		for(int i = 0;i<maxNumRobots;i++)
		{
			for(int k = 0;k<maxNumTurns;k++)
			{ //more array default values (see more in DataMiner)
				radius[i][k] = 4*i;
				centerX[i][k] = 5*i;
				centerY[i][k] = 6*i;
			}
		}
	}
	public void onScannedRobot(ScannedRobotEvent vive)
	{
		super.onScannedRobot(vive); //store data scan from DataMiner
		for (int i = 0; i<maxNumRobots; i++)
		{
			if (robotLockName.equals(vive.getName()) || robotLockName.equals("")) 
			{ // if there is no lock name or the robot is the chosen one, continue, so it doesn't try to endlessly switch between targets
				if (vive.getName().equals(ScannedNameSet[i]))
				{
					fitCircle(X[i][0], Y[i][0], X[i][1], Y[i][1], X[i][2], Y[i][2], i); //calc and project circle
					setGunBearing(this.getX(), this.getY(), projectX[i][0], projectY[i][0], i, vive); //aim the gun
					if (Math.abs(this.getGunHeading()-TrueGunBearing[i][0])<=5 && this.getGunHeat()<=.01) //if the gun is close and ready to fire
					{
						//FIRE!!
						fire(.5);
					}
				}
			}
		}
	}
	public double turnToHeading(double sH) //returns the number of degrees to turn right to a desired heading
	{ //returns the change in heading needed for robot
		//double suggestedHeading
		double mH = this.getHeading(); //my Heading
		double dH = mH - sH; //change in Heading needed
		//filters, so that it never goes more than 360 to an angle
		//the while loops were the infinite loop problem
		if (Math.abs(dH)>360)
		{
			while (Math.abs(dH)>360)
			{
				if (dH >360)
				{
					dH = dH-360;
				}
				else if (dH<-360)
				{
					dH = dH+360;
				}
				else
				{
					break;
				}
			}
		}
		//filters, so that it never goes more than 180 to an angle
		//the while loops were the infinite loop problem
		if (Math.abs(dH)>180)
		{
			while (Math.abs(dH)>180)
			{
				if (dH >180)
				{
					dH = -(360-dH);
				}
				else if (dH<-180)
				{
					dH = -(360-dH);
				}
				else
				{
					break;
				}
			}	
		}
		return dH;
	}
	public void fitCircle(double x1, double y1, double x2, double y2, double x3, double y3, int robotIndex)
	{
		double radium = 0;  //radius
		double centerH = 0; //X
		double centerK = 0; //Y
		advanceCircleArray(robotIndex); //prepare for data capture
		if (linearTest(x1,y1,x2,y2,x3,y3)) //if the points are in a line, use linear projection
		{
			double bulletFlightTime = (distance[robotIndex][0])/(20-(3*1));
			lineProjection(X[robotIndex][0], Y[robotIndex][0], direction[robotIndex][0], speed[robotIndex][0], bulletFlightTime, robotIndex);
		}
		else
		{
			mX1 = (x1+x2)/2; //find midpoint coordinates
			mY1 = (y1+y2)/2;
			s1 = -((x1-x2)/(y1-y2)); //find slope
			mX2 = (x2+x3)/2; //repeat
			mY2 = (y2+y3)/2;
			s2 = -((x2-x3)/(y2-y3));
			//center-point by intersecting perpendicular bysectors
			intersectX = (s1*mX1-mY1-s2*mX2+mY2)/(s1-s2); // x=(-m1x1+y1+m2x2-y2)/(m2-m1)
			intersectY = s1*(intersectX-mX1)+mY1;  // y= m1*(x-x1)+y1
			radial = Math.sqrt(Math.abs((intersectX-mX1)*(intersectX-mX1)+(intersectY-mY1)*(intersectY-mY1)));
			radius[robotIndex][0] = radial;
			centerX[robotIndex][0] = intersectX;
			centerY[robotIndex][0] = intersectY;
			//robot's circle found!!
			double pi = Math.PI;
			double avSpeed = 0;
			if (getTime()<32)
			{
				avSpeed = (speed[robotIndex][0]); //if there isn't enough time for the robot to have lots of data to average, just use recent
			}
			else
			{
				avSpeed = (speed[robotIndex][0]+speed[robotIndex][1]+speed[robotIndex][2])/3; //take average speed
			}
			double sections = (2*pi*radial)/(avSpeed); //the number of sections of the circle the robot travels (totol distance/covered distance)
			LeftOrRight(robotIndex); //figure our Clockwise or CounterClockwise (misnamed)
			double projectTurnLeftperSection = (LorR*(360/sections)); //degrees per section
			double bulletFlightTime = (distance[robotIndex][0])/(20-3*bulletPower); //time for bullet to reach target
			double totalTurn = (projectTurnLeftperSection*bulletFlightTime); //degrees moved around circle in bullet flight time
			//simplify total turn
			if (totalTurn>=360) totalTurn = totalTurn-360;
			if (totalTurn<0) totalTurn = totalTurn+360;
			double eightTurn = (projectTurnLeftperSection*8);
			if(LorR == -1)
			{
				effectiveHeading = LorR*(180-(totalTurn/2))+direction[robotIndex][0];
				eightHeading = LorR*(180-(eightTurn/2))+direction[robotIndex][0];
			}
			else
			{
				effectiveHeading = LorR*((totalTurn/2))+direction[robotIndex][0];
				eightHeading = LorR*((eightTurn/2))+direction[robotIndex][0];	
			}
			double effectiveDistance = radial*Math.pow(3/4, 1/2)*2*Math.sin(rad(totalTurn/2));
			double eightDistance = radial*Math.pow(3/4, 1/2)*2*Math.sin(rad(eightTurn/2));
			double effectiveSpeed = effectiveDistance/bulletFlightTime;
			double eightSpeed = eightDistance/8;
			// end method 2
			lineProjection(X[robotIndex][0], Y[robotIndex][0], effectiveHeading, effectiveSpeed, bulletFlightTime, robotIndex);
			lineProjection(X[robotIndex][0], Y[robotIndex][0], eightHeading, eightSpeed, 8, robotIndex);
		}
	}
	public void onRobotDeath(RobotDeathEvent deadman)
	{
		robotLockName = "";
		System.out.println("locked onto no robot where no robot has gone before");
	}
	public void lineProjection(double enemyX, double enemyY, double heading, double speed, double time, int robotIndex)
	{
		double eX = enemyX;
		double eY = enemyY;
		double deltaX = speed*time*Math.cos(rad(-(heading-90)));
		double deltaY = speed*time*Math.sin(rad(-(heading-90)));
		double projectedX = eX+deltaX;
		double projectedY = eY+deltaY;
		if (time == 8)
		{
			advanceProjectEightArray(robotIndex);
			projectEightX[robotIndex][0] = projectedX;
			projectEightY[robotIndex][0] = projectedY;
		}
		else
		{
			advanceProjectArray(robotIndex);
			projectX[robotIndex][0] = projectedX;
			projectY[robotIndex][0] = projectedY;
		}
	}
	public void setGunBearing (double myX, double myY, double enemyX, double enemyY, int robotIndex, ScannedRobotEvent vive)
	{
		//calculates the angle to the other robot from self
		double deltaX = projectX[robotIndex][0]-myX;
		double deltaY = projectY[robotIndex][0]-myY;
		if (deltaX<=0)
		{
			if (deltaY<=0)
			{
				angle = 180+(90-(deg(Math.atan(Math.abs(deltaY/deltaX)))));
			}
			else
			{
				angle = 180+(90+(deg(Math.atan(Math.abs(deltaY/deltaX)))));
			}
		}
		else
		{
			if (deltaY<=0)
			{
				angle = 180-90+(deg(Math.atan(Math.abs(deltaY/deltaX))));
			}
			else
			{
				angle = 180-90-(deg(Math.atan(Math.abs(deltaY/deltaX))));	
			}
		}
		//System.out.println("angle! = "+angle);
		double currentGun = this.getGunHeading();
		if(currentGun>180) currentGun = currentGun-360;
		if(currentGun>180) currentGun = currentGun-360;
		double suggestedGunTurnLeft = currentGun-angle;
		//reduction of the angle to within a full turn
		int count =0;
		while (Math.abs(suggestedGunTurnLeft)>360)
		{
			count = count+1;
			if (suggestedGunTurnLeft>=360) suggestedGunTurnLeft = suggestedGunTurnLeft-360;
			if (suggestedGunTurnLeft<=-360) suggestedGunTurnLeft = suggestedGunTurnLeft+360;
			System.out.println("360count "+count);
			if (count>4) break;
		}
		//reduction of the angle to no more than a half turn
		count = 0;
		while (Math.abs(suggestedGunTurnLeft)>180)
		{
			count = count+1;
			if (suggestedGunTurnLeft>180) suggestedGunTurnLeft = 180-suggestedGunTurnLeft;
			if (suggestedGunTurnLeft<-180) suggestedGunTurnLeft = 360+suggestedGunTurnLeft;
			System.out.println("180count "+count);
			if (count>4) break;
		}
		advanceGunBearingArray(robotIndex); //prepare to input data
		TrueGunBearing[robotIndex][0] = suggestedGunTurnLeft+this.getGunHeading(); //set the desired heading
		setTurnGunLeft(suggestedGunTurnLeft); //move gun to desired heading
	}
	public void LeftOrRight(int robotIndex) 
	{ //determine clockwise or counterclock, actually the wrong name
		if (X[robotIndex][0]!=X[robotIndex][1] && X[robotIndex][0]!=X[robotIndex][2])
		{
		//compares differnce in the angle of heading as time passes
			double angle1 = direction[robotIndex][0];
			double angle2 = direction[robotIndex][1];
			double difference = (angle2-angle1);
			if (difference<0) LorR = 1;
			if (difference>0) LorR = -1;
			if (difference>180) LorR = 1;
			if (difference<-180) LorR = -1;
		}
		else
		{
			LorR = LorR+0;
		}
	}
	public boolean linearTest(double x1, double y1, double x2, double y2,	double x3, double y3) 
	{
		//false = non-linear
		//check to see if a line is linear by same slope
		if (y1!=y2 && y1!=y3)
		{
			double slope1 = ((x1-x2)/(y1-y2));
			double slope2 = ((x1-x3)/(y1-y3));
			double angle1 = deg(Math.atan(rad(slope1)));
			double angle2 = deg(Math.atan(rad(slope2)));
			double difference = Math.abs(angle1-angle2);
			return (difference<.005);
		}
		else if (y1==y2 && y1==y3)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void advanceCircleArray(int robotIndex) 
	{ //advance array in order to add more data
		for (int k = maxNumTurns-1;k>0;k--)
		{
			radius[robotIndex][k]= radius[robotIndex][k-1];
			centerX[robotIndex][k] = centerX[robotIndex][k-1];
			centerY[robotIndex][k] = centerY[robotIndex][k-1];
		}
	}
	public void advanceProjectArray(int robotIndex) 
	{ //advance array in order to add more data
		for (int k = maxNumTurns-1;k>0;k--)
		{
			projectX[robotIndex][k]= projectX[robotIndex][k-1];
			projectY[robotIndex][k] = projectY[robotIndex][k-1];
		}
	}
	public void advanceProjectEightArray(int robotIndex) 
	{ //advance array in order to add more data
		for (int k = maxNumTurns-1;k>0;k--)
		{
			projectEightX[robotIndex][k] = projectEightX[robotIndex][k-1];
			projectEightY[robotIndex][k] = projectEightY[robotIndex][k-1];
		}
	}
	public void advanceGunBearingArray(int robotIndex) 
	{ //advance array in order to add more data
		for (int k = maxNumTurns-1;k>0;k--)
		{
			TrueGunBearing[robotIndex][k]= TrueGunBearing[robotIndex][k-1];
		}
	}
	public void onPaint(Graphics2D painted)
	{ //advance array in order to add more data
		super.onPaint(painted);
		int squareZise = 5;
		for (int i = 0;i<maxNumRobots;i++)
		{
			if (!ScannedNameSet[i].equals("")) //accounts for blank space
			{
				//sets a unique color, hopefully
				if (i<10) 
				{
					painted.setColor(paintAssist[i]);
				}
				// draws a small box at last X,Y position
				painted.fillRect((int) (X[i][0]), (int) (Y[i][0]), squareZise, squareZise);
				// draws a circle at centerX,center Y with diameter 2*radius
				painted.setColor(java.awt.Color.CYAN);
				painted.drawOval((int)(intersectX-radial), (int)(intersectY-radial), (int) (2*radial), (int) (2*radial));
				painted.setColor(java.awt.Color.ORANGE); //actual projection of location when bullet arrives
				painted.fillRect((int) (projectX[i][0]), (int) (projectY[i][0]), squareZise, squareZise);
				painted.drawLine((int) (X[i][0]), (int) (Y[i][0]), (int) (projectX[i][0]), (int) (projectY[i][0]));
				painted.setColor(java.awt.Color.GREEN); //8 step projection (next radar, for accuracy of guess)
				painted.fillRect((int) (projectEightX[i][0]), (int) (projectEightY[i][0]), squareZise, squareZise);
				painted.drawLine((int) (X[i][0]), (int) (Y[i][0]), (int) (projectEightX[i][0]), (int) (projectEightY[i][0]));
				painted.setColor((java.awt.Color.RED));
				//draws a line to the projected target location
				painted.drawLine((int) this.getX(), (int) this.getY(), (int) projectX[i][0], (int) projectY[i][0]); //draw my desired targetting line
			}
		}
	}
}
