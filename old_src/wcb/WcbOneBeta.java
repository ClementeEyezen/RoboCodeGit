package wcb;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * @author Buck
 * My first robot's second official form
 */
/*
 * OneBeta is a select form of One that takes only completed modules, and is more or less a final project
 * It takes the Continuous Mode Search Radar, LinearOn/LockOn Gun, DistanceFactorCircular Movement
 */
/*
 * Continuous Mode Search Radar continuously searches around the robot in max size 45 degree arcs
 * Effective for finding bots, but then usurped by LockOn function of Gun
 */
/*
 * LinearOn/LockOn Gun moves independently of the robot, and aims and fires at the predicted location with varied power
 * When the Radar hits another robot, the gun begins to swing towards that robot's location plus its current heading*bullet traveltime
 * The radar is kept alternating left and right, instead of a full sweep, so the robot continuously updates its targeting info
 * Once the gun is aligned within one degree of the predicted location, the gun fires
 * Effective at medium ranges, with slower or predictable robots, or stopped/disabled robots
 */
/*
 * ConstantDistance Movement moves the robot in a long circle
 * then it adjusts the turn direction based on distance
 * It is somewhat effective at dodging headon fire, because it moves around, 
 * and it is rarely in the same place on the circle from when it was fired on
 */

public class WcbOneBeta extends AdvancedRobot
{
	public static int LinearOn = 2;
	public static int GunMode = LinearOn;
	public static double SafeDistance = 200;
	public static double Distance = 0;
	public static double Bearing = 0;
	public static double EnemyEnergy = 0;
	public static double MyEnergy = 0;
	public static double MyHeading;
	public static double SuggestedTrueGunBearing;
	public static double Edistance;
	public static double X;
	public static double Y;
	public static double directionX;
	public static double directionY;
	public static double relativeX;
	public static double relativeY;
	public static double bulletspeed;
	public static double bullettime;

