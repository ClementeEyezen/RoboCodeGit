package wcb;

/*
 * a basic robot that just moves randomly while not moving into walls. 
 * It factors in the walls by moving towards the center whenever it is approx. 2 lengths from the wall
 * It also has a linear gun, suprise!
 */

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class WcbRandomNoWall extends AdvancedRobot
{
	public static double SuggestedTrueGunBearing;
	public static double relativeX;
	public static double relativeY;
	public static double Edistance;
	public static double X;
	public static double Y;
	public static double directionX;
	public static double directionY;
	
	public void run()
	{
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnRadarRight(360);
		System.out.println("running!!");
		double direction = (Math.random()*360)-180;
		double speed = (Math.random()*8);
		System.out.println("direction chosen");
		setTurnRight(direction);
		setAhead(speed);
		avoidWall();
		execute();
		run();
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
	public void onScannedRobot(ScannedRobotEvent vibe) 
	{
		double EnemyEnergy = vibe.getEnergy();
		double MyEnergy = getEnergy();
		double Bearing = vibe.getBearing();
		double Distance = vibe.getDistance();
		LinearFire(vibe.getBearing(),vibe.getVelocity(),vibe.getDistance(), vibe.getHeading(), getHeading());
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
		double X = enemyLocationX;
		double Y = enemyLocationY;
		double Espeed = enemyVelocity;
		double Edirection = enemyHeading;
		double Edistance = enemyDistance;
		double bulletspeed = 20-3*1.5;
		double bullettime = (Edistance/bulletspeed)+1;
		double EDistanceCovered=Espeed*bullettime;
		double directionX = X+(EDistanceCovered*(Math.cos(-(rad(Edirection)-(Math.PI/2)))));
		double directionY = Y+(EDistanceCovered*(Math.sin(-(rad(Edirection)-(Math.PI/2)))));
		SuggestedTrueGunBearing = deg(Math.atan(directionX/directionY));
	}
	
	private void LinPointGen(double bearing, double distance)
	{
		relativeX = distance*Math.cos(-(rad(bearing)-(Math.PI/2)));
		relativeY = distance*Math.sin(-(rad(bearing)-(Math.PI/2)));
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
}
