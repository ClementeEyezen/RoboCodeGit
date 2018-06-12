package wcb;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import java.awt.*;

/*
 * keep arrays of all of this data for other robots, that's it.
 * Use data by other classes 
 */
public class WcbDataMiner extends AdvancedRobot
{	//these variables and methods are public
	// Reasoning: trying to make sure that this is modular code, so that data could be used by any bot in package, not just extensions
	public static int maxNumRobots = 4;
	public static int maxNumTurns = 30;
	public static String[] ScannedNameSet = new String[maxNumRobots]; //a list of the names
	public static double[][] energy = new double[maxNumRobots][maxNumTurns]; // a list of the robots energy, first set is robot index, second set is time stamps
	public static double[][] X = new double[maxNumRobots][maxNumTurns]; // a list of the robots X position, first set is robot index, second set is time stamps
	public static double[][] Y = new double[maxNumRobots][maxNumTurns]; //Y position
	public static double[][] speed = new double[maxNumRobots][maxNumTurns]; //velocity
	public static double[][] direction = new double[maxNumRobots][maxNumTurns]; // direction the robot is facing
	public static double[][] bearing = new double[maxNumRobots][maxNumTurns]; // direction from self to robot
	public static double[][] distance = new double[maxNumRobots][maxNumTurns]; //distance at time of scan
	public static double[][] time = new double[maxNumRobots][maxNumTurns]; //time of scan
	public static Color[] paintAssist = new Color[maxNumRobots]; //list of colors by number
	//public static double[][] bulletCircle = new double[8][maxNumTurns]; //centerX,centerY,startTimeMin,startTimeMax,speedMin,speedMax,radiusMin,radiusMax
	public static double[] energyDrop = new double[maxNumRobots]; //list of energy drop by robot, only most recent

	public void run() 
	{
		//run not used in this class, see Oscill/Oscar
	}
	public void onScannedRobot(ScannedRobotEvent vida)
	{
		for(int i=0;i<maxNumRobots; i++) //look through the names array
		{
			// if the robot name matches the value in the NameSet array, then i is the correct row/index for everything else
			if (vida.getName().equals(ScannedNameSet[i]))
			{
				//moves all the data arrays down one, leaves space for new info at the top
				advanceAllArrays(i);
				//new info put in at the top
				energy[i][0] = vida.getEnergy();
				X[i][0] = returnX(this.getX(),vida.getDistance(),vida.getBearingRadians(),this.getHeadingRadians());
				Y[i][0] = returnY(this.getY(),vida.getDistance(),vida.getBearingRadians(),this.getHeadingRadians());
				speed[i][0] = vida.getVelocity();
				direction[i][0] = vida.getHeading(); //true direction
				bearing[i][0] = vida.getBearing()+this.getHeading();
				distance[i][0] = vida.getDistance();
				time[i][0] = vida.getTime();
				break;
			}
			// if it reaches an empty spot without finding a match, it creates a new space by assigning the name
			else if (ScannedNameSet[i].equals("i"))
			{
				//set the robot name to that row
				ScannedNameSet[i] = vida.getName();
				break;
			}
			//it shouldn't get here, unless the names array is full
			else
			{
				System.out.println("this is another robot's name, not "+vida.getName());
			}
		}
		maintainCircles();
	}
	public void maintainCircles()
	{ //the name is deprocated, now it only reports energy drops by robot index
		advanceCircles();
		for (int i=0;i<maxNumRobots;i++)
		{
			energyDrop[i] = energy[i][1]-energy[i][0];
		}
	}
	public void startCase()
	{
		//in place of a constructor method, variable values et. al. are set here
		//everything moves independently
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		//setting my choice default values for all arrays
		for(int i = 0;i<maxNumRobots;i++)
		{
			//all names start out as ""
			ScannedNameSet[i] = "i";
			for(int k = 0;k<maxNumTurns;k++)
			{
				//default values for each array are set
				energy[i][k] = 999;
				X[i][k] = 100;
				Y[i][k] = 100;
				speed[i][k] = 0;
				direction[i][k] = 0;
				bearing[i][k] = 0;
				distance[i][k] = 100;
				time[i][k] = 0;
			}
			//allows the program to cycle through colors based on number instead of name (used in on paint)
			paintAssist[0] = Color.RED;
			paintAssist[1] = Color.BLACK;
			paintAssist[2] = Color.BLUE;
			paintAssist[3] = Color.CYAN;
			//add more to the above list
			/*paintAssist[4] = Color.GREEN;
			paintAssist[5] = Color.MAGENTA;
			paintAssist[6] = Color.ORANGE;
			paintAssist[7] = Color.PINK;
			paintAssist[8] = Color.WHITE;
			paintAssist[9] = Color.YELLOW;
			*/
		}
		System.out.println("All starting cases are set and ready to RUMBLE!");
	}
	public double returnX(double myX, double enemyDistance, double enemyBearingRadians,	double myHeadingRadians) 
	{
		//returns the X value of a (scanned) robot using trig
		double pi = Math.PI;
		double trueBearing = enemyBearingRadians+myHeadingRadians;
		double rotateBearing = -(trueBearing-pi/2);
		double relativeX = enemyDistance*(Math.cos(rotateBearing));
		double posiX = myX+relativeX;
		return posiX;
	}
	public double returnY(double myY, double enemyDistance, double enemyBearingRadians,	double myHeadingRadians) 
	{
		//returns the y value of a (scanned) robot using trig
		double pi = Math.PI;
		double trueBearing = enemyBearingRadians+myHeadingRadians;
		double rotateBearing = -(trueBearing-pi/2);
		double relativeX = enemyDistance*(Math.sin(rotateBearing));
		double posiX = myY+relativeX;
		return posiX;
	}
	public void advanceAllArrays(int robotIndex)
	{
		//advances all arrays to set up for data collection
		for (int k = maxNumTurns-1;k>0;k--)
		{
			energy[robotIndex][k]= energy[robotIndex][k-1];
			X[robotIndex][k] = X[robotIndex][k-1];
			Y[robotIndex][k] = Y[robotIndex][k-1];
			speed[robotIndex][k] = speed[robotIndex][k-1];
			direction[robotIndex][k] = direction[robotIndex][k-1];
			bearing[robotIndex][k] = bearing[robotIndex][k-1];
			distance[robotIndex][k] = distance[robotIndex][k-1];
			time[robotIndex][k] = time[robotIndex][k-1];
		}
	}
	public void advanceCircles()
	{
		for (int k = maxNumRobots-1;k>0;k--)
		{
			energyDrop[k]= 0;
		}
	}
	public double rad(double degrees)
	{ //essentially only renamed existing Java function for ease of use
		return Math.toRadians(degrees);
	}
	public double deg(double radians)
	{ //essentially only renamed existing Java function for ease of use
		return Math.toDegrees(radians);
	}
	public void onPaint(Graphics2D paint)
	{
		int squareZise = 5; //default square size
		for (int i = 0;i<maxNumRobots;i++)
		{ //scan through robots
			if (!ScannedNameSet[i].equals(""))
			{ //if the name is not blank 
				paint.setColor(paintAssist[i]);
				for (int k = 0; k+1<maxNumTurns;k++)
				{
					//draw lines connecting the robot's known points
					paint.drawLine((int) (X[i][k]), (int) (Y[i][k]), (int) (X[i][k+1]), (int) (Y[i][k+1]));
					paint.fillRect((int) X[i][k],(int) Y[i][k], squareZise, squareZise);
				}
			}
		}
	}
}