	public void run() 
	{
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		funcRadar();
		funcGun();
		funcMove();
		avoidWall();
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
	private void funcGun() 
	{
		if (GunMode==LinearOn)
		{
			System.out.println("Gun Heat:"+getGunHeat()+"GunHeading:"+getGunHeading()+" Suggested:"+(SuggestedTrueGunBearing));
			if ((Math.abs(moveGunToHeading(SuggestedTrueGunBearing))<1) && (getGunHeat() == 0))
			{
				conserveFire();
			}
		}
	}
	private void funcMove() 
	{
		MoveUp2(Bearing, Distance, EnemyEnergy, MyEnergy);
	}
	public void onScannedRobot(ScannedRobotEvent vibe) 
	{
		EnemyEnergy = vibe.getEnergy();
		MyEnergy = getEnergy();
		Bearing = vibe.getBearing();
		Distance = vibe.getDistance();
		MoveUp2(Bearing,EnemyEnergy,MyEnergy,Distance);
		if (GunMode==0)
		{
		}
		else if (GunMode==LinearOn)
		{
			System.out.println("LinearFire chosen");
			LinearFire(vibe.getBearing(),vibe.getVelocity(),vibe.getDistance(), vibe.getHeading(), getHeading());
		}
	}
	private void LinearFire(double bearing, double velocity, double distance, double heading, double myHeading)
	{
		double actualBearing = myHeading+bearing;
		LinPointGen(actualBearing, distance);
		LinArrowGen(relativeX,relativeY,velocity,heading,distance);
		System.out.println("gun moving "+moveGunToHeading(SuggestedTrueGunBearing)+" to a suggestion of "+SuggestedTrueGunBearing);
		setTurnGunLeft(moveGunToHeading(SuggestedTrueGunBearing));
		if (getRadarHeading()<SuggestedTrueGunBearing) 
		{
			setTurnRadarRight(45);
		}
		else
		{
			setTurnRadarLeft(45);
		}
		if ((Math.abs(moveGunToHeading(SuggestedTrueGunBearing))<1) && (getGunHeat() == 0))
		{
			conserveFire();
		}
	}
	private void LinArrowGen(double enemyLocationX, double enemyLocationY, double enemyVelocity, double enemyHeading, double enemyDistance)
	{
		X = enemyLocationX;
		Y = enemyLocationY;
		double Espeed = enemyVelocity;
		double Edirection = enemyHeading;
		Edistance = enemyDistance;
		bulletspeed = 20-3*Math.min(3, (250/Edistance));
		bullettime = (Edistance/bulletspeed);
		double EDistanceCovered=Espeed*bullettime;
		directionX = X+(EDistanceCovered*(Math.cos(-(rad(Edirection)-(Math.PI/2)))));
		directionY = Y+(EDistanceCovered*(Math.sin(-(rad(Edirection)-(Math.PI/2)))));
		SuggestedTrueGunBearing = deg(Math.atan(directionX/directionY));
	}
	private void LinPointGen(double bearing, double distance)
	{
		relativeX = distance*Math.cos(-(rad(bearing)-(Math.PI/2)));
		relativeY = distance*Math.sin(-(rad(bearing)-(Math.PI/2)));
	}
	private void MoveUp2(double bearing, double distance, double enemyEnergy, double myEnergy) 
	{
		double turnRate = 2; // it takes 360/2, 180 steps to get around circle
		double stepsToFarcyde = 180/turnRate;
		if (stepsToFarcyde != bullettime)
		{
			turnRate = 180/bullettime;
		}
		setTurnLeft(-bearing);
		setAhead(4);
		double ranmomm = Math.random();
		if (distance>(SafeDistance) || ranmomm<=.5)
		{
			setTurnLeft(turnRate);
			setAhead(8);
		}
		else
		{
			setBack(8);
			setTurnLeft(-turnRate);
		}
	}
	private void conserveFire()
	{
		double powerChoice = 250/Edistance;
		setFireBullet(powerChoice);
		System.out.println("fired bullet power --> "+(powerChoice));
	}
	private double moveGunToHeading(double desired)
	{
		double delta = getGunHeading()-desired;
		if (directionY<=0) delta=delta-180;
		if (delta>=360) delta=delta-360;
		if (delta<=-360) delta=delta+360;
		if (delta>=180) delta=(360-delta);
		if (delta<=-180) delta=(-360-delta);
		System.out.println("delta ="+delta);
		return delta;
	}
	public void avoidWall()
	{
		if ((getX()>getBattleFieldWidth()-50) || (getX()<50))
		{
			System.out.println("move center called");
			moveCenter();
		}
		else if ((getY()>getBattleFieldHeight()-50) || (getY()<50))
		{
			System.out.println("move center called");
			moveCenter();
		}
	}	
	public void moveCenter()
	{
		double start = getHeading(); 
		double desired = 0;
		double X = getX();
		double Y = getY();
		if (X>getBattleFieldWidth()/2 && Y>getBattleFieldHeight()/2) desired = -135;
		if (X<getBattleFieldWidth()/2 && Y>getBattleFieldHeight()/2) desired = 135;
		if (X>getBattleFieldWidth()/2 && Y<getBattleFieldHeight()/2) desired = -45;
		if (X<getBattleFieldWidth()/2 && Y<getBattleFieldHeight()/2) desired = 45;
		turnRight(desired-start);
		setAhead(50);
	}
	private double rad(double input)
	{
		double pi = 3.14159;
		double output = (input/180)*pi;
		return output;
	}
	private double deg(double input)
	{
		double pi = 3.14159;
		double output = (input/pi)*180;
		return output;
	}
	public void onPaint(Graphics2D g)
	{
		g.setColor(java.awt.Color.RED);
		//draw a distance circle
		g.drawOval(((int) (getX()-Edistance)),(int) (getY()-Edistance),(int) (2*Edistance),(int) (2*Edistance));
		//draw a safe distance circle on enemy bot
		g.drawOval(((int) (relativeX+getX()-200)),(int) (relativeY+getY()-200),(int) (2*200),(int) (2*200));
		//draw a line from self to other robot
		g.drawLine((int) (getX()), (int) (getY()), (int) (relativeX+getX()), (int) (relativeY+getY()));
		//draw where it thinks the other robot is
		g.fillRect((int) (relativeX+getX()), (int) (relativeY+getY()), 10, 10);
		g.setColor(java.awt.Color.GREEN);
		//draw where it think the robot is going
		g.fillRect((int) (directionX+getX()), (int) (directionY+getY()), 10, 10);
		//draw a line to its expected position
		g.drawLine((int) (getX()), (int) (getY()), (int) (directionX+getX()), (int) (directionY+getY()));
		//line from actual to predicted
		g.drawLine((int) (directionX+getX()), (int) (directionY+getY()), (int) (relativeX+getX()), (int) (relativeY+getY()));
		//System.out.println("I just drew something");
	}
}
