package wrc;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class DataCatch extends AdvancedRobot
{
	public static int maxNumRobots = 5;
	public static int maxNumTurns = 50;
	public static String[] robotIndex = new String[maxNumRobots];
	public static String lockName = "i";
	public static double[][] eX = new double[maxNumRobots][maxNumTurns];
	public static double[][] eY = new double[maxNumRobots][maxNumTurns];
	public static double[][] eDirection = new double[maxNumRobots][maxNumTurns];
	public static double[][] eSpeed = new double[maxNumRobots][maxNumTurns];
	public static double[][] eBearing = new double[maxNumRobots][maxNumTurns];
	public static double[][] eDistance = new double[maxNumRobots][maxNumTurns];
	public static double[][] eEnergy = new double[maxNumRobots][maxNumTurns];
	public static double[][] eHeading = new double[maxNumRobots][maxNumTurns];
	public static double[][] eGunHeat = new double[maxNumRobots][maxNumTurns];
	public static double[][] eEnergyDrop = new double[maxNumRobots][maxNumTurns];
	public static double[][] eScanTime = new double[maxNumRobots][maxNumTurns];
	public static double[] mX = new double[maxNumTurns];
	public static double[] mY = new double[maxNumTurns];
	public static double[] mDirection = new double[maxNumTurns];
	public static double[] mSpeed = new double[maxNumTurns];
	public static double[] mEnergy = new double[maxNumTurns];
	public static double[] mHeading = new double[maxNumTurns];
	public static double[] mGunHeat = new double[maxNumTurns];
	public static double[] mEnergyDrop = new double[maxNumTurns];
	public static double[] moveRecieve = new double[2];
	public static double[] gunRecieve = new double[5];


	public void run()
	{
		System.out.println("DataCatch says: I'm just here for collecting data, but I'm also the base for the robot");
		System.out.println("I use the radar and onScannedRobot, and call other classes");
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		startCase();
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		MercatorProjection moveDecision = new MercatorProjection();
		StarlightScope gunDecision = new StarlightScope();
		execute();
		while (true)
		{
			onScannedSelf();
			//these are the mapping and targeting objects that take in all of the data the robot collects and outputs desired heading et. al.
			moveRecieve = moveDecision.mappingData(getIndex(lockName), eX, eY, eDirection, eSpeed, eBearing, eDistance, eEnergy, eHeading, eGunHeat, eEnergyDrop, 
					eScanTime, mX, mY, mDirection, mSpeed, mEnergy, mHeading, mGunHeat, mEnergyDrop);
			gunRecieve = gunDecision.aimBot(getIndex(lockName), ((eScanTime[getIndex(lockName)][0]-eScanTime[getIndex(lockName)][1])>0), eX, eY, eDirection, eSpeed, eBearing, eDistance, eEnergy, eHeading, eGunHeat, eEnergyDrop, 
					eScanTime, mX, mY, mDirection, mSpeed, mEnergy, mHeading, mGunHeat, mEnergyDrop);
			setAhead(moveRecieve[1]);
			//System.out.println("moveReceive angle = "+moveRecieve[0]+" from "+this.getHeading());
			//System.out.println("turntoAngle = "+turnToAngle(moveRecieve[0], this.getHeading()));
			setTurnLeft(turnToAngle(moveRecieve[0],this.getHeading()));
			//System.out.println("gunReceive angle = "+moveRecieve[0]+" from "+this.getGunHeading());
			//System.out.println("turntoAngle = "+turnToAngle(gunRecieve[0], this.getGunHeading()));
			setTurnGunLeft(turnToAngle(gunRecieve[0],this.getGunHeading()));
			if (this.getGunHeat() == 0)
			{
				setFire(gunRecieve[1]);
			}
			execute();
		}
	}
	public void startCase()
	{
		for (int i = 0;i<maxNumRobots;i++)
		{ //scan through robot settings, to set default values
			robotIndex[i] = "i";
			for (int j = 0;j<maxNumTurns;j++)
			{
				eX[i][j] = -1;
				eY[i][j] = -1;
				eDirection[i][j] = -1;
				eSpeed[i][j] = -1;
				eBearing[i][j] = -1;
				eDistance[i][j] = -1;
				eEnergy[i][j] = -1;
				eHeading[i][j] = -1;
				eGunHeat[i][j] = -1;
				eEnergyDrop[i][j] = -1;
				eScanTime[i][j] = -1;
				mX[j] = -1;
				mY[j] = -1;
				mSpeed[j] = -1;
				mHeading[j] = -1;
				mEnergy[j] = -1;
				mGunHeat[j] = -1;
				mEnergyDrop[j] = -1;
			}
		}
	}
	public void onScannedRobot(ScannedRobotEvent vida)
	{
		//System.out.println("begin scanned robot");
		if (lockName.equals("i")) lockName = vida.getName();
		String namer = vida.getName();
		int RoboIndex = getIndex(namer);
		advanceDataArray(RoboIndex);
		eX[RoboIndex][0] = getRobotX(vida.getDistance(),vida.getBearing()+this.getHeading());
		eY[RoboIndex][0] = getRobotY(vida.getDistance(),vida.getBearing()+this.getHeading());
		eDirection[RoboIndex][0] = vida.getBearing()+this.getHeading();
		eSpeed[RoboIndex][0] = vida.getVelocity();
		eBearing[RoboIndex][0] = vida.getBearing();
		eDistance[RoboIndex][0] = vida.getDistance();
		eEnergy[RoboIndex][0] = vida.getEnergy();
		eHeading[RoboIndex][0] = vida.getHeading();
		eEnergyDrop[RoboIndex][0] = eEnergy[RoboIndex][1]-eEnergy[RoboIndex][0];
		eScanTime[RoboIndex][0] = this.getTime();
		//System.out.println("time has changed? "+((eScanTime[RoboIndex][0]-eScanTime[RoboIndex][1])>0));
		//System.out.println("end scanned robot");
	}
	public void onRobotDeath(RobotDeathEvent murder)
	{
		lockName = "i";
	}
	public void onScannedSelf()
	{
		advanceDataArray(-1); //data advance for self
		mX[0] = this.getX();
		mY[0] = this.getY();
		mSpeed[0] = this.getVelocity();
		mEnergy[0] = this.getEnergy();
		mHeading[0] = this.getHeading();
		mGunHeat[0] = this.getGunHeat();
		mEnergyDrop[0] = mEnergy[1] - mEnergy[0];
	}
	public double getRobotX(double distance, double trueAngle)
	{
		double rotateAngle = -(trueAngle-90);
		double radRotAngle = Math.toRadians(rotateAngle);
		double X = distance*Math.cos(radRotAngle)+mX[0];
		return X;
	}
	public double getRobotY(double distance, double trueAngle)
	{
		double rotateAngle = -(trueAngle-90);
		double radRotAngle = Math.toRadians(rotateAngle);
		double Y = distance*Math.sin(radRotAngle)+mY[0];
		return Y;
	}
	public double turnToAngle(double dA, double cA)
	{ //returns the setTurnLeft value to achieve the desired value from the current location
		//desriedAngle and currentAngle
		double unCut = cA-dA;
		if (unCut >=360)
		{
			unCut = unCut-360;
		}
		if (unCut <= -360)
		{
			unCut = 360+unCut;
		}
		if (unCut >= 180)
		{
			unCut = -(360-unCut);
		}
		if (unCut <= -180)
		{
			unCut = -(360-unCut);
		}
		double diamond = unCut;
		return diamond;
	}
	public void advanceDataArray(int robotIndex)
	{
		if (robotIndex!=-1)
		{
			//System.out.println("data array advance in progres for robotIndex "+robotIndex);
			for (int i = maxNumTurns-1;i>0;i--)
			{
				eX[robotIndex][i] = eX[robotIndex][i-1];
				eY[robotIndex][i] = eY[robotIndex][i-1];
				eDirection[robotIndex][i] = eDirection[robotIndex][i-1];
				eSpeed[robotIndex][i] = eSpeed[robotIndex][i-1];
				eBearing[robotIndex][i] = eBearing[robotIndex][i-1];
				eDistance[robotIndex][i] = eDistance[robotIndex][i-1];
				eEnergy[robotIndex][i] = eEnergy[robotIndex][i-1];
				eHeading[robotIndex][i] = eHeading[robotIndex][i-1];
				eGunHeat[robotIndex][i] = eGunHeat[robotIndex][i-1];
				eEnergyDrop[robotIndex][i] = eEnergyDrop[robotIndex][i-1];
				eScanTime[robotIndex][i] = eScanTime[robotIndex][i-1];
			}
		}
		else
		{
			for (int j = maxNumTurns-1;j>0;j--)
			{
				mX[j] = mX[j-1];
				mY[j] = mY[j-1];
				mSpeed[j] = mSpeed[j-1];
				mHeading[j] = mHeading[j-1];
				mEnergy[j] = mEnergy[j-1];
				mGunHeat[j] = mGunHeat[j-1];
				mEnergyDrop[j] = mEnergyDrop[j-1];
			}
		}
	}
	public int getIndex(String name)
	{
		for (int i = 0;i<maxNumRobots;i++)
		{
			if (robotIndex[i].equals(name))
			{
				return i;
			}
			else if (robotIndex[i].equals("i"))
			{
				robotIndex[i] = name;
				return i;
			}
		}
		robotIndex[0] = name;
		return 0;
	}
	public void onHitRobotEvent (HitRobotEvent collide)
	{
		setAhead(-64);
		turnLeft(45);
	}
	public void onHitWallEvent (HitWallEvent collide)
	{
		setAhead(-8);
		turnLeft(-45);
	}
	public void onPaint(Graphics2D paint)
	{
		int squareZise = 5; //default square size
		for (int i = 0;i<maxNumRobots;i++)
		{ //scan through robots
			if (!robotIndex[i].equals("i"))
			{ //if the name is not blank 
				for (int k = 0; k+1<maxNumTurns;k++)
				{
					//draw lines connecting the robot's known points
					paint.setColor(java.awt.Color.GREEN);
					paint.drawLine((int) (eX[i][k]), (int) (eY[i][k]), (int) (eX[i][k+1]), (int) (eY[i][k+1]));
					paint.fillRect((int) eX[i][k],(int) eY[i][k], squareZise, squareZise);
					//draw my path
					paint.setColor(java.awt.Color.BLUE);
					paint.drawLine((int) (mX[k]), (int) (mY[k]), (int) (mX[k+1]), (int) (mY[k+1]));
					paint.fillRect((int) mX[k],(int) mY[k], squareZise, squareZise);
					//draw the scanned heading
					paint.setColor(java.awt.Color.CYAN);
					paint.drawLine((int) eX[i][0], (int) eY[i][0], (int) (eX[i][0]+100*Math.cos(Math.toRadians(-(eHeading[i][0]-90)))), 
							(int) (eY[i][0]+100*Math.sin(Math.toRadians(-(eHeading[i][0]-90)))));
					paint.drawString("eHeading", 0, 375);
					//draw the rotated heading the robot is going to use
					paint.setColor(java.awt.Color.ORANGE);
					paint.drawLine((int) eX[i][0], (int) eY[i][0], (int) (eX[i][0]+100*Math.cos(Math.toRadians(-(gunRecieve[4]-90)))) , 
							(int) (eY[i][0]+100*Math.sin(Math.toRadians(-(gunRecieve[4]-90)))));
					paint.drawString("eRotatedHeading", (int) eX[0][0]+100, (int) eY[0][0]+100);
				}
			}
		}
		paint.setColor(java.awt.Color.RED);
		paint.drawLine((int) mX[0], (int) mY[0], (int) (mX[0]+300*Math.cos(Math.toRadians(-(gunRecieve[0]-90)))), 
				(int) (mY[0]+300*Math.sin(Math.toRadians(-(gunRecieve[0]-90)))));
		paint.drawLine((int) eX[0][0], (int) eY[0][0], (int) (gunRecieve[2]), (int) (gunRecieve[3]));
	}
}
