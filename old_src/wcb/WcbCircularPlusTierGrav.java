package wcb;

import java.awt.*;

import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/*
 * This robot extends the data collection/surveyor robot DataMiner
 * TODO the goals for this robot are circular targeting
 * Circular targeting uses true circle fitting to project along a curve
 */
public class WcbCircularPlusTierGrav extends WcbDataMiner //extends AdvancedRobot
{
	//variables set here, take them from
	public static double radius[][] = new double[maxNumRobots][maxNumTurns];
	public static double centerX[][] = new double[maxNumRobots][maxNumTurns];
	public static double centerY[][] = new double[maxNumRobots][maxNumTurns];
	public static double projectX[][] = new double[maxNumRobots][maxNumTurns];
	public static double projectY[][] = new double[maxNumRobots][maxNumTurns];
	public static double projectEightX[][] = new double[maxNumRobots][maxNumTurns]; //projects forward 1 full swing of radar, to test for accuracy of targeting
	public static double projectEightY[][] = new double[maxNumRobots][maxNumTurns]; //ditto
	public static double TrueGunBearing[][] = new double[maxNumRobots][maxNumTurns];
	public static String robotLockName = "";
	public static int LorR = 1; //used to set whether the scanned robot is turning left or right (1 = left, -1 = right)
	public static double bulletPower = 1;
	public static double rSquared[][] = new double[maxNumRobots][maxNumTurns];
	public static int buffer = 100;

	//method 2 circle finding variables
	public static double mX1 = 0; //midpoint
	public static double mY1 = 0;
	public static double s1 = 0; //slope
	public static double mX2 = 0;
	public static double mY2 = 0;
	public static double s2 = 0;
	public static double intersectX = 0;
	public static double intersectY = 0;
	public static double radial = 0;

	public static double effectiveHeading;
	public static double eightHeading;
	public static double angle;

