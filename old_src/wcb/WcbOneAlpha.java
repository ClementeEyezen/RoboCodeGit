package wcb;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
/**
 * @author Buck
 * My first robot's first official form
 */
/*
 * OneAlpha is a select form of One that takes only completed modules, and is more or less a final project
 * It takes the Continuous Mode Search Radar, HeadOn/LockOn Gun, ConstantDistance Movement
 */
/*
 * Continuous Mode Search Radar continuously searches around the robot in max size 45 degree arcs
 * Effective for finding bots, but then usurped by LockOn function of Gun
 */
/*
 * HeadOn/LockOn Gun moves independently of the robot, and aims and fires at the noted location with constant power
 * When the Radar hits another robot, the gun begins to swing towards that robot's location
 * The radar is kept alternating left and right, instead of a full sweep, so the robot continuously updates its targeting info
 * Once the gun is aligned within one degree of the intended target, the gun fires
 * Effective at shorter ranges, with slower robots, or stopped/disabled robots
 */
/*
 * ConstantDistance Movement moves the robot on an axis directly to and from the most recently scanned robot
 * it looks to keep a set distance from the robot
 * then it adjusts the distance slightly to account for relative energy levels
 * it approaches the other robot when it has higher energy, making each shot more accurate and less wasteful
 * it moves away from a player with higher energy, making their shots less likely to hit
 * It isn't really effective at dodging, because it stays facing straight into the other robot
 */

public class WcbOneAlpha extends AdvancedRobot
{
	//All Variables initiated
	public static int callR = 0; 
	public static int callG = 0;
	public static int callM = 0;
	public static int GunMode = 1;
	public static int HeadOn = 1;
	public static double SafeDistance = 200;
	public static double SafeMulti = 1.5;
	public static double Distance = 0;
	public static double Bearing = 0;
	public static double EnemyEnergy = 0;
	public static double MyEnergy = 0;

	//Methods
	public void run() 
	{
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		funcRadar();
		funcMove();
		execute();
		run();
	}
	private void funcRadar() 
	{
		if (getRadarTurnRemaining()==0) 
		{
			setTurnRadarRight(45);
		}

	}
	private void funcMove() 
	{
		MoveUp(Bearing, Distance, EnemyEnergy, MyEnergy);
	}
	private void MoveUp(double bearing, double distance, double enemyEnergy, double myEnergy) 
	{
		setTurnLeft(-bearing);
		if (distance>(SafeDistance)*(enemyEnergy*SafeMulti/myEnergy))
		{
			setAhead(8);
		}
		else
		{
			setBack(4);
		}
	}
	public void onScannedRobot(ScannedRobotEvent vibe) 
	{
		EnemyEnergy = vibe.getEnergy();
		MyEnergy = getEnergy();
		Bearing = vibe.getBearing();
		Distance = vibe.getDistance();
		MoveUp(Bearing,EnemyEnergy,MyEnergy,Distance);
		HeadFire(vibe.getBearing());
	}
	private void HeadFire(double EnemyBearing) 
	{
		double CurrentEnemyBearing = EnemyBearing;
		double CurrentGunBearing = getGunHeading();
		double CurrentRobotBearing = getHeading();
		double diff = CurrentGunBearing - (CurrentEnemyBearing+CurrentRobotBearing);
		
		if (getRadarHeading()<(CurrentEnemyBearing+CurrentRobotBearing)) 
		{
			setTurnRadarRight(45);
		}
		else
		{
			setTurnRadarLeft(45);
		}
		
		if (diff>=360) diff=diff-360;
		if (diff>=180) diff=-(360-diff);
		if (diff<=-360) diff=diff+360;
		if (diff<=-180) diff=-(-360-diff);
		
		setTurnGunLeft(diff);
		
		if ((Math.abs(diff)<0.2) && (getGunHeat() == 0))
		{
			fireBullet(2);
		}
	}
}
