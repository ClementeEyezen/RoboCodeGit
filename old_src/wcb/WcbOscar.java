package wcb;

import java.awt.Graphics2D;

import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;

/*
 * this robot started out as a basic oscillating bot, that is now the prototype for minimum risk movement
 * Its choice have become independent of the on scanned robot method, allowing for a more diverse option set
 * it also prevents data spikes on a single or multiple scanned robots
 * It just records (mostly double) values in arrays on scanned
 * then in its run method, it calls the various brain methods, which look at stored data to make decisions
 */

/*
 * credit where credit is due
 * David Silin - debugging assistance
 * RoboWiki - general ideas, no direct code
 * 		name for circular targetting, not even implementation style
 * 		name for minimum risk/wave movement styles, but not implementation
 * RoboWiki - http://robowiki.net/wiki/Oscillator_Movement (back when the robot used oscillations to move in a star pattern, no longer used in any form)
 * 		code was taken directly from "Direct" subheading at one point
 * RoboWiki - http://robowiki.net/wiki/Robocode/Graphical_Debugging#Graphical_Debugging
 * 		code was taken directly from "Simple Example" subheading at one point, but is no longer specifically used
 * other than that, all code is my own
 */

public class WcbOscar extends WcbCircularTarget
{
	private static double masterKey[][] = new double[6][maxNumTurns];//X1,Y1,X2,Y2,RadiusMin,RadiusMax //data storage for wave circles
	private static double testCard[][] = new double [36][4]; //degrees and radius and x and y coordinates of the test locations, relative to mY
	private static double testScore[] = new double [36]; //counts of bad happenings for each node around the robot
	private static int minVal; //min val for movenment
	private static int minLoc; //testCard ID for min risk movement
	double maxVb = 19.7; //bullet max velocity
	double minVb = 11;  //bullet min velocity