	public void run()
	{
		startCase();
		while(true)
		{
			setTurnRadarRight(360); //turns radar, runs exclusively out of onScannedRobot
			antiGravMove();
			execute();
		}
	}
	private void antiGravMove() 
	{
		double MassX = 0;
		double MassY = 0;
		double pushAngle = 0;
		double myAngle = this.getHeading();
		double deltaAngle = 0;
		//use 1/distance for walls, negative to push it away from the walls
		double xin = getX();
		double width = getBattleFieldWidth();
		double yin = getY();
		double height = getBattleFieldHeight();
		System.out.println("width "+width+" height "+height);
		System.out.println("(xin,yin) ("+xin+","+yin+")");
		double dFLBoundry = xin-buffer;
		double dFRBoundry = width-(xin+buffer);
		double wallPushLeft = 1000*1/(dFLBoundry);
		double wallPushRight = 1000*-1/(dFRBoundry);
		MassX = wallPushLeft+wallPushRight;
		double dFBBoundry = yin-buffer;
		double dFTBoundry = height-(yin+buffer);
		double wallPushBottom = 1000*1/(dFBBoundry);
		double wallPushTop = 1000*-1/(dFTBoundry);
		MassY = wallPushBottom+wallPushTop;
		
		System.out.println("X "+MassX);
		System.out.println("Y "+MassY);
		System.out.println("refactoring a rotated and flipped space time continuum");
		pushAngle = 0;
		if (MassX==0 || MassY==0)
		{
			System.out.println("something = 0");
			if (MassX==0 && MassY==0)
			{
				pushAngle = 0;
				System.out.println("both = 0");
			}
			else if (MassX == 0)
			{
				pushAngle = (MassY/Math.abs(MassY))*90-90;
				System.out.println("X = 0");
			}
			else
			{
				pushAngle = (MassX/Math.abs(MassX))*90;
				System.out.println("Y = 0");
			}
		}
		else if (dFLBoundry<0 || dFLBoundry<buffer)// || dFLBoundry<2*buffer)
		{
			pushAngle = 90;
			if (dFTBoundry<0 || dFTBoundry<buffer )//|| dFTBoundry<2*buffer)
			{
				pushAngle = 135;
			}
			if (dFLBoundry<0 || dFLBoundry<buffer )//|| dFBBoundry<2*buffer)
			{
				pushAngle = 45;
			}
		}
		else if (dFRBoundry<0 || dFRBoundry<buffer )//|| dFRBoundry<2*buffer)
		{
			pushAngle = 270;
			if (dFTBoundry<0 || dFTBoundry<buffer )//|| dFTBoundry<2*buffer)
			{
				pushAngle = 225;
			}
			if (dFLBoundry<0 || dFLBoundry<buffer )//|| dFBBoundry<2*buffer)
			{
				pushAngle = 315;
			}
		}
		else if (dFBBoundry<0 || dFBBoundry<buffer )//|| dFBBoundry<2*buffer)
		{
			pushAngle = 0;
		}
		else if (dFTBoundry<0 || dFTBoundry<buffer )//|| dFTBoundry<2*buffer)
		{
			pushAngle = 180;
		}
		if (pushAngle<0)
		{
			pushAngle=pushAngle+360;
		}
		System.out.println("pushAngle \n"+pushAngle);
		System.out.println("X force "+MassX+" Y force "+MassY+" desired angle of travel "+pushAngle+" myAngle "+myAngle);
		deltaAngle = myAngle-pushAngle;
		int count = 0;
		while (Math.abs(deltaAngle)>=360)
		{
			count++;
			if (deltaAngle>360) deltaAngle = deltaAngle-360;
			if (deltaAngle<-360) deltaAngle = deltaAngle+360;
			if (count>5) break;
		}
		count = 0;
		while (Math.abs(deltaAngle)>=180)
		{
			count++;
			if (deltaAngle>180) deltaAngle = 180-deltaAngle;
			if (deltaAngle<-180) deltaAngle = 180+deltaAngle;
			if (count>5) break;
		}
		System.out.println("distance to desire"+deltaAngle);
		setTurnLeft(deltaAngle);
		setAhead(Math.random()*4+4);
	}
	public void startCase()
	{
		super.startCase();
		// i is the index of each robot, k is the time stamps for 
		for(int i = 0;i<maxNumRobots;i++)
		{
			for(int k = 0;k<maxNumTurns;k++)
			{
				radius[i][k] = 4*i;
				centerX[i][k] = 5*i;
				centerY[i][k] = 6*i;
				//rSquared[i][k] = 1;

			}
		}
	}
	public void onScannedRobot(ScannedRobotEvent vive)
	{
		super.onScannedRobot(vive);
		for (int i = 0; i<maxNumRobots; i++)
		{
			if (robotLockName.equals(vive.getName()) || robotLockName.equals("")) // if there is no lock or the robot is the chosen one, continue
			{
				if (vive.getName().equals(ScannedNameSet[i]))
				{
					// fitCircle sets a TrueGunBearing (DONE!)
					fitCircle(X[i][0], Y[i][0], X[i][1], Y[i][1], X[i][2], Y[i][2], i);
					//advanceRSquaredArray(i);
					//rSquared[i][0] = -(Math.abs((X[i][0]-projectEightX[i][1])/8)+Math.abs((Y[i][0]-projectY[i][1])/8))/(2*800);
					//System.out.println("rSq = "+rSquared[i][0]);
					setGunBearing(this.getX(), this.getY(), projectX[i][0], projectY[i][0], i, vive);
				//	System.out.println("firing angle ="+TrueGunBearing[i][0]+" gun angle"+this.getGunHeading()+
				//			" delta"+(this.getGunHeading()-TrueGunBearing[i][0]));
				//	System.out.println("gun Heat??"+this.getGunHeat());
					if (Math.abs(this.getGunHeading()-TrueGunBearing[i][0])<=5 && this.getGunHeat()<=.001) setFire(2);
					// TODO if(!gunClose()) setFire(); // gunClose() tests to see if gun is close to suggested bearing, then when it is, 
					// 								the loop breaks, the gun fires
					setRobotLock(vive.getName(),vive); //makes it so that it only tries to find and shoot at one robot, unless that robot dies
					setTurnRight((TrueGunBearing[i][0]%90+this.getHeading()));
				}
			}
		}
	}
	public void setRobotLock(String name, ScannedRobotEvent vibe) 
	{
		/*if (vibe.getEnergy()==0) //test for death
		{
			robotLockName = "";
		}
		else */
		{
			robotLockName = name;
		}
		//System.out.println("Locked on to robot "+name);
	}
	public void fitCircle(double x1, double y1, double x2, double y2, double x3, double y3, int robotIndex)
	{
		double radium = 0;  //radius
		double centerH = 0; //X
		double centerK = 0; //Y
		//if (linearTest(x1, y1, x2, y2, x3, y3))
		/*{
			double bulletFlightTime = 20-bulletPower;
			//System.out.println("Linear estimation");
			//System.out.print("inputs"+"\n"+":x1:"+x1+":y1:"+y1+":direction:"+direction[robotIndex][0]+":speed:"+speed[robotIndex][0]+":bFT:"+bulletFlightTime+"\n");
			lineProjection(x1, y1, direction[robotIndex][0], speed[robotIndex][0], bulletFlightTime, robotIndex);
			lineProjection(x1, y1, direction[robotIndex][0], speed[robotIndex][0], 8, robotIndex);
		} */
		/*
		 * 
		 */
		//else
		{
			advanceCircleArray(robotIndex);
			//System.out.println("not linear confirmed");
			//System.out.println("ax"+x1+"ay"+y1+"bx"+x2+"by"+y2+"Dx"+x3+"Dy"+y3);
			//method 1	
			/*
			double zy = y1+y2;
			double zx = x1+x2;
			double wy = y1+y3;
			double wx = x1+x3;
			double v1 = x1*x1+y1*y1-x3*x3-y3*y3;
			double v2 = x1*x1+y1*y1-x2*x2-y2*y2;
			centerK = (zx*v1-wx*v2)/(-zy*wx-wy*zx);
			centerH = (.5*v2-zy*centerK)/(zx);
			radium = Math.sqrt((centerH-x1)*(centerH-x1)+((centerK-y1))*(centerK-y1));
			//System.out.println("predicted center ("+centerH+","+centerK+") with radius "+radium);
			advanceCircleArray(robotIndex);
			radius[robotIndex][0] = radium;
			centerX[robotIndex][0] = centerH;
			centerY[robotIndex][0] = centerK;
			//System.out.println("robot's circle found!!");
			double pi = Math.PI;
			double avSpeed = 0;
			if (getTime()<32)
			{
				avSpeed = (speed[robotIndex][0]);
			}
			else
			{
				avSpeed = (speed[robotIndex][0]*speed[robotIndex][1]*speed[robotIndex][2]);
			}
			double sections = (2*pi*radius[robotIndex][0])/(avSpeed); //the number of sections of the circle the robot travels (totol distance/covered distance)
			LeftOrRight(robotIndex);
			double projectTurnLeftperSection = (LorR*(360/sections));
			double bulletFlightTime = (distance[robotIndex][0])*(20-bulletPower);
			double totalTurn = projectTurnLeftperSection*bulletFlightTime;
			double eightTurn = projectTurnLeftperSection*8;
			double effectiveHeading = -(totalTurn/2)+direction[robotIndex][0];
			double eightHeading = -(eightTurn/2)+direction[robotIndex][0];
			double effectiveDistance = 2*deg(Math.sin(rad(totalTurn/2)));
			double eightDistance = 2*deg(Math.sin(rad(eightTurn/2)));
			double effectiveSpeed = effectiveDistance/bulletFlightTime;
			double eightSpeed = eightDistance/8;
			 */
			// begin method 2
			mX1 = (x1+x2)/2; //midpoint
			mY1 = (y1+y2)/2;
			s1 = -((x1-x2)/(y1-y2)); //slope
			mX2 = (x2+x3)/2;
			mY2 = (y2+y3)/2;
			s2 = -((x2-x3)/(y2-y3));
			//center-point by intersecting perpendicular bysectors
			intersectX = (s1*mX1-mY1-s2*mX2+mY2)/(s1-s2); // x=(-m1x1+y1+m2x2-y2)/(m2-m1)
			intersectY = s1*(intersectX-mX1)+mY1;  // y= m1*(x-x1)+y1
			radial = Math.sqrt(Math.abs((intersectX-mX1)*(intersectX-mX1)+(intersectY-mY1)*(intersectY-mY1)));
			//System.out.println("X"+intersectX+"Y"+intersectY+"radius"+radial);
			radius[robotIndex][0] = radial;
			centerX[robotIndex][0] = intersectX;
			centerY[robotIndex][0] = intersectY;
			//System.out.println("robot's circle found!!");
			double pi = Math.PI;
			double avSpeed = 0;
			if (getTime()<32)
			{
				avSpeed = (speed[robotIndex][0]);
			}
			else
			{
				avSpeed = (speed[robotIndex][0]+speed[robotIndex][1]+speed[robotIndex][2])/3;
			}
			double sections = (2*pi*radial)/(avSpeed); //the number of sections of the circle the robot travels (totol distance/covered distance)
			//System.out.println("section = "+sections+" ("+radial+","+avSpeed+")");
			LeftOrRight(robotIndex);
			double projectTurnLeftperSection = (LorR*(360/sections));
			//System.out.println("projectTurn"+projectTurnLeftperSection);
			double bulletFlightTime = (distance[robotIndex][0])/(20-3*bulletPower);
			//System.out.println("bFT calc "+bulletFlightTime);
			double totalTurn = (projectTurnLeftperSection*bulletFlightTime);
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
			//System.out.println("bulletFlightTime"+bulletFlightTime+" totalTurn"+totalTurn+" current heading"+direction[robotIndex][0]);
			//System.out.println("effective Heading"+effectiveHeading+" effectiveDistance"+effectiveDistance+" effectiveSpeed"+effectiveSpeed);
			//System.out.println("Circular estimation");
			//System.out.print("inputs"+"\n"+":x1:"+x1+":y1:"+y1+":direction:"+direction[robotIndex][0]+":speed:"+speed[robotIndex][0]+":bFT:"+bulletFlightTime+"\n");
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
		advanceGunBearingArray(robotIndex);
		TrueGunBearing[robotIndex][0] = suggestedGunTurnLeft+this.getGunHeading();
		setTurnGunLeft(suggestedGunTurnLeft);
		//System.out.println("enemy bearing "+angle+" my gun "+ currentGun+" gun turn"+suggestedGunTurnLeft);
	}
	public void LeftOrRight(int robotIndex) 
	{
		if (X[robotIndex][0]!=X[robotIndex][1] && X[robotIndex][0]!=X[robotIndex][2])
		{
			double angle1 = direction[robotIndex][0];
			double angle2 = direction[robotIndex][1];
			//System.out.println("1: "+angle1+" 2: "+angle2);
			double difference = (angle2-angle1);
			//System.out.println("difference ="+difference);
			//R (CW) is 1, L (CCW) is -1
			if (difference<0) LorR = 1;
			if (difference>0) LorR = -1;
			if (difference>180) LorR = 1;
			if (difference<-180) LorR = -1;
			//System.out.println("LorR"+LorR);
		}
		else
		{
			LorR = LorR+0;
		}
	}
	public boolean linearTest(double x1, double y1, double x2, double y2,	double x3, double y3) 
	{
		//false = non-linear
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
	{
		for (int k = maxNumTurns-1;k>0;k--)
		{
			radius[robotIndex][k]= radius[robotIndex][k-1];
			centerX[robotIndex][k] = centerX[robotIndex][k-1];
			centerY[robotIndex][k] = centerY[robotIndex][k-1];
		}
	}
	public void advanceProjectArray(int robotIndex) 
	{
		for (int k = maxNumTurns-1;k>0;k--)
		{
			projectX[robotIndex][k]= projectX[robotIndex][k-1];
			projectY[robotIndex][k] = projectY[robotIndex][k-1];
		}
	}
	public void advanceProjectEightArray(int robotIndex) 
	{
		for (int k = maxNumTurns-1;k>0;k--)
		{
			projectEightX[robotIndex][k] = projectEightX[robotIndex][k-1];
			projectEightY[robotIndex][k] = projectEightY[robotIndex][k-1];
		}
	}
	public void advanceGunBearingArray(int robotIndex) 
	{
		for (int k = maxNumTurns-1;k>0;k--)
		{
			TrueGunBearing[robotIndex][k]= TrueGunBearing[robotIndex][k-1];
		}
	}
	public void advanceRSquaredArray(int robotIndex) 
	{
		for (int k = maxNumTurns-1;k>0;k--)
		{
			rSquared[robotIndex][k]= rSquared[robotIndex][k-1];
		}
	}
	public void onPaint(Graphics2D painted)
	{
		super.onPaint(painted);
		//draw estimating lines
		//vertical
		painted.setColor(java.awt.Color.CYAN);
		painted.drawLine(buffer, buffer, buffer, 600-buffer);
		//painted.drawLine(200, 0, 200, 600);
		//painted.drawLine(300, 0, 300, 600);
		//painted.drawLine(400, 0, 400, 600);
		//painted.drawLine(500, 0, 500, 600);
		//painted.drawLine(600, 0, 600, 600);
		painted.drawLine(800-buffer, buffer, 800-buffer, 600-buffer);
		//horizontal
		painted.drawLine(buffer, buffer, 800-buffer, buffer);
		//painted.drawLine(0, 200, 800, 200);
		//painted.drawLine(0, 300, 800, 300);
		//painted.drawLine(0, 400, 800, 400);
		painted.drawLine(buffer, 600-buffer, 800-buffer, 600-buffer);
		
		//method 2 visual debug
		/*painted.setColor(java.awt.Color.BLACK);
		painted.drawLine((int) (mX1-100),(int) (mY1-100*s1),(int) (mX1+100),(int) (mY1+100*s1));
		painted.drawLine((int) (mX2-100),(int) (mY2-100*s2),(int) (mX2+100),(int) (mY2+100*s2));
		painted.fillRect((int) intersectX, (int) intersectY, 5, 5);
		 */
		int squareZise = 5;
		for (int i = 0;i<maxNumRobots;i++)
		{
			if (!ScannedNameSet[i].equals("")) //accounts for blank space
			{
				//sets a unique color, hopefully
				if (i<10) 
				{
					painted.setColor(paintAssist[i]);
					//	System.out.println("paint color set to "+paintAssist[i]+" for robotIndex "+i);
				}
				// draws a small box at last X,Y position
				painted.fillRect((int) (X[i][0]), (int) (Y[i][0]), squareZise, squareZise);
				// draws a circle at centerX,center Y with diameter 2*radius
				painted.setColor(java.awt.Color.CYAN);
				painted.drawOval((int)(intersectX-radial), (int)(intersectY-radial), (int) (2*radial), (int) (2*radial));
				//draws a line to the projected target location
				painted.setColor(java.awt.Color.ORANGE); //actual projection
				painted.fillRect((int) (projectX[i][0]), (int) (projectY[i][0]), squareZise, squareZise);
				painted.drawLine((int) (X[i][0]), (int) (Y[i][0]), (int) (projectX[i][0]), (int) (projectY[i][0]));
				painted.setColor(java.awt.Color.GREEN); //8 step projection (next radar, for accuracy of guess)
				painted.fillRect((int) (projectEightX[i][0]), (int) (projectEightY[i][0]), squareZise, squareZise);
				painted.drawLine((int) (X[i][0]), (int) (Y[i][0]), (int) (projectEightX[i][0]), (int) (projectEightY[i][0]));
				painted.setColor((java.awt.Color.RED));
				painted.drawLine((int) this.getX(), (int) this.getY(), (int) projectX[i][0], (int) projectY[i][0]);
			}
		}
	}
}
