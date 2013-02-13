package wcb;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;
import java.awt.*;

import robocode.control.events.BattleStartedEvent;

//notes to leave myself with
/*
 * I have trouble with "robot name" (score) [idea to solve]
 * Walls(9-1) [linear targeting], 
 * VelociRobot(10-0) [circular targeting], 
 * TrackFire(6-4) [defensive movement], 
 * Spinbot(3-7) [Circ targeting],
 */

/*
 * Sample robots in order of strength
 *  TODO BEAT TOP 5 1V1
 *  1. Walls 67 (L27-73)
 *  2. Spinbot 64 (L4-96)
 *  3. Crazy 45 (W86-14)
 *  4. TrackFire 43 (L36-64)
 *  5. RamFire 43 (W62-38)
 *  
 *  6. Tracker 42
 *  7. Fire 37
 *  8. VelociRobot 35
 *  9. MyFirstJuniorRobot 34
 * 10. Corners 31
 * 11. MyFirstRobot 31
 */

/**
 * @author Buck
 * My first robot
 */
/*
 * Goals for targeting (Radar+Gun)
 * COMPLETED HEAD ON TARGETING w/ locking mechanism
 * COMPLETED Linear Targeting
 * TODO Circular Targeting
 * TODO GuessFactor Targeting
 * TODO DC Targeting
 * TODO create a method that selects the most effective targeting for a given robot
 * TODO create a "black book" that stores former enemies and effectiveness data
 */
/*
 * Goals for movement
 * TODO Antigravity bot avoidance
 * TODO Minimum Risk plot-move
 * TODO Wave Surfing
 */
/*
 * Other Goals
 * TODO Multimode programming
 * TODO Learn to store data
 * TODO Between ticks
 * TODO Between rounds
 * TODO Between matches
 */
