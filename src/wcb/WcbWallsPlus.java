package wcb;
//code is derived from  sample.Walls
//it started as a direct copy of the entire robot
//now it is walls plus buck's linear targeting jazz, which is relatively ineffective
/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *******************************************************************************/

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;


/**
 * Walls - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Moves around the outer edge with the gun facing in.
 */
public class WcbWallsPlus extends AdvancedRobot 
{

	boolean peek; // Don't turn if there's a robot there
	double moveAmountH; // How much to move up
	double moveAmountW; // How much to move across
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
	/**
	 * run: Move around the walls
	 */
	public void run() 
	{
		// Set colors
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.orange);
		setBulletColor(Color.cyan);
		setScanColor(Color.cyan);

		// Initialize moveAmount to the maximum possible width, height for this battlefield.
		moveAmountW = getBattleFieldWidth();
		moveAmountH = getBattleFieldHeight();
		// Initialize peek to false
		peek = false;

		// turnLeft to face a wall.
		// getHeading() % 90 means the remainder of
		// getHeading() divided by 90.
		turnLeft(getHeading() % 90);
		if (getHeading()==90)
		{
			ahead(moveAmountW-getX());
		}
		else if (getHeading()==180)
		{
			ahead(getY());
		}
		else if (getHeading()==270)
		{
			ahead(getX());
		}
		else
		{
			ahead(moveAmountH-getY());
		}
		
		// Turn the gun to turn right 90 degrees.
		peek = true;
		turnGunRight(90);
		turnRight(90);

		//right now, this loop only calls at corners
		while (true) {
			// Look before we turn when ahead() completes.
			peek = true;
			// Move up the wall
			System.out.println("I'm going:"+getHeading()+"and my gun it pointing"+getGunHeading());
			if (getHeading()==90)
			{
				ahead(moveAmountW-getX()-5); //move the distance to the next wall
			}
			else if (getHeading()==180)
			{
				ahead(getY()-5);
			}
			else if (getHeading()==270)
			{
				ahead(getX()-5);
			}
			else if (this.getHeading()==0)
			{
				ahead(moveAmountH-getY()-5);
			}
			// Don't look now
			peek = false;
			// Turn to the next wall, with gun locked
			System.out.println("I'm turning right");
			turnRight(90);
			funcGun();
			execute();
		}

	}

	private void funcGun() 
	{
		System.out.println("Gun Heat:"+getGunHeat()+"GunHeading:"+getGunHeading()+" Suggested:"+(SuggestedTrueGunBearing));
		if ((Math.abs(moveGunToHeading(SuggestedTrueGunBearing))<1) && (getGunHeat() == 0))
		{
			conserveFire();
		}
	}

	/**
	 * onHitRobot:  Move away a bit.
	 */
	public void onHitRobot(HitRobotEvent e) {
		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			ahead(100);
		}
	}

	/**
	 * onScannedRobot:  Fire!
	 */
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
	public void onScannedRobot(ScannedRobotEvent vibe) 
	{
		EnemyEnergy = vibe.getEnergy();
		MyEnergy = getEnergy();
		Bearing = vibe.getBearing();
		Distance = vibe.getDistance();
		System.out.println("LinearFire chosen");
		LinearFire(vibe.getBearing(),vibe.getVelocity(),vibe.getDistance(), vibe.getHeading(), getHeading());

	}
	private void LinearFire(double bearing, double velocity, double distance, double heading, double myHeading)
	{
		double actualBearing = myHeading+bearing;
		LinPointGen(actualBearing, distance);
		LinArrowGen(relativeX,relativeY,velocity,heading,distance);
		System.out.println("gun moving "+moveGunToHeading(SuggestedTrueGunBearing)+" to a suggestion of "+SuggestedTrueGunBearing);
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
		double bulletspeed = 20-3*1.5;
		double bullettime = (Edistance/bulletspeed)+1;
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
	private void conserveFire()
	{
		double powerChoice = 250/Edistance;
		setFireBullet(powerChoice);
		System.out.println("fired bullet power --> "+(powerChoice));
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
}
