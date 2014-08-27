package cwr;

import java.awt.Color;
import java.awt.Graphics2D;

public class Legs extends Brain implements Paintable
{
	double moveEndTheta;
	double moveEndDistance;
	double randomW;
	double randomH;
	cwruBase robot;
	
	int pointer_x; //pointers for where the robot is pointing at range 100
	int pointer_y;
	int current_x;
	int current_y;

	public Legs(LifeBox source, cwruBase cwruBase) 
	{
		super(source);
		robot = cwruBase;
	}
	//Legs is the brain that controls movement of the robot
	public void process()
	{
		long processTime = System.currentTimeMillis();
		if (robot_near_wall())
		{
			double width = source.battlefield_width;
			double heigh = source.battlefield_height;
			randomW = (width/3)+Math.random()*(width/3);
			randomH = (heigh/3)+Math.random()*(heigh/3);
		}
		moveEndTheta = choose_radians_to_turn_left(); //calculate desired theta
		moveEndDistance = choose_distance_to_move(); //calculate desired distance
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("MOV calc time (millis):"+totalTime);
	}
	public final void set()
	{
		current_x = (int) (robot.getX()+100*Math.cos(-robot.getHeadingRadians()+Math.PI/2));
		current_y = (int) (robot.getY()+100*Math.sin(-robot.getHeadingRadians()+Math.PI/2));;
		reduceTheta();
		pointer_x = (int) (robot.getX()+100*Math.cos(moveEndTheta-robot.getHeadingRadians()+Math.PI/2));
		pointer_y = (int) (robot.getY()+100*Math.sin(moveEndTheta-robot.getHeadingRadians()+Math.PI/2));;
		robot.setTurnLeftRadians(moveEndTheta);
		robot.setAhead(moveEndDistance);
	}
	public void reduceTheta()
	{
		moveEndTheta = moveEndTheta%(Math.PI*2);
		if (moveEndTheta > Math.PI)
		{
			double delta = moveEndTheta-Math.PI;
			moveEndTheta = delta-Math.PI;
		}
		if (moveEndTheta < -1*Math.PI)
		{
			double delta = -moveEndTheta-Math.PI;
			moveEndTheta = delta-Math.PI;
		}
	}
	public double choose_radians_to_turn_left()
	{
		double radian_result = 0;
		//calculate the radians to turn left
		if (robot_near_wall())//if the robot is near a wall
		{
			//move to center
			radian_result = direction_to_point(robot.getX(), 
					robot.getY(), randomW, randomH);
		}
		else if (source.getRobot().getTurnRemainingRadians()>.349) //greater than one move turn
		{
			//if it was turning, let it keep turning
			radian_result = source.getRobot().getTurnRemainingRadians();
		}
		else if (source.getRobot().getDistanceRemaining()>8) //greater than one move turn
		{
			//if it is still moving from a prior setAhead()
			radian_result = 0;
		}
		else
		{
			//do the normal thing
			//if the robot isn't near an edge, 
			//	it isn't already turning, 
			//	and it isn't moving forward
			double random_radian = Math.random()*2*Math.PI-Math.PI;
			radian_result = random_radian;
		}
		return radian_result;
	}
	public double choose_distance_to_move()
	{
		double distance_result = 0;
		//calculate the radians to turn left
		if (robot_near_wall())//if the robot is near a wall
		{
			//move to center
			distance_result = distance_to_point(robot.getX(), robot.getY(),
					randomW, randomH);
		}
		else if (source.getRobot().getDistanceRemaining()>8) //greater than one move turn
		{
			//if it is still moving from a prior setAhead()
			distance_result = source.getRobot().getDistanceRemaining();
		}
		else if (source.getRobot().getTurnRemainingRadians()>.349) //greater than one move turn
		{
			//if it was turning, let it keep turning
			distance_result = 0;
		}
		else
		{
			//do the normal thing
			//if the robot isn't near an edge, 
			//	it isn't already turning, 
			//	and it isn't moving forward
			double random_radian = Math.random()*2*Math.PI-Math.PI;
			distance_result = random_radian;
		}
		return distance_result;
	}
	public boolean robot_near_wall()
	{
		boolean near_wall = false;
		cwruBase source_bot = robot;
		double xCoord = source_bot.cx;
		double yCoord = source_bot.cy;
		if (xCoord<50 ||  //if the robot is close to the right or left
				xCoord>(source.battlefield_width-50))
		{
			near_wall =  true;
		}
		if (yCoord<50 ||  //if the robot is close to the top or bottom
				yCoord>(source.battlefield_height-50))
		{
			near_wall =  true;
		}
		return near_wall;
	}
	public double direction_to_point(double px,double py, double mx, double my)
	{
		double radians_return = 0;
		//adjust for rotated coordinate system
		radians_return = (-Math.atan2(py-my, px-mx)+Math.PI/2)%(2*Math.PI);
		return radians_return;
	}
	public double distance_to_point(double px, double py, double mx, double my)
	{
		return Brain.distance(px, py, mx, my);
	}
	public void onPaint(Graphics2D g) 
	{
		System.out.println("Painting Legs graphics");
		g.setColor(Color.ORANGE);
		g.drawLine((int) robot.getX(), (int) robot.getY(), pointer_x, pointer_y);
		g.setColor(Color.RED);
		g.drawLine((int) robot.getX(), (int) robot.getY(), current_x, current_y);
	}
}