public class WcbOne extends AdvancedRobot
{
	//Variables below--------------------------------------------------------------------------------------
	//counter for the number of times that the method has been called
	public static int GetTest = 0;
	public static int StartWait = 8;
	//sets the various ints for GunMode choices
	public static int HeadOn = 1;
	public static int LinearOn = 2;
	public static int CircularOn = 3;
	public static int GuessFactorOn = 4;
	public static int DynamicClusterOn = 5;
	//if default GunMode to 0, it won't fire
	public static int GunMode = LinearOn;
	//sets desired distance for shooting at another robot
	public static double SafeDistance = 200;
	public static double SafeMulti = 1.5;
	//measures accuracy of firing
	public static double ShotCount = 1;
	public static double ShotHit = 0;
	//last captured data for target
	public static double Distance = 0;
	public static double Bearing = 0;
	public static double EnemyEnergy = 0;
	public static double MyEnergy = 0;
	public static double MyHeading;
	//Linear Targeting output data
	public static double SuggestField[] = new double[4];
	public static double SuggestedTrueGunBearing;
	public static double Edistance;
	public static double X; //scanned X (relative)
	public static double Y; //scanned Y (relative)
	public static double directionX;
	public static double directionY;
	public static double relativeX;
	public static double relativeY;
	//bot location storage module
	//first row is bot name
	//second row is y values (just one for now)
	//third row is x values (just one for now)
	public static int NumberOfBots=1;
	public static String StorArray[][][] = new String[NumberOfBots][2][2];
	//METHODS below----------------------------------------------------------------------------------------
	public void run() 
	{
		//System.out.println("the run method is called");
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		//System.out.println("turn independently+++++++++++");
		funcRadar();
		//System.out.println("funcRadar ends");
		funcGun();
		//System.out.println("funcGun ends");
		funcMove();
		//System.out.println("funcMove ends");
		//System.out.println("execute about to be called...");
		execute();
		//System.out.println("execute called");
		run();

	}
	private void funcRadar() 
	{
		//System.out.println("funcRadar called");
		//this class is called on run, and sets the default movement for every tick for the robot
		if (getTime()<StartWait)
		{
			//System.out.println("funcRadar tested getTime() less than 8");
			//this calls operations for the first 8 ticks/steps
			//right now, it merely counts the ticks, then increments the radar scan
			if (getRadarTurnRemaining()==0) 
			{
				setTurnRadarRight(45);
				//System.out.println("Radar turns right 45 from funcRadar");
			}
		}
		else
		{
			//System.out.println("funcRadar tested getTime() more than 8");
			if (getRadarTurnRemaining()==0)
			{
				setTurnRadarRight(45); 
				//System.out.println("Radar turns right 45 from funcRadar");
			}

			/* 
			 * this turns the radar right continuously, which can be interrupted by scanned robot
			 * (interrupted to lock-on scan a robot)
			 */
		}

	}
	private void funcGun() 
	{
		/* this class is called on run, and sets the default function for the gun
		 * the default operation for the gun is to wait for 8 ticks (8 calls to run from start)
		 * then it will look at data to choose a gun mode based on data?
		 * once an original gun mode is chosen, this class returns nothing
		 * because the gun mode is then changed, if needed, by the onFire() method
		 */
		//System.out.println("funcGun called");

		//this calls the chooseGunMode() method
		//chooseGunMode() sets an integer to a value that corresponds to a mode, and returns that integer
		//TODO create choose gun mode method
		//TODO program different gun modes
		//this method also shoots the guns of various methods by testing conditions
		if (GunMode==HeadOn)
		{
			if ((getGunHeading()-(Bearing+MyHeading))<0.2 && (getGunHeat() == 0))
			{
				conserveFire();
				//TODO call chooseGun() method
			}
		}
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
		/* 
		 * this calls the movement mode selector
		 * TODO make movement mode selector
		 */
		//System.out.println("funcMove called");
		MoveUp2(Bearing, Distance, EnemyEnergy, MyEnergy);

	}
	public void onScannedRobot(ScannedRobotEvent vibe) 
	{
		//System.out.println("onScannedRobot called");
		//this moves the robot to a short distance from its enemy
		EnemyEnergy = vibe.getEnergy();
		MyEnergy = getEnergy();
		Bearing = vibe.getBearing();
		Distance = vibe.getDistance();
		MoveUp2(Bearing,EnemyEnergy,MyEnergy,Distance);
		//this set of if statements determines the firing mechanisms based on preset 
		if (GunMode==0)
		{
			//default firing mechanism does not fire
		}
		else if (GunMode==HeadOn)
		{
			//System.out.println("HeadFire chosen");
			HeadFire(vibe.getBearing());
		}
		else if (GunMode==LinearOn)
		{
			System.out.println("LinearFire chosen");
			//System.out.println("scanned bearing ="+vibe.getBearing()+"scanned distance ="+vibe.getDistance());
			LinearFire(vibe.getBearing(),vibe.getVelocity(),vibe.getDistance(), vibe.getHeading(), getHeading());

		}
		else if (GunMode==CircularOn)
		{
			//CircularFireFire(vibe.getBearing(),vibe.getVelocity(),vibe.getDistance(), vibe.getHeading());
		}
		//need to figure out if already in the array, and yes or no, return the first value in which to write in
		int Row = whichRowInArray(vibe.getName());
		//then write name into the array, just to keep it up to date
		StorArray[Row][0][0] = vibe.getName();
		//then write the locations [relativeX+getX()] to the next parts of the array
		StorArray[Row][1][0] = relativeY+getY()+"";
		StorArray[Row][1][1] = relativeX+getX()+"";
	}
	private void MoveUp2(double bearing, double distance, double enemyEnergy, double myEnergy) 
	{
		setTurnLeft(-bearing);
		if (distance>(SafeDistance))//*(enemyEnergy*SafeMulti/myEnergy))
		{
			setTurnLeft(2);
			setAhead(3);
		}
		else
		{
			setBack(3);
			setTurnLeft(-2);
		}
	}
	private void HeadFire(double EnemyBearing) 
	{
		//Head on Firing with Targeting
		//System.out.println("HeadFire called");
		//double CurrentGunBearing = getGunHeading();
		double CurrentEnemyBearing = EnemyBearing; //with relation to bot
		double CurrentGunBearing = getGunHeading();
		MyHeading = getHeading();
		//System.out.println("Current Enemy True Bearing:"+(CurrentEnemyBearing+CurrentRobotBearing));
		//System.out.println("Current Gun True Bearing"+CurrentGunBearing);
		//System.out.println("Current Difference:"+(CurrentGunBearing - (CurrentEnemyBearing+CurrentRobotBearing)));

		//set the Radar Direction
		if (getRadarHeading()<(CurrentEnemyBearing+MyHeading)) 
		{
			setTurnRadarRight(45);
			//System.out.println("Radar turns HeadFire tracking right"+45);
		}
		else
		{
			setTurnRadarLeft(45);
			//System.out.println("Radar turns HeadFire tracking left"+45);
		}
		double diff = CurrentGunBearing - (CurrentEnemyBearing+MyHeading);
		if (diff>=360) diff=diff-360;
		if (diff>=180) diff=-(360-diff);
		if (diff<=-360) diff=diff+360;
		if (diff<=-180) diff=-(-360-diff);
		//the angle between the gun and the robot scanned is now stored as CurrentDifference
		setTurnGunLeft(diff);
		//System.out.println("Gun turns "+diff);
		//then the robot is now turning the gun towards the scanned heading
		//System.out.println("Do I fire?");
		if ((Math.abs(diff)<0.2) && (getGunHeat() == 0))
		{
			//once it gets to the scanned heading, it fires
			//System.out.println("Yes I do fire!"+" Gun Heat:"+getGunHeat());
			conserveFire();
			ShotCount++;
			System.out.println("accuracy%:"+(100*(ShotHit/ShotCount)));
			System.out.println("Hit:"+ShotHit+" / "+"Shot"+ShotCount);
			//TODO call chooseGun() method
		}

	}
	public void onBulletHit(BulletHitEvent ver)
	{
		ShotHit++;
		System.out.println("accuracy%:"+(100*(ShotHit/ShotCount)));
		System.out.println("Hit:"+ShotHit+" / "+"Shot"+ShotCount);
	}
	private void LinearFire(double bearing, double velocity, double distance, double heading, double myHeading)
	{
		double actualBearing = myHeading+bearing;
		//System.out.println("Linear Fire bearing ="+actualBearing+" distance ="+distance);
		LinPointGen(actualBearing, distance);
		LinArrowGen(relativeX,relativeY,velocity,heading,distance);
		System.out.println("gun moving "+moveGunToHeading(SuggestedTrueGunBearing)+" to a suggestion of "+SuggestedTrueGunBearing);
		setTurnGunLeft(moveGunToHeading(SuggestedTrueGunBearing));

		//locking mechanism
		if (getRadarHeading()<SuggestedTrueGunBearing) 
		{
			setTurnRadarRight(45);
			//System.out.println("Radar turns HeadFire tracking right"+45);
		}
		else
		{
			setTurnRadarLeft(45);
			//System.out.println("Radar turns HeadFire tracking left"+45);
		}

		if ((Math.abs(moveGunToHeading(SuggestedTrueGunBearing))<1) && (getGunHeat() == 0))
		{
			//once it gets to suggested heading, it fires
			conserveFire();
		}
	}
	private void LinArrowGen(double enemyLocationX, double enemyLocationY, double enemyVelocity, double enemyHeading, double enemyDistance)
	{
		X = enemyLocationX; //relative
		Y = enemyLocationY; //relative
		double Espeed = enemyVelocity;
		double Edirection = enemyHeading; //true
		Edistance = enemyDistance;
		//System.out.println("X:"+X+" Y:"+Y+" Espeed:"+Espeed+" Edirection:"+Edirection+" Edistance:"+Edistance);
		double bulletspeed = 20-3*1.5;
		double bullettime = (Edistance/bulletspeed)+1;
		//System.out.println("bulletspeed:"+bulletspeed+" bullettime:"+bullettime);
		double EDistanceCovered=Espeed*bullettime;
		directionX = X+(EDistanceCovered*(Math.cos(-(rad(Edirection)-(Math.PI/2)))));
		directionY = Y+(EDistanceCovered*(Math.sin(-(rad(Edirection)-(Math.PI/2)))));
		//System.out.println("LinArrowGen target X, target Y "+(directionX+getX())+","+(directionY+getY()));
		SuggestedTrueGunBearing = deg(Math.atan(directionX/directionY));
		//System.out.println("LinArrowGen target theta, target distance "+SuggestedTrueGunBearing+","+Edistance);
		//System.out.println("Suggested True Gun Bearing="+SuggestedTrueGunBearing);
		//double SuggestField[] = new double[4];
		//true bearing, distance, relative X, relative Y
		//SuggestField[0] = SuggestedTrueGunBearing;
		//SuggestField[1] = Edistance;
		//SuggestField[2] = directionX;
		//SuggestField[3] = directionY;
		//System.out.println("Multidata output attempted");
	}
	private void LinPointGen(double bearing, double distance)
	{
		//System.out.println("LinPointGen bearing, distance:"+bearing+","+distance);
		relativeX = distance*Math.cos(-(rad(bearing)-(Math.PI/2)));
		relativeY = distance*Math.sin(-(rad(bearing)-(Math.PI/2)));
		//System.out.println("LinPointGen actual X, actual Y "+(relativeX+getX())+","+(relativeY+getY()));
	}
	private void chooseGun()
	{
		
	}
	private double rad(double input)
	{
		//converts degrees to radians
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
	private void conserveFire()
	{
		double powerChoice = 250/Edistance;
		setFireBullet(powerChoice);
		System.out.println("fired bullet power --> "+(powerChoice));
	}
	public void onPaint(Graphics2D g)
	{
		g.setColor(java.awt.Color.RED);
		//draw a distance circle
		g.drawOval(((int) (getX()-Edistance)),(int) (getY()-Edistance),(int) (2*Edistance),(int) (2*Edistance));
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
	/* public void onBattleStarted(BattleStartedEvent goforgold)
	{
		//NumberOfBots = goforgold.getRobotsCount();
		//NumberOfBots = 1;
	} */
	public int whichRowInArray(String input)
	{
		for (int i = 0;i<NumberOfBots;i++)
		{
			if (StorArray[i][0][0].equals(input))
			{
				return i;
			}
			if (StorArray[i][0][0].equals(""))
			{
				return i;
			}
		}
		System.out.println("Error Message no slot found for the data to be stored, returned first slot");
		return 0;
	}
}
