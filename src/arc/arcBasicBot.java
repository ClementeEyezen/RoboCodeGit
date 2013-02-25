package arc;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class ArcBasicBot extends AdvancedRobot
{
	DataBox dan;
	MoveBrain mary;
	GunBrain gary;
	RadarBrain rarely;
	Point driveTo;
	boolean pointControl;
	double driveBearing;
	double driveDistance;
	boolean polarControl;
	public void run()
	{
		//make the ports move independently
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		//create new brains
		dan = new DataBox(this);
		mary = new MoveBrain(dan);
		gary = new GunBrain(dan);
		rarely = new RadarBrain(dan);
		//begin run through of calculations
		while (true)
		{
			mary.process();
			gary.process();
			rarely.process();
			execute();
		}
	}
	public void onScannedRobot(ScannedRobotEvent sre)
	{
		dan.scanEvent(sre);
	}
	public void moveGunTo(double theta)
	{
		//move the gun to the heading theta
		double current = this.getGunHeading();
		double desire = theta;
		this.setTurnGunLeftRadians(desire-current);
	}
	public void setGunFire(double power)
	{
		//set the gun to fire a certain amount of steps later
		this.setFireBullet(gary.getPower());
	}
	public void moveRadarTo(double theta)
	{
		//move the gun to the heading theta
		double current = this.getRadarHeading();
		double desire = theta;
		this.setTurnRadarLeftRadians(desire-current);
	}
	public void driveRobotTo(Point p)
	{
		//move the robot from its current position to a point on the map, scaled from the upper left hand corner of the map
		pointControl = true;
		if (p.distance(this.selfPoint())<=2)
		{
			//if the robot is within 2 pixels, it is close enough and does nothing
			pointControl = false;
		}
		else
		{
			double dx = p.getPoint()[0]-this.selfPoint().getPoint()[0];
			double dy = p.getPoint()[1]-this.selfPoint().getPoint()[1];
			if (dy == 0)
			{
				if (dx >= 0)
				{
					this.turnRobotTo(Math.PI/2);
				}
				else
				{
					this.turnRobotTo(3*Math.PI/2);
				}
			}
			else if (dy>0)
			{
				if (dx == 0)
				{
					this.turnRobotTo(0.0);
				}
				else if (dx > 0)
				{
					double direction = Math.atan2(dx, dy);
					this.turnRobotTo(direction);
				}
				else
				{
					double direction = Math.atan2(dx, dy);
					this.turnRobotTo(direction);
				}
			}
			else
			{
				//dy<0
				if (dx == 0)
				{
					this.turnRobotTo(Math.PI);
				}
				else if (dx > 0)
				{
					double direction = Math.atan2(dx, dy);
					this.turnRobotTo(direction);
				}
				else
				{
					double direction = Math.atan2(dx, dy);
					this.turnRobotTo(direction);
				}
			}
			double d = p.distance(this.selfPoint());
			this.advanceRobot(d);
		}
	}
	public void driveRobotTo(double direction, double distance)
	{
		turnRobotTo(direction);
		advanceRobot(distance);
	}
	public void turnRobotTo(double theta)
	{
		//move the robot to the heading theta in radians
		double current = this.getHeading();
		double desire = theta;
		this.setTurnLeftRadians(desire-current);
	}
	public void advanceRobot(double distance)
	{
		//move the robot this far forward in pixels
		this.setAhead(distance);
	}
	public Point selfPoint()
	{
		double[] non = new double[2];
		non[0] = this.getX();
		non[1] = this.getY();
		return new Point(non);
	}
}