	public void run()
	{
		setAhead(8);
		execute();
		startCase();
		while (true)
		{
		// the 1 through 8 printouts were for determining where the loop would hang (determined at execute)
			System.out.println("1");
			setTurnRadarRight(360);
			System.out.println("2");
			setAhead(8);
			System.out.println("3");
			setBulletCircles(); //look for energy drops from bullets and add circles to the data collection if they are found
			System.out.println("4");
			adjustBulletCircles(); //increment the data storage to increase the areas where bullets could be
			System.out.println("5");
			returnTestSpaces(); //test spaces around the robot for minimum risk movement
			System.out.println("6");
			chooseDayChoice(); //tactical next turn move, based on minimum risk movement, looking at nearspace
			//returnTestLarge(); //tests the node space across the entire field, not used
			//chooseLifeChoice(); //strategic nodespace calculation that covers the entire plane, not used
			System.out.println("7");
			execute();
			System.out.println("8");
		}
	}
	public void startCase()// unable to make a privative inherited method
	{ //extension of prior pseudo-constructor methods
		super.startCase();
		for (int i = 0;i<maxNumTurns;i++)
		{
			masterKey[0][i] = 0;
			masterKey[1][i] = 0;
			masterKey[2][i] = 0;
			masterKey[3][i] = 0;
			masterKey[4][i] = 0;
			masterKey[5][i] = 0;
		}
		for (int i = 0;i<36;i++)
		{
			testCard[i][0] = (i*10);//degrees angle, not rotated
			System.out.println("degrees for "+i+" = "+testCard[i][0]);
			testCard[i][1] = 64;  //radius
			testCard[i][2] = testCard[i][1]*Math.cos(deg(-(testCard[i][0]+90)));
			testCard[i][3] = testCard[i][1]*Math.sin(deg(-(testCard[i][0]+90)));
		}
		setAhead(250);
		setTurnRight(this.getHeading()%90);
	}
	private void setBulletCircles()
	{
		//run through all energy drops
		for (int i = 0; i<maxNumRobots;i++)
		{
			if (energyDrop[i]>.1 && energyDrop[i]<3)
			{
				advanceMasterKey();
				masterKey[0][0] = X[i][1];//prior scanned X,Y
				masterKey[1][0] = Y[i][1];
				masterKey[2][0] = X[i][0];//most recent scanned X,Y
				masterKey[3][0] = Y[i][0];
				masterKey[4][0] = 0; //min radius, just fired
				masterKey[5][0] = (time[i][0]-time[i][1])*19.7;//max radius
			}
		}
	}
	private void adjustBulletCircles()
	{ //adjust for  new data to add
		for (int i = 0;i<maxNumTurns;i++)
		{
			masterKey[4][i] = masterKey[4][i]+minVb;
			masterKey[5][i] = masterKey[5][i]+maxVb;
		}
	}
	private void returnTestSpaces()
	{ //set scores for each node point near the robot in a circle
		for (int k = 0;k<36;k++)
		{//reset scores
			testScore[k] = 0;
		}
		double mX = this.getX();
		double mY = this.getY();
		for (int j = 0;j<maxNumTurns;j++)
		{//run through each wave
			if (masterKey[4][j]<1000)//if the radius is out of logical bounds (about >1000 or so), ignore
			{
				for (int i = 0;i<36;i++)
				{//run through the test spaces
					double testX = mX+testCard[i][2];
					double testY = mY+testCard[i][3];
					double testD = distancePtoP(testX,testY,(masterKey[0][j]+masterKey[2][j])/2,(masterKey[1][j]+masterKey[3][j])/2);
					if (testD<masterKey[5][j] && testD>masterKey[4][j])
					{
						testScore[i] = testScore[i]+1;
						//sum the total number of waves washing over each node at a given time
					}
				}
			}
		}
		for (int robot = 0;robot<maxNumRobots;robot++)
		{//run through opposing robots
			for (int l = 0;l<36;l++)
			{//run through the test spaces, looking for other robots
				double testX = mX+testCard[l][2];
				double testY = mY+testCard[l][3];
				double testD = distancePtoP(testX,testY,(X[robot][0]),Y[robot][0]);
				if (testD<64)
				{
					testScore[l] = testScore[l]+2;
					//sum the total number of robots within critical range, i.e. could hit within the next 8 turns before it is scanned again
					//weights robots more heavily than bullets, in order to avoid them more stubbornly
				}
			}
		}
		for (int l = 0;l<36;l++)
		{//run through the test spaces, this time looking for walls
			double testX = mX+testCard[l][2];
			double testY = mY+testCard[l][3];
			if (testX<18 || testY<18 || testX>(this.getBattleFieldWidth()-18) || testX>(this.getBattleFieldHeight()-18)) //if the tested point is out of bounds
			{
				testScore[l] = testScore[l]+10000;
				//sum the total number of robots within critical range, i.e. could hit within the next 8 turns before it is scanned again
				//weights walls as don't run into this, go away from here
			}
		}

	}
	private void returnTestLarge()
	{	//not officially used due to looping problems
		//runs the same as returnTestSpace, but for node space across field
		//sets the score value for each node
	}
	private void chooseDayChoice()
	{ //choose between multiple spaces in nearspace of robot
		//start at 0, then count around, looking for the lowest position, then drive to that position
		minLoc = 0;
		minVal = 1000000;
		for (int i = 0;i<36;i++)
		{
		//	System.out.println("I'm in the first choice for loop");
			if (testScore[i]<minVal)
			{
				minLoc = i;
				minVal = (int) (testScore[i]);
				System.out.println("I've found a lower score");
			}
		}
		double minDeg = minLoc*10;
		double turnL = turnToHeading(minDeg);
		setTurnLeft(turnL);
	}
	private void chooseLifeChoice()
	{ //not officially used due to looping problems
		//used when there are multiple spaces of minimum and equal value near the robot
		//picks the most desirable node, doesn't actually set a direction
		//once it is chosen, and there are equal opportunity points, it chooses the one closest to the bearing from lifeChoice
		
	}
	public void onScannedRobot(ScannedRobotEvent vive)// unable to make a privative inherited method
	{
		super.onScannedRobot(vive); //nothing new
	}
	private void advanceMasterKey()
	{ //prepare for new circle data
		for (int i = maxNumTurns-1;i>0;i--)
		{
			double zero = masterKey[0][i-1];
			double one = masterKey[1][i-1];
			double two = masterKey[2][i-1];
			double three = masterKey[3][i-1];
			double four = masterKey[4][i-1];
			double five = masterKey[5][i-1];
			masterKey[0][i] = zero;
			masterKey[1][i] = one;
			masterKey[2][i] = two;
			masterKey[3][i] = three;
			masterKey[4][i] = four;
			masterKey[5][i] = five;
		}
	}
	private double distancePtoP(double X1, double Y1, double X2, double Y2) //returns distance between two point sets
	{ //calculates distance from one point to the other
		double dX = Math.abs(X2-X1);
		double dY = Math.abs(Y2-Y1);
		double distance = Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2));
		return distance;
	}
	private double moveToPoint (double tX, double tY)
	{	//returns the desired movement direction for self to the given points
		//double targetX, targetY
		double mX = this.getX();
		double mY = this.getY();
		double dX = tX-mX; //deltaX, change in X
		double dY = tY-mY; //deltaY, change in Y
		double sA = 0; //suggestedAngle
		if (dX == 0) //account for 0 case
		{
			if (dY< 0)
			{
				sA = 180;
			}
			else
			{
				sA = 0;
			}
		}
		else
		{
			double dRatio = dY/dX; //input into atan func
			double tA = deg(Math.atan(dRatio)); //true angle
			double rA = -(tA-90); //rotated, realigned angle for roboCode
			sA = rA;
		}
		if (dX<0)
		{
			sA = sA+180;
		}
		return sA;
	}
	public void onSkippedEvent (SkippedTurnEvent darn)
	{ //looking to find information on what gets skipped
		long skippedEvent = darn.getSkippedTurn();
		int priority = darn.getPriority();
		long timeSkipped = darn.getTime();
		System.out.println("skippedEvent "+skippedEvent+" with priority "+priority+" at time "+timeSkipped);
		execute(); //attempt to stop skipping by executing what I have calculated
	}
	public void onPaint(Graphics2D painter)
	{
		super.onPaint(painter); //prior paint events
		for (int j = 0; j<maxNumRobots; j++)
		{ //stoplight on robot to indicate that they have fired
			if (energyDrop[j]>0) painter.setColor(java.awt.Color.GREEN);
			else painter.setColor(java.awt.Color.RED);
			painter.fillRect((int) X[j][0],(int) Y[j][0],10,10);
		}
		painter.setColor(java.awt.Color.CYAN);
		//this next chunk prints out all of the waves, and makes robocode trippy
		for (int i = 0; i<maxNumTurns; i++)
		{
			painter.setColor(paintAssist[i%maxNumRobots]);
			//print the different circles for min and max location, min and max radius (only max move, max radius not commented out, to make it easy to visualize
			painter.drawOval((int) (masterKey[0][i]-masterKey[5][i]), (int) (masterKey[1][i]-masterKey[5][i]), (int) (2*masterKey[5][i]), (int) (2*masterKey[5][i]));
			//uncomment out to see how the robot actually thinks (looking at each wave position
		//	painter.drawOval((int) (masterKey[2][i]-masterKey[5][i]), (int) (masterKey[3][i]-masterKey[5][i]), (int) (2*masterKey[5][i]), (int) (2*masterKey[5][i]));
		//	painter.drawOval((int) (masterKey[0][i]-masterKey[4][i]), (int) (masterKey[1][i]-masterKey[4][i]), (int) (2*masterKey[4][i]), (int) (2*masterKey[4][i]));
		//	painter.drawOval((int) (masterKey[2][i]-masterKey[4][i]), (int) (masterKey[3][i]-masterKey[4][i]), (int) (2*masterKey[4][i]), (int) (2*masterKey[4][i]));
			painter.drawString("prepare to be a bit trippy in the campaign for LSD bot", 0, 0);
		}
		//this next chunk prints out the minimum risk movement test squares
		double mX = this.getX();
		double mY = this.getY();
		painter.setColor(java.awt.Color.GREEN);
		for (int j = 0;j < 36; j++)
		{
			int printX = (int) (mX+testCard[j][2]);
			int printY = (int) (mY+testCard[j][3]);
			if (testScore[j] == 0)
			{
				painter.setColor(java.awt.Color.CYAN); //for 0, best move
			}
			else if (testScore[j] <= 2)
			{
				painter.setColor(java.awt.Color.GREEN); //green (go) for less than 2 waves, or robots nearby 
			}
			else if (testScore[j] <=6)
			{
				painter.setColor(java.awt.Color.YELLOW); //yellow (warning) for less than 6 waves, or robots nearby
			}
			else if (testScore[j] <=10)
			{
				painter.setColor(java.awt.Color.ORANGE); //orange warning for less than 10 waves, or robots nearby
			}
			else
			{
				painter.setColor(java.awt.Color.RED); //red for bad move, most common
			}
			painter.drawRect(printX, printY, 5, 5); //draw the rectangle with the set color
		}
			}
}
